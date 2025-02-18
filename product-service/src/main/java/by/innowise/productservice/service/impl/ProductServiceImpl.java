package by.innowise.productservice.service.impl;


import by.innowise.productservice.constant.TopicName;
import by.innowise.productservice.exception.ProductAlreadyExistsException;
import by.innowise.productservice.exception.ProductInStockException;
import by.innowise.productservice.exception.ProductNotFoundException;
import by.innowise.productservice.mapper.ProductCreateMapper;
import by.innowise.productservice.mapper.ProductReadMapper;
import by.innowise.productservice.model.api.ProductQuantity;
import by.innowise.productservice.model.dto.ProductCreateDto;
import by.innowise.productservice.model.dto.ProductReadDto;
import by.innowise.productservice.model.dto.ProductStatusRequest;
import by.innowise.productservice.model.dto.ProductsBatchReadDto;
import by.innowise.productservice.model.entity.Product;
import by.innowise.productservice.model.entity.ProductStatus;
import by.innowise.productservice.repository.ProductRepository;
import by.innowise.productservice.service.ProductService;
import by.innowise.productservice.web.client.InventoryClient;
import by.innowise.productservice.web.payload.ServerResponse;
import by.innowise.productservice.web.payload.response.AdviceErrorMessage;
import by.innowise.productservice.web.payload.response.MessageServerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.innowise.productservice.constant.ErrorMessage.PRODUCT_NOT_FOUND;
import static by.innowise.productservice.constant.Message.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductReadMapper productReadMapper;
    private final ProductCreateMapper productCreateMapper;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, ProductReadDto> productKafkaTemplate;


    @Override
    public Page<ProductReadDto> findAll(int offset, int pageSize) {
        log.info("Getting all products");
        return productRepository.findAll(PageRequest.of(offset, pageSize)).map(productReadMapper::toDto);
    }

    @Override
    public ProductReadDto findById(Integer id) {
        log.info("Try to find product with id: {}", id);
        return productReadMapper.toDto(productRepository.findById(id)
                .orElseThrow(
                        () -> new ProductNotFoundException(id)
                )
        );
    }

    @Override
    public ProductsBatchReadDto getProductsByIds(List<Integer> ids) {
        log.info("Try to find product with ids: {}", ids);
        List<ProductReadDto> products = new ArrayList<>();
        List<AdviceErrorMessage> errors = new ArrayList<>();
        for (Integer id : ids) {
            Optional<Product> foundProduct = productRepository.findById(id);
            if (foundProduct.isPresent()) {
                log.info("Product with id: {} found", id);
                products.add(productReadMapper.toDto(foundProduct.get()));
            } else {
                log.warn("Product with id: {} not found", id);
                errors.add(AdviceErrorMessage.builder()
                        .message(String.format(PRODUCT_NOT_FOUND, id))
                        .status(HttpStatus.NOT_FOUND.value())
                        .build());
            }
        }

        return ProductsBatchReadDto.builder()
                .products(products)
                .errors(errors)
                .build();
    }

    @Override
    @Transactional
    public ResponseEntity<ServerResponse> updateStatus(Integer productId, ProductStatusRequest status) {
        log.info("Try to update status for product with id: {}", productId);
        Optional<Product> foundProduct = productRepository.findById(productId);
        if (foundProduct.isEmpty()) {
            log.warn("Product with id {} not found during status updating!", productId);
            throw new ProductNotFoundException(productId);
        }
        Product product = foundProduct.get();
        product.setStatus(status.getProductStatus());
        log.info("Try to change status for product with id: {}. Current status: {}. New status: {}", productId, product.getStatus(), status);
        productRepository.save(product);
        log.info("Status of product with id {} updated on {} successfully !", productId, status);
        productKafkaTemplate.send(TopicName.PRODUCT_STATUS_UPDATES_EVENTS_TOPIC,
                String.valueOf(product.getId()),
                productReadMapper.toDto(product));
        ServerResponse serverResponse = MessageServerResponse.builder()
                .message(PRODUCT_STATUS_UPDATED_SUCCESSFULLY)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(serverResponse);
    }


    @Override
    @Transactional
    public ResponseEntity<ServerResponse> save(ProductCreateDto productCreateDto) {
        log.info("Try to find product with name: {} in db", productCreateDto.getName());
        Optional<Product> foundByName = productRepository.findByName(productCreateDto.getName());

        if (foundByName.isPresent()) {
            log.warn("Product with name {} already exists!", productCreateDto.getName());
            throw new ProductAlreadyExistsException(productCreateDto.getName());
        }

        log.debug("Map productCreateDto in entity!");
        Product product = productCreateMapper.toEntity(productCreateDto);
        Product preparedProduct = productRepository.save(product);
        log.info("Product with name {} saved!", productCreateDto.getName());
        ProductQuantity productQuantity = ProductQuantity.builder()
                .inventoryId(productCreateDto.getInventoryId())
                .productId(preparedProduct.getId())
                .quantity(productCreateDto.getQuantity())
                .build();
        inventoryClient.addNewProductInInventory(productQuantity);
        productKafkaTemplate.send(TopicName.PRODUCT_CREATE_EVENTS_TOPIC,
                String.valueOf(preparedProduct.getId()),
                productReadMapper.toDto(preparedProduct));
        ServerResponse message = MessageServerResponse.builder()
                .message(PRODUCT_SAVED_SUCCESSFULLY)
                .status(HttpStatus.CREATED.value())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @Override
    @Transactional
    public ResponseEntity<ServerResponse> update(ProductReadDto productReadDto) {

        Product product = productRepository.findById(productReadDto.getId())
                .orElseThrow(() -> {
                    log.warn("Product with id {} not found during update!", productReadDto.getId());
                    return new ProductNotFoundException(productReadDto.getId());
                });


        product.setName(productReadDto.getName());
        product.setDescription(productReadDto.getDescription());
        product.setPrice(productReadDto.getPrice());
        product.setStatus(productReadDto.getStatus());

        productRepository.save(product);
        ServerResponse message = MessageServerResponse.builder()
                .message(PRODUCT_INFO_UPDATED_SUCCESSFULLY)
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(message);
    }

    @Override
    @Transactional
    public ResponseEntity<ServerResponse> delete(Integer id) {
        log.info("Try to delete product with id: {}", id);
        Product product = productRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("Product with id {} not found!", id);
                    return new ProductNotFoundException(id);
                }
        );
        log.debug("Check product status");
        if (product.getStatus() == ProductStatus.AVAILABLE) {
            log.warn("Product status is AVAILABLE. Deleting product is not possible!");
            throw new ProductInStockException(product.getId());
        }
        inventoryClient.deleteProductFromInventory(product.getId());
        productRepository.delete(product);
        log.info("Product with id {} deleted!", id);
        ServerResponse message = MessageServerResponse.builder()
                .message(PRODUCT_DELETED_SUCCESSFULLY)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(message);
    }

}
