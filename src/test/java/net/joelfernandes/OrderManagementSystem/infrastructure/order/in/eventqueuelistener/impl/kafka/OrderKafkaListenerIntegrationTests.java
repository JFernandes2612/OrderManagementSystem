package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.kafka;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.LocalDateTime;
import java.util.List;
import net.joelfernandes.OrderManagementSystem.OrderManagementSystemIntegrationTests;
import net.joelfernandes.OrderManagementSystem.avro.OrderInput;
import net.joelfernandes.OrderManagementSystem.avro.OrderLineInput;
import net.joelfernandes.OrderManagementSystem.domain.order.service.OrderService;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.kafka.config.CustomErrorHandler;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderEntity;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderLineEntity;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.repositories.OrderJPARepository;
import org.apache.kafka.common.errors.DisconnectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@Import(KafkaTestProducerConfig.class)
@EmbeddedKafka(
        brokerProperties = "listeners=PLAINTEXT://localhost:9092",
        partitions = 1,
        topics = "${ordman.kafka.consumer.topic}")
class OrderKafkaListenerIntegrationTests extends OrderManagementSystemIntegrationTests {

    private static final String ORDER_ID = "orderId";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final String ORDER_DATE_STRING = "2024-09-30T20:57:46";
    private static final LocalDateTime ORDER_DATE = LocalDateTime.parse(ORDER_DATE_STRING);

    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    @Value("${ordman.kafka.consumer.topic}")
    private String topic;

    @MockitoSpyBean private OrderJPARepository orderJPARepository;

    @Autowired private KafkaTemplate<String, OrderInput> testProducerKafkaTemplate;

    @BeforeEach
    void setUp() {
        orderJPARepository.deleteAll();
    }

    @Test
    void shouldSaveOrder() {
        // given
        OrderInput orderInput = getOrderInput();

        // when
        this.testProducerKafkaTemplate.send(topic, orderInput);

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

        OrderInput orderInput = getOrderInput();

        // when
        this.testProducerKafkaTemplate.send(topic, orderInput);

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
    void shouldExhaustRetriesOnRetriableException() {
        // given
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(CustomErrorHandler.class);
        logger.addAppender(appender);

        // Throwing the exception here to simulate a retriable error
        doThrow(new DisconnectException("Connection refused"))
                .when(orderJPARepository)
                .save(any(OrderEntity.class));

        OrderInput orderInput = getOrderInput();

        // when
        this.testProducerKafkaTemplate.send(topic, orderInput);

        // then
        await().untilAsserted(
                        () ->
                                assertThat(
                                                appender.list.stream()
                                                        .anyMatch(
                                                                event ->
                                                                        event.getMessage()
                                                                                        .contains(
                                                                                                "Error occurred consuming message in topic {} at offset {}: {}. Retrying in {} milliseconds.")
                                                                                && event.getLevel()
                                                                                        .equals(
                                                                                                Level
                                                                                                        .WARN)))
                                        .isTrue());
        await().untilAsserted(
                        () ->
                                assertThat(
                                                appender.list.stream()
                                                        .anyMatch(
                                                                event ->
                                                                        event.getMessage()
                                                                                        .contains(
                                                                                                "Error occurred consuming message in topic {} at offset {}: {}. Retries exhausted!")
                                                                                && event.getLevel()
                                                                                        .equals(
                                                                                                Level
                                                                                                        .ERROR)))
                                        .isTrue());
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

    private static OrderInput getOrderInput() {
        return OrderInput.newBuilder()
                .setOrderId(ORDER_ID)
                .setCustomerName(ORDER_CUSTOMER_NAME)
                .setOrderDate(ORDER_DATE_STRING)
                .setOrderLines(
                        List.of(
                                OrderLineInput.newBuilder()
                                        .setProductId(ORDER_LINE_PRODUCT1_ID)
                                        .setQuantity(ORDER_LINE_QUANTITY)
                                        .setPrice(ORDER_LINE_PRICE)
                                        .build(),
                                OrderLineInput.newBuilder()
                                        .setProductId(ORDER_LINE_PRODUCT2_ID)
                                        .setQuantity(ORDER_LINE_QUANTITY)
                                        .setPrice(ORDER_LINE_PRICE)
                                        .build()))
                .build();
    }
}
