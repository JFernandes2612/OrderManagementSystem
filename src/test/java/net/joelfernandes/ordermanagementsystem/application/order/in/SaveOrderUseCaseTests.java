package net.joelfernandes.ordermanagementsystem.application.order.in;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import net.joelfernandes.ordermanagementsystem.application.order.in.model.OrderInput;
import net.joelfernandes.ordermanagementsystem.application.order.in.model.OrderInputMapper;
import net.joelfernandes.ordermanagementsystem.application.order.in.model.OrderLineInput;
import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import net.joelfernandes.ordermanagementsystem.domain.order.model.OrderLine;
import net.joelfernandes.ordermanagementsystem.domain.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaveOrderUseCaseTests {
    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER_ID = "orderId";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final LocalDateTime ORDER_DATE = LocalDateTime.now();

    @Mock private OrderService orderService;
    @Mock private OrderInputMapper orderInputMapper;

    private SaveOrderUseCase saveOrderUseCase;

    @BeforeEach
    void setUp() {
        saveOrderUseCase = new SaveOrderUseCase(orderService, orderInputMapper);
    }

    @Test
    void shouldReturnOptionalWithOrderWhenOrderIsSaved() {
        // given
        OrderInput orderInput = getOrderInput();
        Order order = getOrder();

        when(orderInputMapper.toOrder(orderInput)).thenReturn(order);
        when(orderService.saveOrder(any(Order.class))).thenReturn(Optional.ofNullable(order));

        // when
        Optional<Order> savedOrder = saveOrderUseCase.receiveOrder(orderInput);

        // then
        assertTrue(savedOrder.isPresent());
        assertEquals(order, savedOrder.get());
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
