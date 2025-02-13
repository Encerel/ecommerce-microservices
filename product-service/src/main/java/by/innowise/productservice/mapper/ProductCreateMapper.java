package by.innowise.productservice.mapper;

import by.innowise.mapper.Mapper;
import by.innowise.productservice.model.dto.ProductCreateDto;
import by.innowise.productservice.model.entity.Product;
import by.innowise.productservice.model.entity.ProductStatus;
import org.springframework.stereotype.Component;

@Component
public class ProductCreateMapper implements Mapper<Product, ProductCreateDto> {

    @Override
    public Product toEntity(ProductCreateDto dto) {
        if (dto == null) {
            return null;
        }

        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .status(ProductStatus.AVAILABLE)
                .build();
    }
}
