package by.innowise.productservice.service;

import by.innowise.productservice.model.dto.ProductCreateDto;
import by.innowise.productservice.model.dto.ProductReadDto;
import by.innowise.productservice.web.payload.ServerResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface ProductService {


    List<ProductReadDto> findAll();

    ProductReadDto findById(Integer id);

    ResponseEntity<ServerResponse> save(ProductCreateDto productCreateDto);

    ResponseEntity<ServerResponse> delete(Integer id);
}
