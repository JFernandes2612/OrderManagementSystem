package net.joelfernandes.ordermanagementsystem.domain.order.service;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import net.joelfernandes.ordermanagementsystem.domain.order.model.OrderLine;
import net.joelfernandes.ordermanagementsystem.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {
    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER_ID = "orderId";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final LocalDateTime ORDER_DATE = LocalDateTime.now();

    @Mock private OrderRepository orderRepository;

    @Captor private ArgumentCaptor<Order> orderCaptor;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository);
    }

    @Test
    void shouldSaveOrderWhenOrderDoesNotExistYet() {
        // given
        Order order = getOrder();

        when(orderRepository.findOrderById(ORDER_ID)).thenReturn(Optional.empty());

        // when
        orderService.saveOrder(order);

        // then
        verify(orderRepository).saveOrder(orderCaptor.capture());
        Order orderSaved = orderCaptor.getValue();
        verifyOrder(orderSaved, order);
    }

    @Test
    void shouldNotSaveOrderWhenOrderAlreadyExists() {
        // given
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(OrderService.class);
        logger.addAppender(appender);
        Order order = getOrder();

        when(orderRepository.findOrderById(ORDER_ID)).thenReturn(Optional.of(order));

        // when
        orderService.saveOrder(order);

        // then
        verify(orderRepository, never()).saveOrder(any());
        assertTrue(
                appender.list.stream()
                        .anyMatch(
                                event ->
                                        event.getFormattedMessage()
                                                        .contains(
                                                                format(
                                                                        "Order with id '%s' already exists!",
                                                                        ORDER_ID))
                                                && event.getLevel().equals(Level.ERROR)));
        appender.stop();
    }

    @Test
    void shouldGetAllOrders() {
        // given
        Order order1 = getOrder();
        Order order2 = getOrder();
        when(orderRepository.findAllOrders()).thenReturn(List.of(order1, order2));

        // when
        List<Order> orders = orderService.getAllOrders();

        // then
        assertEquals(2, orders.size());
        verifyOrder(orders.get(0), order1);
        verifyOrder(orders.get(1), order2);
    }

    private void verifyOrder(Order expectedOrder, Order actualOrder) {
        assertEquals(expectedOrder.getOrderId(), actualOrder.getOrderId());
        assertEquals(expectedOrder.getCustomerName(), actualOrder.getCustomerName());
        assertEquals(expectedOrder.getOrderDate(), actualOrder.getOrderDate());
        assertEquals(expectedOrder.getOrderLines().size(), actualOrder.getOrderLines().size());
        for (int i = 0; i < expectedOrder.getOrderLines().size(); i++) {
            verifyOrderLine(
                    expectedOrder.getOrderLines().get(i), actualOrder.getOrderLines().get(i));
        }
    }

    private void verifyOrderLine(OrderLine expectedOrderLine, OrderLine actualOrderLine) {
        assertEquals(expectedOrderLine.getPrice(), actualOrderLine.getPrice());
        assertEquals(expectedOrderLine.getQuantity(), actualOrderLine.getQuantity());
        assertEquals(expectedOrderLine.getProductId(), actualOrderLine.getProductId());
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
