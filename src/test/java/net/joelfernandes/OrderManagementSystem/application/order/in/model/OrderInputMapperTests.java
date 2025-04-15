package net.joelfernandes.OrderManagementSystem.application.order.in.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.model.OrderLine;
import org.junit.jupiter.api.Test;

class OrderInputMapperTests {

    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER_ID = "orderId";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final Date ORDER_DATE = new Date();

    private final OrderInputMapper orderInputMapper = OrderInputMapper.INSTANCE;

    @Test
    void shouldMapFromOrderInputToOrder() {
        // given
        OrderInput orderInput = getOrderInput();

        // when
        Order order = orderInputMapper.toOrder(orderInput);

        // then
        assertEquals(ORDER_ID, order.getOrderId());
        assertEquals(ORDER_CUSTOMER_NAME, order.getCustomerName());
        assertEquals(ORDER_DATE, order.getOrderDate());
        assertEquals(orderInput.getOrderLines().size(), order.getOrderLines().size());

        OrderLine firstOrderLine = order.getOrderLines().getFirst();
        assertEquals(ORDER_LINE_PRODUCT1_ID, firstOrderLine.getProductId());
        assertEquals(ORDER_LINE_QUANTITY, firstOrderLine.getQuantity());
        assertEquals(ORDER_LINE_PRICE, firstOrderLine.getPrice());

        OrderLine secondOrderLine = order.getOrderLines().getLast();
        assertEquals(ORDER_LINE_PRODUCT2_ID, secondOrderLine.getProductId());
        assertEquals(ORDER_LINE_QUANTITY, secondOrderLine.getQuantity());
        assertEquals(ORDER_LINE_PRICE, secondOrderLine.getPrice());
    }

    private static OrderInput getOrderInput() {
        OrderLineInput orderLineInput1 =
                OrderLineInput.builder()
                        .quantity(ORDER_LINE_QUANTITY)
                        .price(ORDER_LINE_PRICE)
                        .productId(ORDER_LINE_PRODUCT1_ID)
                        .build();
        OrderLineInput orderLineInput2 =
                OrderLineInput.builder()
                        .quantity(ORDER_LINE_QUANTITY)
                        .price(ORDER_LINE_PRICE)
                        .productId(ORDER_LINE_PRODUCT2_ID)
                        .build();

        return OrderInput.builder()
                .orderDate(ORDER_DATE)
                .orderId(ORDER_ID)
                .customerName(ORDER_CUSTOMER_NAME)
                .orderLines(List.of(orderLineInput1, orderLineInput2))
                .build();
    }
}
