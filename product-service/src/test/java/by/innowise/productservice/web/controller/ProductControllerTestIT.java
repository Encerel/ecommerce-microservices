package by.innowise.productservice.web.controller;

import by.innowise.productservice.model.dto.ProductCreateDto;
import by.innowise.productservice.model.dto.ProductReadDto;
import by.innowise.productservice.model.dto.ProductStatusRequest;
import by.innowise.productservice.model.entity.ProductStatus;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 54321)
@Sql(scripts = "/sql/products.sql")
class ProductControllerTestIT {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void getAllProducts_ReturnsListOfProducts() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/products")
                .param("offset", "0")
                .param("pageSize", "3");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.content").isArray(),
                        jsonPath("$.content[0].id").value(1),
                        jsonPath("$.content[1].id").value(2),
                        jsonPath("$.content", hasSize(3)),
                        jsonPath("$.content[2].name").value("Samsung Galaxy S21")
                );
    }

    @Test
    void getProductById_IdIsValid_ReturnsProduct() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/products/8");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(8),
                        jsonPath("$.name").value("Huawei P40"),
                        jsonPath("$.status").value("OUT_OF_STOCK")
                );
    }

    @Test
    void getProductById_IdIsInvalid_ReturnsBadRequest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/products/12");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("Product with id 12 not found")
                );
    }

    @Test
    void getProductsBatch_IdsAreValid_ReturnsProductsBatchDtoWithProducts() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/api/products/batch")
                .content(objectMapper.writeValueAsString(List.of(3, 4, 9)))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")));
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.products[0].id").value(3),
                        jsonPath("$.products[0].name").value("Samsung Galaxy S21"),
                        jsonPath("$.products[1].price").value(599.9),
                        jsonPath("$.products[1].status").value("AVAILABLE"),
                        jsonPath("$.products[2].description").value("Budget flagship"),
                        jsonPath("$.products[2].name").value("Realme GT"),
                        jsonPath("$.products", hasSize(3))
                );
    }

    @Test
    void getProductsBatch_IdIsInvalid_ReturnsProductsBatchDtoWithProductsAndErrors() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/api/products/batch")
                .content(objectMapper.writeValueAsString(List.of(3, 4, 12)))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")));
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.products[0].id").value(3),
                        jsonPath("$.products[0].name").value("Samsung Galaxy S21"),
                        jsonPath("$.products[1].price").value(599.9),
                        jsonPath("$.products[1].status").value("AVAILABLE"),
                        jsonPath("$.products", hasSize(2)),
                        jsonPath("$.errors[0].message").value("Product with id 12 not found!"),
                        jsonPath("$.errors[0].status").value(404),
                        jsonPath("$.errors", hasSize(1))
                );
    }

    @Test
    void createProduct_UserIsUnauthorized_Returns403Status() throws Exception {
        ProductCreateDto productCreateDto = ProductCreateDto.builder()
                .name("Logitech G102")
                .price(100.99)
                .quantity(5)
                .inventoryId(1)
                .description("Budget gaming mouse")
                .build();

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/api/inventories/items")));
        MockHttpServletRequestBuilder requestBuilder = post("/api/products")
                .content(objectMapper.writeValueAsString(productCreateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")));
        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @Test
    void createProduct_UserIsAuthorizedAndProductDoesNotExist_ReturnsProduct() throws Exception {
        ProductCreateDto productCreateDto = ProductCreateDto.builder()
                .name("Logitech G102")
                .price(100.99)
                .quantity(5)
                .inventoryId(1)
                .description("Budget gaming mouse")
                .build();

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/api/inventories/items")));
        MockHttpServletRequestBuilder createProductRequestBuilder = post("/api/products")
                .content(objectMapper.writeValueAsString(productCreateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));
        mockMvc.perform(createProductRequestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("Product saved successfully!"),
                        jsonPath("$.status").value(201)
                );

        MockHttpServletRequestBuilder getProductsRequestBuilder = get("/api/products")
                .param("offset", "0")
                .param("pageSize", "15");
        mockMvc.perform(getProductsRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.content[10].id").value(11),
                        jsonPath("$.content[10].name").value("Logitech G102"),
                        jsonPath("$.content", hasSize(11))
                );
    }

    @Test
    void createProduct_UserIsAuthorizedAndProductAlreadyExists_ReturnsProduct() throws Exception {
        ProductCreateDto productCreateDto = ProductCreateDto.builder()
                .name("Logitech G102")
                .price(100.99)
                .quantity(5)
                .inventoryId(1)
                .description("Budget gaming mouse")
                .build();

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/api/inventories/items"))
                .willReturn(WireMock.created()));
        MockHttpServletRequestBuilder createProductRequestBuilder = post("/api/products")
                .content(objectMapper.writeValueAsString(productCreateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));
        MockHttpServletRequestBuilder getProductsRequestBuilder = get("/api/products")
                .param("offset", "0")
                .param("pageSize", "15");

        mockMvc.perform(createProductRequestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("Product saved successfully!"),
                        jsonPath("$.status").value(HttpStatus.CREATED.value())
                );

        mockMvc.perform(createProductRequestBuilder)
                .andExpectAll(
                        status().isConflict(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.message")
                                .value(String.format("Product with name: %s already exists", productCreateDto.getName())),
                        jsonPath("$.status").value(HttpStatus.CONFLICT.value())
                );


        mockMvc.perform(getProductsRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(11))
                );
    }

    @Test
    void updateProductInfo_UserIsUnauthorized_Returns403Status() throws Exception {
        ProductReadDto productCreateDto = ProductReadDto.builder()
                .id(1)
                .name("Logitech G102")
                .price(100.99)
                .description("Budget gaming mouse")
                .build();

        MockHttpServletRequestBuilder requestBuilder = put("/api/products")
                .content(objectMapper.writeValueAsString(productCreateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")));
        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProductInfo_UserIsAuthorizedAndProductExists_ReturnsOkStatus() throws Exception {
        ProductReadDto productReadDto = ProductReadDto.builder()
                .id(1)
                .name("Logitech G102")
                .price(100.99)
                .description("Budget gaming mouse")
                .status(ProductStatus.AVAILABLE)
                .build();

        MockHttpServletRequestBuilder updateProductRequestBuilder = put("/api/products")
                .content(objectMapper.writeValueAsString(productReadDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));
        MockHttpServletRequestBuilder getProductsRequestBuilder = get("/api/products/1");

        mockMvc.perform(getProductsRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Iphone 13")
                );

        mockMvc.perform(updateProductRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("Product info updated successfully!"),
                        jsonPath("$.status").value(HttpStatus.OK.value())
                );
        mockMvc.perform(getProductsRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Logitech G102"),
                        jsonPath("$.description").value("Budget gaming mouse")
                );
    }

    @Test
    void updateProductInfo_UserIsAuthorizedAndProductDoesNotExist_ReturnsBadRequest() throws Exception {
        ProductReadDto productReadDto = ProductReadDto.builder()
                .id(12)
                .name("Logitech G102")
                .price(100.99)
                .description("Budget gaming mouse")
                .status(ProductStatus.AVAILABLE)
                .build();

        MockHttpServletRequestBuilder updateProductRequestBuilder = put("/api/products")
                .content(objectMapper.writeValueAsString(productReadDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(updateProductRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("Product with id 12 not found"),
                        jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value())
                );
    }

    @Test
    void updateProductStatus_UserIsUnauthorized_Returns403Status() throws Exception {
        MockHttpServletRequestBuilder getProductsRequestBuilder = get("/api/products/1");

        mockMvc.perform(getProductsRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status").value(ProductStatus.AVAILABLE.name())
                );


        MockHttpServletRequestBuilder requestBuilder = patch("/api/products/1/status")
                .content(objectMapper.writeValueAsString(new ProductStatusRequest(ProductStatus.OUT_OF_STOCK)))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")));
        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProductStatus_UserIsAuthorizedAndProductExists_ReturnsOkStatus() throws Exception {
        MockHttpServletRequestBuilder getProductsRequestBuilder = get("/api/products/1");

        mockMvc.perform(getProductsRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status").value(ProductStatus.AVAILABLE.name())
                );


        MockHttpServletRequestBuilder changeStatusRequestBuilder = patch("/api/products/1/status")
                .content(objectMapper.writeValueAsString(new ProductStatusRequest(ProductStatus.OUT_OF_STOCK)))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(changeStatusRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("Product status updated successfully!"),
                        jsonPath("$.status").value(HttpStatus.OK.value())
                );

        mockMvc.perform(getProductsRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status").value(ProductStatus.OUT_OF_STOCK.name()),
                        jsonPath("$.id").value(1)
                );

    }

    @Test
    void updateProductStatus_UserIsAuthorizedAndProductDoesNotExist_ReturnsBadRequest() throws Exception {

        MockHttpServletRequestBuilder changeStatusRequestBuilder = patch("/api/products/12/status")
                .content(objectMapper.writeValueAsString(new ProductStatusRequest(ProductStatus.OUT_OF_STOCK)))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(changeStatusRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Product with id 12 not found"),
                        jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value())
                );


    }

    @Test
    void deleteProduct_UserIsUnauthorized_Returns403Status() throws Exception {

        MockHttpServletRequestBuilder deleteRequestBuilder = delete("/api/products/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")));
        mockMvc.perform(deleteRequestBuilder)
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProduct_UserIsAuthorizedAndProductExistsAndStatusOutOfStock_ReturnsOKStatus() throws Exception {


        MockHttpServletRequestBuilder getAllProductsRequestBuilder = get("/api/products")
                .param("offset", "0")
                .param("pageSize", "15");
        MockHttpServletRequestBuilder getProductByIdRequestBuilder = get("/api/products/2");
        MockHttpServletRequestBuilder deleteRequestBuilder = delete("/api/products/2")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        WireMock.stubFor(WireMock.delete(WireMock.urlPathMatching("/api/inventories/items/2"))
                .willReturn(WireMock.ok()));

        mockMvc.perform(getProductByIdRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(2),
                        jsonPath("$.name").value("Xiaomi 10"),
                        jsonPath("$.status").value(ProductStatus.OUT_OF_STOCK.name())
                );


        mockMvc.perform(deleteRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("Product deleted successfully!"),
                        jsonPath("$.status").value(HttpStatus.OK.value())
                );

        mockMvc.perform(getProductByIdRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Product with id 2 not found")
                );

        mockMvc.perform(getAllProductsRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(9))
                );
    }

    @Test
    void deleteProduct_UserIsAuthorizedAndProductExistsAndStatusOutOfStockAndDoesNotExistInInventory_ReturnsBadRequest() throws Exception {


        MockHttpServletRequestBuilder getAllProductsRequestBuilder = get("/api/products")
                .param("offset", "0")
                .param("pageSize", "15");
        MockHttpServletRequestBuilder getProductByIdRequestBuilder = get("/api/products/2");
        MockHttpServletRequestBuilder deleteRequestBuilder = delete("/api/products/2")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        WireMock.stubFor(WireMock.delete(WireMock.urlPathMatching("/api/inventories/items/2"))
                .willReturn(WireMock.badRequest()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                 "message" : "Inventory item with product id 2 not found",
                                 "status" : 400
                                }
                                """)));

        mockMvc.perform(getProductByIdRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(2),
                        jsonPath("$.name").value("Xiaomi 10"),
                        jsonPath("$.status").value(ProductStatus.OUT_OF_STOCK.name())
                );


        mockMvc.perform(deleteRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Inventory item with product id 2 not found"),
                        jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value())
                );

        mockMvc.perform(getAllProductsRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(10))
                );
    }

    @Test
    void deleteProduct_UserIsAuthorizedAndProductExistsAndStatusAvailable_ReturnsBadRequest() throws Exception {


        MockHttpServletRequestBuilder getAllProductsRequestBuilder = get("/api/products")
                .param("offset", "0")
                .param("pageSize", "15");
        MockHttpServletRequestBuilder getProductByIdRequestBuilder = get("/api/products/1");
        MockHttpServletRequestBuilder deleteRequestBuilder = delete("/api/products/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(getProductByIdRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Iphone 13"),
                        jsonPath("$.status").value(ProductStatus.AVAILABLE.name())
                );


        mockMvc.perform(deleteRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Product with id 1 is in stock. Deleting is not possible!"),
                        jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value())
                );

        mockMvc.perform(getAllProductsRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(10))
                );
    }


}