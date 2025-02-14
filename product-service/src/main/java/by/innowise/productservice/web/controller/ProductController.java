package by.innowise.productservice.web.controller;


import by.innowise.productservice.model.dto.ProductCreateDto;
import by.innowise.productservice.model.dto.ProductReadDto;
import by.innowise.productservice.model.dto.ProductStatusRequest;
import by.innowise.productservice.model.dto.ProductsBatchReadDto;
import by.innowise.productservice.service.ProductService;
import by.innowise.productservice.web.payload.ServerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<ProductReadDto> getProducts(@RequestParam(defaultValue = "0") Integer offset,
                                            @RequestParam(defaultValue = "5") Integer pageSize) {
        return productService.findAll(offset, pageSize);
    }

    @GetMapping("/{id}")
    public ProductReadDto getProduct(@PathVariable Integer id) {
        return productService.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ServerResponse> createProduct(@RequestBody @Valid ProductCreateDto product) {
        return productService.save(product);
    }

    @PostMapping("/batch")
    public ProductsBatchReadDto getProducts(@RequestBody List<Integer> ids) {
        return productService.getProductsByIds(ids);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<ServerResponse> updateInfo(@RequestBody @Valid ProductReadDto productReadDto) {
        return productService.update(productReadDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{productId}/status")
    public ResponseEntity<ServerResponse> updateProductStatus(@PathVariable Integer productId,
                                                              @RequestBody @Valid ProductStatusRequest status) {
        return productService.updateStatus(productId, status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ServerResponse> deleteProduct(@PathVariable Integer id) {
        return productService.delete(id);
    }
}
