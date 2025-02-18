package by.innowise.orderservice.web.controller;

import by.innowise.orderservice.model.api.TakenProductQuantity;
import by.innowise.orderservice.model.dto.OrderCreateDto;
import by.innowise.orderservice.model.entity.OrderStatus;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WireMockTest
@AutoConfigureMockMvc
@Sql(scripts = "/sql/orders.sql")
class OrderControllerTestIT {

    private WireMockServer wireMockServer1;
    private WireMockServer wireMockServer2;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        wireMockServer1 = new WireMockServer(54321);
        wireMockServer2 = new WireMockServer(54322);

        wireMockServer1.start();
        wireMockServer2.start();

        WireMock.configureFor("localhost", 54321);
        WireMock.configureFor("localhost", 54322);
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer1 != null && wireMockServer1.isRunning()) {
            wireMockServer1.stop();
        }
        if (wireMockServer2 != null && wireMockServer2.isRunning()) {
            wireMockServer2.stop();
        }
    }

    @Test
    void placeOrder_DataIsValidProductsInStockProductsIdsAreValid_ReturnsOrderDetails() throws Exception {
        UUID userId = UUID.randomUUID();
        TakenProductQuantity product1 = TakenProductQuantity.builder()
                .productId(202)
                .quantity(2)
                .build();

        TakenProductQuantity product2 = TakenProductQuantity.builder()
                .productId(203)
                .quantity(1)
                .build();

        OrderCreateDto orderCreateDto = OrderCreateDto.builder()
                .items(List.of(product1, product2))
                .build();

        wireMockServer1.stubFor(
                WireMock.post(WireMock.urlPathMatching("/api/inventories/items/take")).willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        [
                                          {
                                            "orderId": 1,
                                            "productId": 202,
                                            "productName": "Logitech G102",
                                            "productDescription": "Budget gaming mouse",
                                            "productPrice": 29.99,
                                            "status": "AVAILABLE",
                                            "quantity": 2,
                                            "inventoryId": 1
                                          },
                                          {
                                            "orderId": 1,
                                            "productId": 203,
                                            "productName": "Samsung Galaxy S21",
                                            "productDescription": "Flagship phone with great camera",
                                            "productPrice": 799.99,
                                            "status": "OUT_OF_STOCK",
                                            "quantity": 1,
                                            "inventoryId": 2
                                          }
                                        ]
                                        """
                                )
                ));

        wireMockServer2.stubFor(
                WireMock.post(WireMock.urlPathMatching("/api/products/batch")).willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                          "products": [
                                            {
                                              "id": 202,
                                              "name": "Logitech G102",
                                              "description": "Budget gaming mouse",
                                              "price": 29.99,
                                              "status": "AVAILABLE"
                                            },
                                            {
                                              "id": 203,
                                              "name": "Samsung Galaxy S21",
                                              "description": "Flagship phone with great camera",
                                              "price": 799.99,
                                              "status": "OUT_OF_STOCK"
                                            }
                                          ],
                                          "errors": []
                                        }
                                        """)
                ));

        MockHttpServletRequestBuilder placeOrderRequestBuilder = post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderCreateDto))
                .with(jwt().jwt(builder -> {
                    builder.claim("email", "user5@example.com");
                    builder.subject(userId.toString());
                }));

        MockHttpServletRequestBuilder findAllOrdersRequestBuilder = get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "10")
                .param("offset", "0")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        MockHttpServletRequestBuilder findAllOrdersByUserId = get("/api/orders/my")
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().jwt(builder -> builder.subject(userId.toString()))
                        .authorities(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(placeOrderRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(6),
                        jsonPath("$.userEmail").value("user5@example.com"),
                        jsonPath("$.status").value(OrderStatus.PENDING.name()),
                        jsonPath("$.items", hasSize(2))
                );

        mockMvc.perform(findAllOrdersRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(6))
                );

        mockMvc.perform(findAllOrdersByUserId)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$.[0].id").value(6),
                        jsonPath("$[0].userId").value(userId.toString())
                );

    }


    @Test
    void placeOrder_DataIsValidProductsOutOfStock_ReturnsListOfOutOfStockProducts() throws Exception {
        UUID userId = UUID.randomUUID();
        TakenProductQuantity product1 = TakenProductQuantity.builder()
                .productId(202)
                .quantity(2)
                .build();

        TakenProductQuantity product2 = TakenProductQuantity.builder()
                .productId(203)
                .quantity(5)
                .build();

        OrderCreateDto orderCreateDto = OrderCreateDto.builder()
                .items(List.of(product1, product2))
                .build();

        wireMockServer1.stubFor(
                WireMock.post(WireMock.urlPathMatching("/api/inventories/items/take")).willReturn(
                        WireMock.status(409)
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                            "message": "Not enough items in stock!",
                                            "code": 409,
                                            "outStockProducts": [
                                                {
                                                    "productId": 202,
                                                    "availableQuantity": 1,
                                                    "requiredQuantity": 2
                                                },
                                                {
                                                    "productId": 203,
                                                    "availableQuantity": 3,
                                                    "requiredQuantity": 5
                                                }
                                            ]
                                        }
                                        """
                                )
                ));

        MockHttpServletRequestBuilder requestBuilder = post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderCreateDto))
                .with(jwt().jwt(builder -> {
                    builder.claim("email", "user5@example.com");
                    builder.subject(userId.toString());
                }));


        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.message").value("Not enough items in stock!"),
                        jsonPath("$.outStockProducts", hasSize(2))
                );


    }

    @Test
    void placeOrder_DataIsValidProductsInStockProductsNotFound_ReturnsBadRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        TakenProductQuantity product1 = TakenProductQuantity.builder()
                .productId(202)
                .quantity(2)
                .build();

        TakenProductQuantity product2 = TakenProductQuantity.builder()
                .productId(203)
                .quantity(1)
                .build();

        OrderCreateDto orderCreateDto = OrderCreateDto.builder()
                .items(List.of(product1, product2))
                .build();

        wireMockServer1.stubFor(
                WireMock.post(WireMock.urlPathMatching("/api/inventories/items/take")).willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        [
                                          {
                                            "orderId": 1,
                                            "productId": 202,
                                            "productName": "Logitech G102",
                                            "productDescription": "Budget gaming mouse",
                                            "productPrice": 29.99,
                                            "status": "AVAILABLE",
                                            "quantity": 2,
                                            "inventoryId": 1
                                          },
                                          {
                                            "orderId": 1,
                                            "productId": 203,
                                            "productName": "Samsung Galaxy S21",
                                            "productDescription": "Flagship phone with great camera",
                                            "productPrice": 799.99,
                                            "status": "OUT_OF_STOCK",
                                            "quantity": 1,
                                            "inventoryId": 2
                                          }
                                        ]
                                        """
                                )
                ));

        wireMockServer2.stubFor(
                WireMock.post(WireMock.urlPathMatching("/api/products/batch")).willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                          "products": [
                                            {
                                              "id": 202,
                                              "name": "Logitech G102",
                                              "description": "Budget gaming mouse",
                                              "price": 29.99,
                                              "status": "AVAILABLE"
                                            }
                                          ],
                                          "errors": [
                                            {
                                              "message": "Product with id 203 not found",
                                              "status": 400
                                            }
                                          ]
                                        }
                                        """)
                ));

        MockHttpServletRequestBuilder placeOrderRequestBuilder = post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderCreateDto))
                .with(jwt().jwt(builder -> {
                    builder.claim("email", "user5@example.com");
                    builder.subject(userId.toString());
                }));

        MockHttpServletRequestBuilder findAllOrdersRequestBuilder = get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageSize", "10")
                .param("offset", "0")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        MockHttpServletRequestBuilder findAllOrdersByUserId = get("/api/orders/my")
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().jwt(builder -> builder.subject(userId.toString()))
                        .authorities(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(placeOrderRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value(containsString("Product with id 203 not found")),
                        jsonPath("$.status").value(400)
                );

        mockMvc.perform(findAllOrdersRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(5))
                );

        mockMvc.perform(findAllOrdersByUserId)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(0))
                );

    }

    @Test
    void cancelOrder_OrderIdIsValidAndInventoryItemsAreValid_ReturnsCanceledOrder() throws Exception {

        wireMockServer1.stubFor(
                WireMock.post(WireMock.urlPathMatching("/api/inventories/items/return")).willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                            "message": "Products returned successfully",
                                            "status": 200
                                        }
                                        """)
                )
        );

        MockHttpServletRequestBuilder cancelOrderRequestBuilder = patch("/api/orders/1/cancel")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(cancelOrderRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.status").value(OrderStatus.CANCELED.name())
                );

    }

    @Test
    void cancelOrder_OrderIdIsValidAndInventoryItemsAreValidAndOrderAlreadyCanceled_ReturnsBadRequest() throws Exception {


        MockHttpServletRequestBuilder cancelOrderRequestBuilder = patch("/api/orders/2/cancel")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(cancelOrderRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Order with id 2 is already canceled"),
                        jsonPath("$.status").value(400)
                );

    }

    @Test
    void cancelOrder_OrderIdIsInvalid_ReturnsBadRequest() throws Exception {

        MockHttpServletRequestBuilder cancelOrderRequestBuilder = patch("/api/orders/10/cancel")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(cancelOrderRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Order with id 10 not found"),
                        jsonPath("$.status").value(400)
                );
    }

    @Test
    void confirmOrder_OrderIdIsValid_ReturnsOrderSummary() throws Exception {

        MockHttpServletRequestBuilder confirmOrderRequestBuilder = patch("/api/orders/1/confirm")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(confirmOrderRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.status").value(OrderStatus.CONFIRMED.name())
                );
    }

    @Test
    void confirmOrder_OrderIdIsInvalid_ReturnsBadRequest() throws Exception {

        MockHttpServletRequestBuilder confirmOrderRequestBuilder = patch("/api/orders/10/confirm")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(confirmOrderRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Order with id 10 not found"),
                        jsonPath("$.status").value(400)
                );
    }

    @Test
    void confirmOrder_OrderIdIsValidOrderHasTheSameStatus_ReturnsBadRequest() throws Exception {

        MockHttpServletRequestBuilder confirmOrderRequestBuilder = patch("/api/orders/3/confirm")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(confirmOrderRequestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Order with id 3 already has status CONFIRMED"),
                        jsonPath("$.status").value(400)
                );
    }


    @Test
    void findAllOrders_UserAuthorized_ReturnsOrderList() throws Exception {

        MockHttpServletRequestBuilder findAllOrdersRequestBuilder = get("/api/orders")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(findAllOrdersRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(5))
                );
    }

    @Test
    void findAllOrders_UserUnauthorized_Returns403Status() throws Exception {

        MockHttpServletRequestBuilder findAllOrdersRequestBuilder = get("/api/orders")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(findAllOrdersRequestBuilder)
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void findOrderById_OrderIdIsValidUserAuthorized_ReturnsOrderDetails() throws Exception {

        MockHttpServletRequestBuilder findAllOrdersRequestBuilder = get("/api/orders/5")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));


        wireMockServer2.stubFor(
                WireMock.post(WireMock.urlPathMatching("/api/products/batch")).willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                          "products": [
                                            {
                                              "id": 9,
                                              "name": "Logitech G102",
                                              "description": "Budget gaming mouse",
                                              "price": 29.99,
                                              "status": "AVAILABLE"
                                            },
                                            {
                                              "id": 10,
                                              "name": "Samsung Galaxy S21",
                                              "description": "Flagship phone with great camera",
                                              "price": 799.99,
                                              "status": "OUT_OF_STOCK"
                                            }
                                          ]
                                        }
                                        """)
                ));

        mockMvc.perform(findAllOrdersRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.items", hasSize(2))
                );

    }

    @Test
    void findOrderById_OrderIdIsValidUserAuthorized_Returns403Status() throws Exception {

        MockHttpServletRequestBuilder findAllOrdersRequestBuilder = get("/api/orders/5")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")));


        mockMvc.perform(findAllOrdersRequestBuilder)
                .andExpectAll(
                        status().isForbidden()
                );

    }

    @Test
    void findUserOrders_ReturnsOrderList() throws Exception {

        MockHttpServletRequestBuilder findAllOrdersRequestBuilder = get("/api/orders/my")
                .with(jwt().jwt(builder -> {
                    builder.subject("550e8400-e29b-41d4-a716-446655440000");
                    builder.claim("email", "user1@example.com");
                }).authorities(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(findAllOrdersRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(2))
                );
    }

}