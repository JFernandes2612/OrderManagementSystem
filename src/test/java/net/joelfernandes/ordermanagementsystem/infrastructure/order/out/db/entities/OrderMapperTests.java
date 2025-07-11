package net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import net.joelfernandes.ordermanagementsystem.domain.order.model.OrderLine;
import org.junit.jupiter.api.Test;

class OrderMapperTests {

    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER_ID = "orderId";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final LocalDateTime ORDER_DATE = LocalDateTime.now();

    private final OrderMapper orderMapper = new OrderMapperImpl();

    @Test
    void shouldMapFromOrderToOrderEntity() {
        // given
        Order order = getOrder();

        // when
        OrderEntity orderEntity = orderMapper.toOrderEntity(order);

        // then
        assertEquals(ORDER_ID, orderEntity.getOrderId());
        assertEquals(ORDER_CUSTOMER_NAME, orderEntity.getCustomerName());
        assertEquals(ORDER_DATE, orderEntity.getOrderDate());
        assertEquals(order.getOrderLines().size(), orderEntity.getOrderLines().size());

        OrderLineEntity firstOrderLineEntity = orderEntity.getOrderLines().getFirst();
        assertEquals(ORDER_LINE_PRODUCT1_ID, firstOrderLineEntity.getProductId());
        assertEquals(ORDER_LINE_QUANTITY, firstOrderLineEntity.getQuantity());
        assertEquals(ORDER_LINE_PRICE, firstOrderLineEntity.getPrice());

        OrderLineEntity secondOrderLineEntity = orderEntity.getOrderLines().getLast();
        assertEquals(ORDER_LINE_PRODUCT2_ID, secondOrderLineEntity.getProductId());
        assertEquals(ORDER_LINE_QUANTITY, secondOrderLineEntity.getQuantity());
        assertEquals(ORDER_LINE_PRICE, secondOrderLineEntity.getPrice());
    }

    @Test
    void shouldReturnNullWhenOrderIsNull() {
        // when
        OrderEntity orderEntity = orderMapper.toOrderEntity(null);

        // then
        assertNull(orderEntity);
    }

    @Test
    void shouldReturnNullOrderLineEntityListWhenOrderLinesAreNull() {
        // given
        Order order =
                Order.builder()
                        .orderId(ORDER_ID)
                        .customerName(ORDER_CUSTOMER_NAME)
                        .orderDate(ORDER_DATE)
                        .orderLines(null)
                        .build();

        // when
        OrderEntity orderEntity = orderMapper.toOrderEntity(order);

        // then
        assertEquals(ORDER_ID, orderEntity.getOrderId());
        assertEquals(ORDER_CUSTOMER_NAME, orderEntity.getCustomerName());
        assertEquals(ORDER_DATE, orderEntity.getOrderDate());
        assertNull(orderEntity.getOrderLines());
    }

    @Test
    void shouldReturnNullWhenOrderLineIsNull() {
        // given
        List<OrderLine> orderLines = new ArrayList<>();
        orderLines.add(null);
        Order order =
                Order.builder()
                        .orderId(ORDER_ID)
                        .customerName(ORDER_CUSTOMER_NAME)
                        .orderDate(ORDER_DATE)
                        .orderLines(orderLines)
                        .build();

        // when
        OrderEntity orderEntity = orderMapper.toOrderEntity(order);

        // then
        assertEquals(ORDER_ID, orderEntity.getOrderId());
        assertEquals(ORDER_CUSTOMER_NAME, orderEntity.getCustomerName());
        assertEquals(ORDER_DATE, orderEntity.getOrderDate());
        assertNotNull(orderEntity.getOrderLines());
        assertNull(orderEntity.getOrderLines().getFirst());
    }

    @Test
    void shouldMapOrderLineIdIfExists() {
        // given
        OrderLine orderLine =
                OrderLine.builder()
                        .id(1L)
                        .productId(ORDER_LINE_PRODUCT1_ID)
                        .quantity(ORDER_LINE_QUANTITY)
                        .price(ORDER_LINE_PRICE)
                        .build();
        Order order =
                Order.builder()
                        .orderId(ORDER_ID)
                        .customerName(ORDER_CUSTOMER_NAME)
                        .orderDate(ORDER_DATE)
                        .orderLines(List.of(orderLine))
                        .build();

        // when
        OrderEntity orderEntity = orderMapper.toOrderEntity(order);

        // then
        assertNotNull(orderEntity.getOrderLines());
        assertEquals(1, orderEntity.getOrderLines().size());
        assertEquals(Long.valueOf(1), orderEntity.getOrderLines().getFirst().getId());
    }

    private static Order getOrder() {
        OrderLine orderLine1 =
                OrderLine.builder()
                        .quantity(ORDER_LINE_QUANTITY)
                        .price(ORDER_LINE_PRICE)
                        .productId(ORDER_LINE_PRODUCT1_ID)
                        .build();
        OrderLine orderLine2 =
                OrderLine.builder()
                        .quantity(ORDER_LINE_QUANTITY)
                        .price(ORDER_LINE_PRICE)
                        .productId(ORDER_LINE_PRODUCT2_ID)
                        .build();

        return Order.builder()
                .orderDate(ORDER_DATE)
                .orderId(ORDER_ID)
                .customerName(ORDER_CUSTOMER_NAME)
                .orderLines(List.of(orderLine1, orderLine2))
                .build();
    }
}
