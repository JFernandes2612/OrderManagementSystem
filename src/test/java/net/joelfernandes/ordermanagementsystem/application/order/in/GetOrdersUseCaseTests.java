package net.joelfernandes.ordermanagementsystem.application.order.in;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import net.joelfernandes.ordermanagementsystem.domain.order.model.OrderLine;
import net.joelfernandes.ordermanagementsystem.domain.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetOrdersUseCaseTests {
    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER_ID = "orderId";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final LocalDateTime ORDER_DATE = LocalDateTime.now();

    @Mock private OrderService orderService;

    private GetOrdersUseCase getOrdersUseCase;

    @BeforeEach
    void setUp() {
        getOrdersUseCase = new GetOrdersUseCase(orderService);
    }

    @Test
    void shouldGetOrders() {
        // given
        Order order = getOrder();
        when(orderService.getAllOrders()).thenReturn(List.of(order));

        // when
        List<Order> orders = getOrdersUseCase.getAllOrders();

        // then
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(order, orders.getFirst());
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
