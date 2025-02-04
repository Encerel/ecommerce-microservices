package by.innowise.inventoryservice.service.impl;

import by.innowise.inventoryservice.exception.ProductNotFoundException;
import by.innowise.inventoryservice.exception.ProductOutStockException;
import by.innowise.inventoryservice.model.api.OutStockProduct;
import by.innowise.inventoryservice.model.api.Product;
import by.innowise.inventoryservice.model.api.ProductQuantity;
import by.innowise.inventoryservice.model.entity.InventoryItem;
import by.innowise.inventoryservice.model.entity.ProductStatus;
import by.innowise.inventoryservice.repository.InventoryItemRepository;
import by.innowise.inventoryservice.service.InventoryService;
import by.innowise.inventoryservice.web.client.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final ProductClient productClient;


    @Override
    @Transactional
    public List<Product> takeProductsFromInventory(List<ProductQuantity> products) {
        log.info("Check if there are enough products in stock");
        List<Integer> productsIds = new ArrayList<>();
        List<OutStockProduct> outStockProducts = new ArrayList<>();

        for (ProductQuantity product : products) {
            int productId = product.getProductId();
            int requestedQuantity = product.getQuantity();

            InventoryItem foundItem = inventoryItemRepository.findByProductId(productId)
                    .orElseThrow(() -> {
                                log.warn("Product with id {} not found", productId);
                                return new ProductNotFoundException(productId);
                            }
                    );
            int availableQuantity = foundItem.getStock();
            if (availableQuantity >= requestedQuantity) {
                log.info("Product with id {} is in stock", productId);
                foundItem.setStock(availableQuantity - requestedQuantity);
                inventoryItemRepository.save(foundItem);
                log.info("Remain product {} in inventory {}", productId, availableQuantity - requestedQuantity);
                productsIds.add(productId);
                if (foundItem.getStock() == 0) {
                    log.info("Product with id {} out of stock, try to change product status", productId);
                    productClient.updateProductStatus(foundItem.getProductId(), ProductStatus.OUT_OF_STOCK);
                }

            } else {
                log.warn("Product with id {} is not in stock. Required {}, available {}", productId, requestedQuantity, foundItem.getStock());
                outStockProducts.add(new OutStockProduct(productId, foundItem.getStock(), requestedQuantity));
            }
        }

        if (!outStockProducts.isEmpty()) {
            throw new ProductOutStockException(outStockProducts);
        }

        return findProductsByIds(productsIds);
    }

    private List<Product> findProductsByIds(List<Integer> productIds) {
        return productClient.getProductsByIds(productIds).getProducts();
    }

}
