package net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.model.OrderLine;
import org.junit.jupiter.api.Test;

class OrderEntityMapperTests {

    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER1_ID = "orderId1";
    private static final String ORDER2_ID = "orderId2";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final Date ORDER_DATE = new Date();

    private final OrderEntityMapper orderEntityMapper = OrderEntityMapper.INSTANCE;

    @Test
    void shouldMapOrderEntityToOrder() {
        OrderEntity orderEntity = getOrderEntity(ORDER1_ID);

        Order order = orderEntityMapper.toOrder(orderEntity);

        checkOrder(order, ORDER1_ID);
    }

    @Test
    void toOrderList() {
        OrderEntity orderEntity1 = getOrderEntity(ORDER1_ID);
        OrderEntity orderEntity2 = getOrderEntity(ORDER2_ID);

        List<OrderEntity> orderEntities = Arrays.asList(orderEntity1, orderEntity2);

        List<Order> orders = orderEntityMapper.toOrderList(orderEntities);

        assertEquals(orderEntities.size(), orders.size());

        checkOrder(orders.get(0), ORDER1_ID);
        checkOrder(orders.get(1), ORDER2_ID);
    }

    private void checkOrder(Order order, String orderId) {
        assertEquals(orderId, order.getOrderId());
        assertEquals(ORDER_CUSTOMER_NAME, order.getCustomerName());
        assertEquals(ORDER_DATE, order.getOrderDate());
        assertEquals(2, order.getOrderLines().size());

        OrderLine firstOrderLine = order.getOrderLines().getFirst();
        assertEquals(ORDER_LINE_PRODUCT1_ID, firstOrderLine.getProductId());
        assertEquals(ORDER_LINE_QUANTITY, firstOrderLine.getQuantity());
        assertEquals(ORDER_LINE_PRICE, firstOrderLine.getPrice());

        OrderLine secondOrderLine = order.getOrderLines().getLast();
        assertEquals(ORDER_LINE_PRODUCT2_ID, secondOrderLine.getProductId());
        assertEquals(ORDER_LINE_QUANTITY, secondOrderLine.getQuantity());
        assertEquals(ORDER_LINE_PRICE, secondOrderLine.getPrice());
    }

    private static OrderEntity getOrderEntity(String orderId) {
        OrderLineEntity orderLineEntity1 =
                OrderLineEntity.builder()
                        .quantity(ORDER_LINE_QUANTITY)
                        .price(ORDER_LINE_PRICE)
                        .productId(ORDER_LINE_PRODUCT1_ID)
                        .build();
        OrderLineEntity orderLineEntity2 =
                OrderLineEntity.builder()
                        .quantity(ORDER_LINE_QUANTITY)
                        .price(ORDER_LINE_PRICE)
                        .productId(ORDER_LINE_PRODUCT2_ID)
                        .build();

        return OrderEntity.builder()
                .orderDate(ORDER_DATE)
                .orderId(orderId)
                .customerName(ORDER_CUSTOMER_NAME)
                .orderLines(List.of(orderLineEntity1, orderLineEntity2))
                .build();
    }
}
