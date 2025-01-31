package by.innowise.productservice.service.impl;


import by.innowise.productservice.exception.ProductNotFoundException;
import by.innowise.productservice.mapper.ProductCreateMapper;
import by.innowise.productservice.mapper.ProductReadMapper;
import by.innowise.productservice.model.dto.ProductCreateDto;
import by.innowise.productservice.model.dto.ProductReadDto;
import by.innowise.productservice.model.dto.ProductsBatchReadDto;
import by.innowise.productservice.model.entity.Product;
import by.innowise.productservice.repository.ProductRepository;
import by.innowise.productservice.service.ProductService;
import by.innowise.productservice.web.payload.ServerResponse;
import by.innowise.productservice.web.payload.response.AdviceErrorMessage;
import by.innowise.productservice.web.payload.response.MessageServerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.innowise.productservice.constant.Message.PRODUCT_DELETED_SUCCESSFULLY;
import static by.innowise.productservice.constant.Message.PRODUCT_SAVED_SUCCESSFULLY;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductReadMapper productReadMapper;
    private final ProductCreateMapper productCreateMapper;


    @Override
    public List<ProductReadDto> findAll() {
        return productReadMapper.toListDto(productRepository.findAll());
    }

    @Override
    public ProductReadDto findById(Integer id) {
        return productReadMapper.toDto(productRepository.findById(id)
                .orElseThrow(
                        () -> new ProductNotFoundException(id))
        );
    }

    @Override
    public ProductsBatchReadDto getProductsByIds(List<Integer> ids) {
        List<ProductReadDto> products = new ArrayList<>();
        List<AdviceErrorMessage> errors = new ArrayList<>();
        for (Integer id : ids) {
            Optional<Product> foundProduct = productRepository.findById(id);
            if (foundProduct.isPresent()) {
                products.add(productReadMapper.toDto(foundProduct.get()));
            } else {
                errors.add(AdviceErrorMessage.builder()
                        .message(String.format("Product with id %d not found!", id))
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
    public ResponseEntity<ServerResponse> save(ProductCreateDto productCreateDto) {
        Product product = productCreateMapper.toEntity(productCreateDto);
        productRepository.save(product);
        ServerResponse message = MessageServerResponse.builder()
                .message(PRODUCT_SAVED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(message);
    }

    @Override
    @Transactional
    public ResponseEntity<ServerResponse> delete(Integer id) {
        productRepository.deleteById(id);

        ServerResponse message = MessageServerResponse.builder()
                .message(PRODUCT_DELETED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(message);
    }

}
