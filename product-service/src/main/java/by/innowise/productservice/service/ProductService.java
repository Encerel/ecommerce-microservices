package by.innowise.productservice.service;

import by.innowise.productservice.model.dto.ProductCreateDto;
import by.innowise.productservice.model.dto.ProductReadDto;
import by.innowise.productservice.model.dto.ProductStatusRequest;
import by.innowise.productservice.model.dto.ProductsBatchReadDto;
import by.innowise.productservice.web.payload.ServerResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface ProductService {


    Page<ProductReadDto> findAll(int offset, int pageSize);

    ProductReadDto findById(Integer id);

    ResponseEntity<ServerResponse> save(ProductCreateDto productCreateDto);

    ResponseEntity<ServerResponse> delete(Integer id);

    ProductsBatchReadDto getProductsByIds(List<Integer> ids);

    ResponseEntity<ServerResponse> updateStatus(Integer productId, ProductStatusRequest status);
}
