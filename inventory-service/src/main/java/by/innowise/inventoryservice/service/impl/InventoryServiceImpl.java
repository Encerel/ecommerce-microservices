package by.innowise.inventoryservice.service.impl;

import by.innowise.inventoryservice.exception.InventoryNotFoundException;
import by.innowise.inventoryservice.exception.ProductNotFoundException;
import by.innowise.inventoryservice.exception.ProductOutStockException;
import by.innowise.inventoryservice.model.api.*;
import by.innowise.inventoryservice.model.entity.Inventory;
import by.innowise.inventoryservice.model.entity.InventoryItem;
import by.innowise.inventoryservice.model.entity.ProductStatus;
import by.innowise.inventoryservice.repository.InventoryItemRepository;
import by.innowise.inventoryservice.repository.InventoryRepository;
import by.innowise.inventoryservice.service.InventoryService;
import by.innowise.inventoryservice.web.client.ProductClient;
import by.innowise.inventoryservice.web.payload.ServerResponse;
import by.innowise.inventoryservice.web.payload.response.MessageServerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static by.innowise.inventoryservice.constant.Message.*;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final ProductClient productClient;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public List<OrderItem> takeProductsFromInventory(List<TakenProductQuantity> products) {
        log.info("Starting takeProductsFromInventory for {} products", products.size());

        List<OrderItem> orderItems = new ArrayList<>();
        List<OutStockProduct> outStockProducts = new ArrayList<>();
        List<InventoryItem> updatedInventoryItems = new ArrayList<>();

        for (TakenProductQuantity product : products) {
            int productId = product.getProductId();
            int requestedQuantity = product.getQuantity();
            log.info("Processing product with id {} and requested quantity {}", productId, requestedQuantity);

            List<InventoryItem> foundItems = inventoryItemRepository.findByProductId(productId);

            if (foundItems.isEmpty()) {
                log.warn("Product with id {} not found in any inventory", productId);
                throw new ProductNotFoundException(productId);
            }

            int totalAvailable = foundItems.stream().mapToInt(InventoryItem::getStock).sum();
            log.info("Total available quantity for product {}: {}", productId, totalAvailable);

            if (totalAvailable < requestedQuantity) {
                log.warn("Not enough stock for product {}. Available: {}, Requested: {}", productId, totalAvailable, requestedQuantity);
                outStockProducts.add(new OutStockProduct(productId, totalAvailable, requestedQuantity));
                continue;
            }

            for (InventoryItem inventoryItem : foundItems) {
                int availableStock = inventoryItem.getStock();
                int quantityToTake = Math.min(availableStock, requestedQuantity);

                log.info("Taking {} from inventory {} for product {}", quantityToTake, inventoryItem.getId(), productId);

                inventoryItem.setStock(availableStock - quantityToTake);
                updatedInventoryItems.add(inventoryItem);

                orderItems.add(OrderItem.builder()
                        .inventoryId(inventoryItem.getInventory().getId())
                        .productId(productId)
                        .quantity(quantityToTake)
                        .build());

                requestedQuantity -= quantityToTake;

                if (requestedQuantity == 0) {
                    break;
                }
            }

            if (foundItems.stream().allMatch(item -> item.getStock() == 0)) {
                log.info("Product with id {} is out of stock on all inventories. Updating status.", productId);
                productClient.updateProductStatus(productId, ProductStatus.OUT_OF_STOCK);
            }
        }

        if (!updatedInventoryItems.isEmpty()) {
            log.info("Saving {} updated inventory items", updatedInventoryItems.size());
            inventoryItemRepository.saveAll(updatedInventoryItems);
        }

        if (!outStockProducts.isEmpty()) {
            log.warn("Throwing ProductOutStockException for {} out-of-stock products", outStockProducts.size());
            throw new ProductOutStockException(outStockProducts);
        }

        log.info("Successfully processed takeProductsFromInventory. Returning {} order items.", orderItems.size());
        return enrichOrderItemsWithProductDetails(orderItems);
    }

    @Override
    @Transactional
    public ServerResponse returnProductsToInventory(List<ProductQuantityChange> products) {
        log.info("Starting to return products to inventory. Items count: {}", products.size());

        Set<Integer> inventoryIds = products.stream().map(ProductQuantityChange::getInventoryId).collect(toSet());
        Set<Integer> productIds = products.stream().map(ProductQuantityChange::getProductId).collect(toSet());

        Map<Integer, Inventory> inventoryMap = inventoryRepository.findAllById(inventoryIds)
                .stream().collect(toMap(Inventory::getId, Function.identity()));

        Map<Pair<Integer, Integer>, InventoryItem> inventoryItemMap = inventoryItemRepository
                .findByInventoryIdInAndProductIdIn(inventoryIds, productIds)
                .stream().collect(toMap(item -> Pair.of(item.getInventory().getId(), item.getProductId()), Function.identity()));

        List<InventoryItem> updatedInventoryItems = new ArrayList<>();

        for (ProductQuantityChange product : products) {
            int productId = product.getProductId();
            int inventoryId = product.getInventoryId();
            int productQuantity = product.getQuantity();

            log.debug("Processing product {} (quantity {}) for inventory {}", productId, productQuantity, inventoryId);

            Inventory inventory = inventoryMap.get(inventoryId);
            if (inventory == null) {
                log.warn("Inventory with id {} not found during returnin products to inventory", inventoryId);
                throw new InventoryNotFoundException(inventoryId);
            }

            Pair<Integer, Integer> key = Pair.of(inventoryId, productId);
            InventoryItem inventoryItem = inventoryItemMap.get(key);
            if (inventoryItem == null) {
                log.warn("Product {} not found in inventory {}", productId, inventoryId);
                throw new ProductNotFoundException(productId);
            }

            boolean wasOutOfStock = inventoryItem.getStock() == 0;
            inventoryItem.setStock(inventoryItem.getStock() + productQuantity);
            updatedInventoryItems.add(inventoryItem);

            log.debug("Updated stock for product {} in inventory {}: new stock = {}", productId, inventoryId, inventoryItem.getStock());

            if (wasOutOfStock) {
                log.info("Product {} was out of stock, setting status to AVAILABLE", productId);
                productClient.updateProductStatus(productId, ProductStatus.AVAILABLE);
            }
        }

        inventoryItemRepository.saveAll(updatedInventoryItems);
        log.info("All products successfully returned to inventory");

        return generateServerResponseMessage(PRODUCTS_RETURNED_SUCCESSFULLY, HttpStatus.OK);
    }


    @Override
    @Transactional
    public ServerResponse addNewProductInInventory(ProductQuantityChange productQuantityChange) {
        log.info("Try to add product with id {} in inventory with id {} and quantity {} in addNewProduct method", productQuantityChange.getProductId(), productQuantityChange.getInventoryId(), productQuantityChange.getQuantity());
        Inventory inventory = inventoryRepository.findById(productQuantityChange.getInventoryId()).orElseThrow(
                () -> {
                    log.warn("Inventory with id {} not found", productQuantityChange.getInventoryId());
                    return new InventoryNotFoundException(productQuantityChange.getInventoryId());
                }
        );
        log.debug("Inventory with id {} was found", productQuantityChange.getInventoryId());
        log.debug("Crate new Inventory item");
        InventoryItem inventoryItem = InventoryItem.builder()
                .inventory(inventory)
                .stock(productQuantityChange.getQuantity())
                .productId(productQuantityChange.getProductId())
                .build();
        log.info("Adding item in inventory with id {}", productQuantityChange.getInventoryId());
        inventory.addItem(inventoryItem);
        inventoryRepository.save(inventory);
        log.info("Item was added successfully in inventory with id {}", productQuantityChange.getInventoryId());
        return generateServerResponseMessage(PRODUCTS_ADDED_IN_INVENTORY_SUCCESSFULLY, HttpStatus.CREATED);
    }

    @Override
    @Transactional
    public ProductStock increaseProductStock(ProductQuantityChange productQuantityChange) {
        log.info("Try to add product with id {} in inventory with id {} and quantity {} in addExistProduct method", productQuantityChange.getProductId(), productQuantityChange.getInventoryId(), productQuantityChange.getQuantity());
        inventoryRepository.findById(productQuantityChange.getInventoryId()).orElseThrow(
                () -> {
                    log.warn("Inventory with id {} not found", productQuantityChange.getInventoryId());
                    return new InventoryNotFoundException(productQuantityChange.getInventoryId());
                }
        );
        log.debug("Inventory with id {} was found", productQuantityChange.getInventoryId());

        InventoryItem inventoryItem = inventoryItemRepository.findByInventoryIdAndProductId(
                productQuantityChange.getInventoryId(),
                productQuantityChange.getProductId()
        ).orElseThrow(
                () -> {
                    log.warn("Product with id {} not found in inventory with id {}", productQuantityChange.getProductId(), productQuantityChange.getInventoryId());
                    return new ProductNotFoundException(productQuantityChange.getProductId());
                }
        );
        Integer currentStock = inventoryItem.getStock();
        inventoryItem.setStock(currentStock + productQuantityChange.getQuantity());
        log.info("Adding product {} into inventory {}", productQuantityChange.getProductId(), productQuantityChange.getInventoryId());
        inventoryItemRepository.save(inventoryItem);
        log.info("Product was added! Current stock is {}, previous stock is {}", currentStock + productQuantityChange.getQuantity(), currentStock);

        if (currentStock == 0 && productQuantityChange.getQuantity() > 0) {
            log.info("Change product status to Available");
            productClient.updateProductStatus(productQuantityChange.getProductId(), ProductStatus.AVAILABLE);
        }

        return ProductStock.builder()
                .productId(productQuantityChange.getProductId())
                .currentStock(inventoryItem.getStock())
                .previousStock(currentStock)
                .build();
    }

    @Override
    @Transactional
    public ServerResponse deleteByProductId(Integer productId) {
        log.info("Try to delete product with id {}", productId);
        List<InventoryItem> inventoryItems = inventoryItemRepository.findByProductId(productId);
        if (inventoryItems.isEmpty()) {
            throw new ProductNotFoundException(productId);
        }
        inventoryItemRepository.deleteAll(inventoryItems);
        return generateServerResponseMessage(PRODUCTS_DELETED_SUCCESSFULLY, HttpStatus.OK);
    }

    private List<OrderItem> enrichOrderItemsWithProductDetails(List<OrderItem> orderItems) {
        log.info("Enriching {} order items with product details", orderItems.size());

        Set<Integer> productIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .collect(toSet());

        log.info("Fetching product details for {} unique product IDs", productIds.size());

        Map<Integer, Product> productMap = productClient.getProductsByIds(new ArrayList<>(productIds))
                .getProducts()
                .stream()
                .collect(toMap(Product::getId, Function.identity()));

        log.info("Successfully fetched product details. Mapping to order items");

        List<OrderItem> enrichedOrderItems = orderItems.stream()
                .map(orderItem -> {
                    Product product = productMap.get(orderItem.getProductId());
                    log.debug("Enriching order item for product {} with details", product.getId());

                    return OrderItem.builder()
                            .inventoryId(orderItem.getInventoryId())
                            .productId(orderItem.getProductId())
                            .productDescription(product.getDescription())
                            .productName(product.getName())
                            .productPrice(product.getPrice())
                            .status(product.getStatus())
                            .quantity(orderItem.getQuantity())
                            .build();
                })
                .toList();

        log.info("Successfully enriched order items. Returning {} items.", enrichedOrderItems.size());
        return enrichedOrderItems;
    }


    private ServerResponse generateServerResponseMessage(String message, HttpStatus status) {
        return MessageServerResponse.builder()
                .message(message)
                .status(status.value())
                .build();
    }

}
