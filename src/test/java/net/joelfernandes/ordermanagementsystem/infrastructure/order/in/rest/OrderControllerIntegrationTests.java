package net.joelfernandes.ordermanagementsystem.infrastructure.order.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import net.joelfernandes.ordermanagementsystem.OrderManagementSystemIntegrationTests;
import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities.OrderEntity;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities.OrderLineEntity;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.repositories.OrderJPARepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class OrderControllerIntegrationTests extends OrderManagementSystemIntegrationTests {
    private static final String ORDER_ID = "orderId";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final String ORDER_DATE_STRING = "2024-09-30T20:57:46";
    private static final LocalDateTime ORDER_DATE = LocalDateTime.parse(ORDER_DATE_STRING);

    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    @Autowired protected TestRestTemplate testRestTemplate;

    @Autowired private OrderJPARepository orderJPARepository;

    @AfterEach
    void tearDown() {
        orderJPARepository.deleteAll();
    }

    @Test
    void shouldGetEmptyListWhenNoOrdersExist() {
        // given + when
        ResponseEntity<List<Order>> response =
                testRestTemplate.exchange(
                        "/order",
                        HttpMethod.GET,
                        new HttpEntity<>(""),
                        new ParameterizedTypeReference<>() {});

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void shouldGetListWithOneOrder() {
        // given
        OrderEntity orderEntity = getOrderEntity();
        orderJPARepository.save(orderEntity);

        // when
        ResponseEntity<List<Order>> response =
                testRestTemplate.exchange(
                        "/order",
                        HttpMethod.GET,
                        new HttpEntity<>(""),
                        new ParameterizedTypeReference<>() {});

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        Order order = response.getBody().getFirst();
        assertNotNull(order);
        assertEquals(ORDER_ID, order.getOrderId());
        assertEquals(ORDER_CUSTOMER_NAME, order.getCustomerName());
        assertEquals(ORDER_DATE, order.getOrderDate());
        assertNotNull(order.getOrderLines());
        assertFalse(order.getOrderLines().isEmpty());
        assertEquals(2, order.getOrderLines().size());
        assertEquals(ORDER_LINE_PRODUCT1_ID, order.getOrderLines().getFirst().getProductId());
        assertEquals(ORDER_LINE_QUANTITY, order.getOrderLines().getFirst().getQuantity());
        assertEquals(ORDER_LINE_PRICE, order.getOrderLines().getFirst().getPrice());
        assertEquals(ORDER_LINE_PRODUCT2_ID, order.getOrderLines().getLast().getProductId());
        assertEquals(ORDER_LINE_QUANTITY, order.getOrderLines().getLast().getQuantity());
        assertEquals(ORDER_LINE_PRICE, order.getOrderLines().getLast().getPrice());
    }

    private static OrderEntity getOrderEntity() {
        return OrderEntity.builder()
                .orderId(ORDER_ID)
                .customerName(ORDER_CUSTOMER_NAME)
                .orderDate(ORDER_DATE)
                .orderLines(
                        List.of(
                                OrderLineEntity.builder()
                                        .productId(ORDER_LINE_PRODUCT1_ID)
                                        .quantity(ORDER_LINE_QUANTITY)
                                        .price(ORDER_LINE_PRICE)
                                        .build(),
                                OrderLineEntity.builder()
                                        .productId(ORDER_LINE_PRODUCT2_ID)
                                        .quantity(ORDER_LINE_QUANTITY)
                                        .price(ORDER_LINE_PRICE)
                                        .build()))
                .build();
    }
}
