package by.innowise.orderservice.mapper;

import by.innowise.mapper.Mapper;
import by.innowise.orderservice.model.dto.ProductReadDto;
import by.innowise.orderservice.model.entity.product.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductReadMapper implements Mapper<Product, ProductReadDto> {

    @Override
    public ProductReadDto toDto(Product entity) {

        if (entity == null) {
            return null;
        }

        return ProductReadDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .build();
    }

    @Override
    public Product toEntity(ProductReadDto dto) {
        if (dto == null) {
            return null;
        }

        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .status(dto.getStatus())
                .build();
    }

    @Override
    public List<ProductReadDto> toListDto(List<Product> entitiesList) {
        List<ProductReadDto> dtoList = new ArrayList<>();

        for (Product entity : entitiesList) {
            dtoList.add(toDto(entity));
        }
        return dtoList;
    }

    public List<Product> toListEntity(List<ProductReadDto> dtoList) {
        if (dtoList == null) {
            return null;
        }
        List<Product> products = new ArrayList<>();

        for (ProductReadDto dto : dtoList) {
            products.add(toEntity(dto));
        }
        return products;
    }
}
