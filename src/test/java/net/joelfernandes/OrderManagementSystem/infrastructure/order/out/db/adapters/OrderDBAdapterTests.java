package net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.model.OrderLine;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderEntity;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderEntityMapper;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderLineEntity;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderMapper;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.repositories.OrderJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderDBAdapterTests {
    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER_ID_1 = "orderId1";
    private static final String ORDER_ID_2 = "orderId1";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final LocalDateTime ORDER_DATE = LocalDateTime.now();

    @Mock private OrderJPARepository orderJPARepository;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderEntityMapper orderEntityMapper;

    private OrderDBAdapter orderDBAdapter;

    @BeforeEach
    void setUp() {
        this.orderDBAdapter =
                new OrderDBAdapter(orderJPARepository, orderMapper, orderEntityMapper);
    }

    @Test
    void shouldSaveAndFindOrder() {
        // given
        Order order = getOrder(ORDER_ID_1);
        OrderEntity orderEntity = getOrderEntity(ORDER_ID_1);

        when(orderMapper.toOrderEntity(order)).thenReturn(orderEntity);
        when(orderJPARepository.save(orderEntity)).thenReturn(orderEntity);
        when(orderEntityMapper.toOrder(orderEntity)).thenReturn(order);

        // when
        Optional<Order> savedOrderOptional = orderDBAdapter.saveOrder(order);

        // then
        assertTrue(savedOrderOptional.isPresent());
        Order savedOrder = savedOrderOptional.get();
        checkOrder(savedOrder, ORDER_ID_1);
    }

    @Test
    void shouldFindOrderById() {
        // given
        Order order = getOrder(ORDER_ID_1);
        OrderEntity orderEntity = getOrderEntity(ORDER_ID_1);

        when(orderJPARepository.findById(ORDER_ID_1)).thenReturn(Optional.of(orderEntity));
        when(orderEntityMapper.toOrder(orderEntity)).thenReturn(order);

        // when
        Optional<Order> foundOrderOptional = orderDBAdapter.findOrderById(ORDER_ID_1);

        // then
        assertTrue(foundOrderOptional.isPresent());
        Order foundOrder = foundOrderOptional.get();
        checkOrder(foundOrder, ORDER_ID_1);
    }

    @Test
    void shouldFindAllOrders() {
        // given
        Order order1 = getOrder(ORDER_ID_1);
        Order order2 = getOrder(ORDER_ID_2);
        List<OrderEntity> orderEntities =
                List.of(getOrderEntity(ORDER_ID_1), getOrderEntity(ORDER_ID_2));

        when(orderJPARepository.findAll()).thenReturn(orderEntities);
        when(orderEntityMapper.toOrderList(orderEntities)).thenReturn(List.of(order1, order2));

        // when
        List<Order> allOrders = orderDBAdapter.findAllOrders();

        // then
        assertEquals(2, allOrders.size());
        checkOrder(allOrders.getFirst(), ORDER_ID_1);
        checkOrder(allOrders.getLast(), ORDER_ID_2);
    }

    private static Order getOrder(String orderId) {
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
                .orderId(orderId)
                .orderDate(ORDER_DATE)
                .customerName(ORDER_CUSTOMER_NAME)
                .orderLines(List.of(orderLine1, orderLine2))
                .build();
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
                .orderId(orderId)
                .orderDate(ORDER_DATE)
                .customerName(ORDER_CUSTOMER_NAME)
                .orderLines(List.of(orderLineEntity1, orderLineEntity2))
                .build();
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
}
