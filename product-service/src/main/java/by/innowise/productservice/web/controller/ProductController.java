package by.innowise.productservice.web.controller;


import by.innowise.productservice.model.dto.ProductCreateDto;
import by.innowise.productservice.model.dto.ProductReadDto;
import by.innowise.productservice.model.dto.ProductsBatchReadDto;
import by.innowise.productservice.service.ProductService;
import by.innowise.productservice.web.payload.ServerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @GetMapping
    public List<ProductReadDto> getProducts() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ProductReadDto getProduct(@PathVariable Integer id) {
        return productService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ServerResponse> createProduct(@RequestBody ProductCreateDto product) {
        return productService.save(product);
    }

    @PostMapping("/batch")
    public ProductsBatchReadDto getProducts(@RequestBody List<Integer> ids) {
        return productService.getProductsByIds(ids);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServerResponse> deleteProduct(@PathVariable Integer id) {
        return productService.delete(id);
    }
}
