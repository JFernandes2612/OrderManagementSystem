package net.joelfernandes.ordermanagementsystem.infrastructure.order.in.eventqueuelistener.impl.rabbitmq;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import net.joelfernandes.ordermanagementsystem.OrderManagementSystemIntegrationTests;
import net.joelfernandes.ordermanagementsystem.domain.order.service.OrderService;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities.OrderEntity;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities.OrderLineEntity;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.repositories.OrderJPARepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

class OrderRabbitListenerIntegrationTests extends OrderManagementSystemIntegrationTests {
    private static final String ORDER_ID = "orderId";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final String ORDER_DATE_STRING = "2024-09-30T20:57:46";
    private static final LocalDateTime ORDER_DATE = LocalDateTime.parse(ORDER_DATE_STRING);

    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    @Value("${ordman.rabbitmq.topic}")
    private String topic;

    @Autowired private RabbitTemplate rabbitTemplate;

    @Autowired private RabbitAdmin rabbitAdmin;

    @Autowired private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    @Autowired private OrderJPARepository orderJPARepository;

    @AfterEach
    void tearDown() {
        orderJPARepository.deleteAll();
    }

    @Test
    void shouldSaveOrder() {
        // given
        String orderJson = getOrderJson();

        // when
        this.rabbitTemplate.convertAndSend(topic, orderJson);

        // then
        await().untilAsserted(() -> assertEquals(1, orderJPARepository.count()));
        OrderEntity orderEntity = orderJPARepository.findById(ORDER_ID).orElseThrow();
        assertEquals(ORDER_ID, orderEntity.getOrderId());
        assertEquals(ORDER_CUSTOMER_NAME, orderEntity.getCustomerName());
        assertEquals(ORDER_DATE_STRING, orderEntity.getOrderDate().toString());
        assertEquals(2, orderEntity.getOrderLines().size());
        assertEquals(ORDER_LINE_PRODUCT1_ID, orderEntity.getOrderLines().getFirst().getProductId());
        assertEquals(ORDER_LINE_QUANTITY, orderEntity.getOrderLines().getFirst().getQuantity());
        assertEquals(ORDER_LINE_PRICE, orderEntity.getOrderLines().getFirst().getPrice());
        assertEquals(ORDER_LINE_PRODUCT2_ID, orderEntity.getOrderLines().getLast().getProductId());
        assertEquals(ORDER_LINE_QUANTITY, orderEntity.getOrderLines().getLast().getQuantity());
        assertEquals(ORDER_LINE_PRICE, orderEntity.getOrderLines().getLast().getPrice());
    }

    @Test
    void shouldNotSaveOrderIfAlreadyExists() {
        // given
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(OrderService.class);
        logger.addAppender(appender);

        orderJPARepository.save(getOrderEntity());

        String orderJson = getOrderJson();

        // when
        this.rabbitTemplate.convertAndSend(topic, orderJson);

        // then
        await().untilAsserted(
                        () ->
                                assertThat(
                                                appender.list.stream()
                                                        .anyMatch(
                                                                event ->
                                                                        event.getFormattedMessage()
                                                                                        .contains(
                                                                                                format(
                                                                                                        "Order with id '%s' already exists!",
                                                                                                        ORDER_ID))
                                                                                && event.getLevel()
                                                                                        .equals(
                                                                                                Level
                                                                                                        .ERROR)))
                                        .isTrue());
        await().untilAsserted(() -> assertNotEquals(2, orderJPARepository.count()));
        OrderEntity orderEntity = orderJPARepository.findById(ORDER_ID).orElseThrow();
        assertEquals(ORDER_ID, orderEntity.getOrderId());
        assertEquals(ORDER_CUSTOMER_NAME, orderEntity.getCustomerName());
        assertEquals(ORDER_DATE_STRING, orderEntity.getOrderDate().toString());
        assertEquals(2, orderEntity.getOrderLines().size());
        assertEquals(ORDER_LINE_PRODUCT1_ID, orderEntity.getOrderLines().getFirst().getProductId());
        assertEquals(ORDER_LINE_QUANTITY, orderEntity.getOrderLines().getFirst().getQuantity());
        assertEquals(ORDER_LINE_PRICE, orderEntity.getOrderLines().getFirst().getPrice());
        assertEquals(ORDER_LINE_PRODUCT2_ID, orderEntity.getOrderLines().getLast().getProductId());
        assertEquals(ORDER_LINE_QUANTITY, orderEntity.getOrderLines().getLast().getQuantity());
        assertEquals(ORDER_LINE_PRICE, orderEntity.getOrderLines().getLast().getPrice());
    }

    @Test
    void shouldLogErrorForInvalidOrderJson() {
        // given
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(OrderRabbitListener.class);
        logger.addAppender(appender);

        String invalidOrderJson = "{ \"invalidField\": \"invalidValue\" }";

        // when
        this.rabbitTemplate.convertAndSend(topic, invalidOrderJson);

        // then
        await().untilAsserted(
                        () ->
                                assertThat(
                                                appender.list.stream()
                                                        .anyMatch(
                                                                event ->
                                                                        event.getMessage()
                                                                                        .contains(
                                                                                                "'{}' is invalid for Order Object due to '{}'")
                                                                                && event.getLevel()
                                                                                        .equals(
                                                                                                Level
                                                                                                        .ERROR)))
                                        .isTrue());
        await().untilAsserted(
                        () -> {
                            assertEquals(0, orderJPARepository.count());
                        });
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

    private static String getOrderJson() {
        Locale.setDefault(Locale.US);
        return """
                {
                    "orderId": "%s",
                    "customerName": "%s",
                    "orderDate": "%s",
                    "orderLines": [
                        {
                            "productId": "%s",
                            "quantity": %d,
                            "price": %.2f
                        },
                        {
                            "productId": "%s",
                            "quantity": %d,
                            "price": %.2f
                        }
                    ]
                }
                """
                .formatted(
                        ORDER_ID,
                        ORDER_CUSTOMER_NAME,
                        ORDER_DATE_STRING,
                        ORDER_LINE_PRODUCT1_ID,
                        ORDER_LINE_QUANTITY,
                        ORDER_LINE_PRICE,
                        ORDER_LINE_PRODUCT2_ID,
                        ORDER_LINE_QUANTITY,
                        ORDER_LINE_PRICE);
    }
}
