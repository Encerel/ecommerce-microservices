package by.innowise.inventoryservice.web.controller;


import by.innowise.inventoryservice.model.api.ProductQuantityChange;
import by.innowise.inventoryservice.model.api.TakenProductQuantity;
import by.innowise.inventoryservice.model.entity.InventoryItem;
import by.innowise.inventoryservice.repository.InventoryItemRepository;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WireMockTest(httpPort = 54321)
@AutoConfigureMockMvc
@Sql(scripts = "/sql/inventories.sql")
class InventoryControllerTestIT {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private InventoryItemRepository inventoryItemRepository;


    @Test
    void takeProductFromInventory_ProductsIdsAreValidProductsInStock_ReturnsOrderDetails() throws Exception {

        List<TakenProductQuantity> products = List.of(new TakenProductQuantity(1, 3), new TakenProductQuantity(2, 5));


        int product1TotalAvailability = calculateProductAvailability(1);
        int product2TotalAvailability = calculateProductAvailability(2);

        int product1ExpectedAvailability = product1TotalAvailability - 3;
        int product2ExpectedAvailability = product2TotalAvailability - 5;

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/api/products/batch"))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                         {
                                          "products": [
                                            {
                                              "id": 1,
                                              "name": "Logitech G102",
                                              "description": "Budget gaming mouse",
                                              "price": 29.99,
                                              "status": "AVAILABLE"
                                            },
                                            {
                                              "id": 2,
                                              "name": "Samsung Galaxy S21",
                                              "description": "Flagship phone with great camera",
                                              "price": 799.99,
                                              "status": "OUT_OF_STOCK"
                                            }
                                          ],
                                          "errors": []
                                        }
                                        """)
                )
        );

        MockHttpServletRequestBuilder takeProductsFromInventory = post("/api/inventories/items/take")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(products))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(takeProductsFromInventory)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$[0].productId").value(1),
                        jsonPath("$[0].productName").value("Logitech G102"),
                        jsonPath("$[1].productId").value(2),
                        jsonPath("$[1].productName").value("Samsung Galaxy S21")
                );


        assertEquals(product1ExpectedAvailability, calculateProductAvailability(1));
        assertEquals(product2ExpectedAvailability, calculateProductAvailability(2));
    }


    @Test
    void takeProductFromInventory_ProductsIdsAreValidProductsOutOfStock_ReturnsConflict() throws Exception {

        List<TakenProductQuantity> products = List.of(new TakenProductQuantity(1, 3), new TakenProductQuantity(2, 100));


        int product1ExpectedAvailability = calculateProductAvailability(1);
        int product2ExpectedAvailability = calculateProductAvailability(2);


        MockHttpServletRequestBuilder takeProductsFromInventory = post("/api/inventories/items/take")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(products))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(takeProductsFromInventory)
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Not enough items in stock!",
                                    "code": 409,
                                    "outStockProducts": [
                                        {
                                            "productId": 2,
                                            "availableQuantity": 24,
                                            "requiredQuantity": 100
                                        }
                                    ]
                                }
                                """)
                );


        assertEquals(product1ExpectedAvailability, calculateProductAvailability(1));
        assertEquals(product2ExpectedAvailability, calculateProductAvailability(2));
    }


    @Test
    void takeProductFromInventory_ProductsIdsAreInvalid_ReturnsBadRequest() throws Exception {

        List<TakenProductQuantity> products = List.of(new TakenProductQuantity(1, 3), new TakenProductQuantity(200, 100));


        MockHttpServletRequestBuilder takeProductsFromInventory = post("/api/inventories/items/take")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(products))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(takeProductsFromInventory)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Inventory item with product id 200 not found",
                                    "status": 400
                                }
                                """)
                );

    }

    @Test
    void returnProductToInventory_ProductsIdsAreValid_ReturnsSuccessMessage() throws Exception {

        List<ProductQuantityChange> products = List.of(new ProductQuantityChange(1, 1, 3), new ProductQuantityChange(1, 2, 100));

        int product1ExpectedAvailability = calculateProductAvailability(1) + 3;
        int product2ExpectedAvailability = calculateProductAvailability(2) + 100;


        MockHttpServletRequestBuilder returnProductsToInventory = post("/api/inventories/items/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(products))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(returnProductsToInventory)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Product returned successfully",
                                    "status": 200
                                }
                                """)
                );


        assertEquals(product1ExpectedAvailability, calculateProductAvailability(1));
        assertEquals(product2ExpectedAvailability, calculateProductAvailability(2));
    }


    @Test
    void returnProductToInventory_ProductsIdsAreInvalid_ReturnsBadRequest() throws Exception {

        List<ProductQuantityChange> products = List.of(new ProductQuantityChange(1, 1, 3), new ProductQuantityChange(1, 200, 100));


        MockHttpServletRequestBuilder takeProductsFromInventory = post("/api/inventories/items/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(products))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(takeProductsFromInventory)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Inventory item with product id 200 not found",
                                    "status": 400
                                }
                                """)
                );
    }

    @Test
    void addNewProductToInventory_InventoryIdIsValid_ReturnsSuccessMessage() throws Exception {


        ProductQuantityChange newProduct = new ProductQuantityChange(1, 201, 3);

        long expectedItemAmount = inventoryItemRepository.count() + 1;

        MockHttpServletRequestBuilder addNewProductToInventory = post("/api/inventories/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(addNewProductToInventory)
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Product added to inventory successfully",
                                    "status": 201
                                }
                                """)
                );

        assertEquals(expectedItemAmount, inventoryItemRepository.count());
    }

    @Test
    void addNewProductToInventory_InventoryIdIsInvalid_ReturnsBadRequest() throws Exception {


        ProductQuantityChange newProduct = new ProductQuantityChange(100, 201, 3);

        long expectedItemAmount = inventoryItemRepository.count();

        MockHttpServletRequestBuilder addNewProductToInventory = post("/api/inventories/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(addNewProductToInventory)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Inventory with id 100 not found",
                                    "status": 400
                                }
                                """)
                );

        assertEquals(expectedItemAmount, inventoryItemRepository.count());
    }

    @Test
    void increaseProductStock_ProductIdIsValidInventoryIdIsValid_ReturnsProductStock() throws Exception {

        ProductQuantityChange product = new ProductQuantityChange(1, 1, 3);

        int expectedInventoryItemAmount = calculateProductAvailability(1) + 3;

        MockHttpServletRequestBuilder increaseProductStockRequestBuilder = patch("/api/inventories/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));


        mockMvc.perform(increaseProductStockRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "productId": 1,
                                    "previousStock": 14,
                                    "currentStock": 17
                                }
                                """)
                );

        assertEquals(expectedInventoryItemAmount, calculateProductAvailability(1));

    }

    @Test
    void increaseProductStock_ProductIdIsValidInventoryIdIsInvalid_ReturnsBadRequest() throws Exception {

        ProductQuantityChange product = new ProductQuantityChange(100, 1, 3);

        MockHttpServletRequestBuilder increaseProductStockRequestBuilder = patch("/api/inventories/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        int expectedInventoryItemAmount = calculateProductAvailability(1);

        mockMvc.perform(increaseProductStockRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Inventory with id 100 not found",
                                    "status": 400
                                }
                                """)
                );

        assertEquals(expectedInventoryItemAmount, calculateProductAvailability(1));
    }

    @Test
    void increaseProductStock_ProductIdIsInvalid_ReturnsBadRequest() throws Exception {

        ProductQuantityChange product = new ProductQuantityChange(1, 999, 3);

        int expectedInventoryItemAmount = calculateProductAvailability(1);

        MockHttpServletRequestBuilder increaseProductStockRequestBuilder = patch("/api/inventories/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));


        mockMvc.perform(increaseProductStockRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Inventory item with product id 999 not found",
                                    "status": 400
                                }
                                """)
                );

        assertEquals(expectedInventoryItemAmount, calculateProductAvailability(1));

    }

    @Test
    void deleteProductFromInventory_ProductIdIsValid_ReturnsSuccessMessage() throws Exception {
        int productId = 1;

        int expectedInventoryItemAmount = 0;

        MockHttpServletRequestBuilder deleteProductFromInventoryRequestBuilder = delete("/api/inventories/items/" + productId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));


        mockMvc.perform(deleteProductFromInventoryRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Product deleted successfully",
                                    "status": 200
                                }
                                """)
                );

        assertEquals(expectedInventoryItemAmount, calculateProductAvailability(1));
    }

    @Test
    void deleteProductFromInventory_ProductIdIsInvalid_ReturnsBadRequest() throws Exception {
        int productId = 999;


        MockHttpServletRequestBuilder deleteProductFromInventoryRequestBuilder = delete("/api/inventories/items/" + productId)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));


        mockMvc.perform(deleteProductFromInventoryRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Inventory item with product id 999 not found",
                                    "status": 400
                                }
                                """)
                );

    }

    private Integer calculateProductAvailability(Integer productId) {
        return inventoryItemRepository.findByProductId(productId).stream().mapToInt(InventoryItem::getStock).sum();
    }

}