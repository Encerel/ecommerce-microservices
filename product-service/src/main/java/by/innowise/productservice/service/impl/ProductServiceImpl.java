package by.innowise.productservice.service.impl;


import by.innowise.productservice.exception.ProductNotFoundException;
import by.innowise.productservice.mapper.ProductCreateMapper;
import by.innowise.productservice.mapper.ProductReadMapper;
import by.innowise.productservice.model.dto.ProductCreateDto;
import by.innowise.productservice.model.dto.ProductReadDto;
import by.innowise.productservice.model.entity.Product;
import by.innowise.productservice.repository.ProductRepository;
import by.innowise.productservice.service.ProductService;
import by.innowise.productservice.web.payload.ServerResponse;
import by.innowise.productservice.web.payload.response.MessageServerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static by.innowise.productservice.constant.Message.PRODUCT_DELETED_SUCCESSFULLY;
import static by.innowise.productservice.constant.Message.PRODUCT_SAVED_SUCCESSFULLY;

@Service
@RequiredArgsConstructor
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
    public ResponseEntity<ServerResponse> save(ProductCreateDto productCreateDto) {
        Product product = productCreateMapper.toEntity(productCreateDto);
        productRepository.save(product);
        ServerResponse message = MessageServerResponse.builder()
                .message(PRODUCT_SAVED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<ServerResponse> delete(Integer id) {
        productRepository.deleteById(id);

        ServerResponse message = MessageServerResponse.builder()
                .message(PRODUCT_DELETED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(message);
    }
}
