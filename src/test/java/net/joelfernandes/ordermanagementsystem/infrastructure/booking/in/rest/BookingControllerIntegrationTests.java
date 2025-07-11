package net.joelfernandes.ordermanagementsystem.infrastructure.booking.in.rest;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.LocalDateTime;
import java.util.List;
import net.joelfernandes.ordermanagementsystem.OrderManagementSystemIntegrationTests;
import net.joelfernandes.ordermanagementsystem.application.booking.in.model.BookingInput;
import net.joelfernandes.ordermanagementsystem.domain.booking.model.Booking;
import net.joelfernandes.ordermanagementsystem.domain.booking.service.BookingService;
import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import net.joelfernandes.ordermanagementsystem.domain.order.model.OrderLine;
import net.joelfernandes.ordermanagementsystem.infrastructure.booking.out.db.entities.BookingEntity;
import net.joelfernandes.ordermanagementsystem.infrastructure.booking.out.db.repositories.BookingJPARepository;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities.OrderEntity;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities.OrderLineEntity;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.repositories.OrderJPARepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class BookingControllerIntegrationTests extends OrderManagementSystemIntegrationTests {

    private static final String BOOKING_ID = "bookingId1";
    private static final String BOOKING_ID_EX = "bookingId2";
    private static final String BOOKING_CUSTOMER_CODE = "customerCode";
    private static final String BOOKING_SUPPLIER_CODE = "supplierCode";
    private static final String BOOKING_FACTORY_CODE = "factoryCode";

    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER_ID = "orderId";
    private static final String CUSTOMER_NAME = "customerName";
    private static final String ORDER_DATE_STRING = "2024-09-30T20:57:46";
    private static final LocalDateTime ORDER_DATE = LocalDateTime.parse(ORDER_DATE_STRING);

    @Autowired protected TestRestTemplate testRestTemplate;

    @Autowired private BookingJPARepository bookingJPARepository;

    @Autowired private OrderJPARepository orderJPARepository;

    @AfterEach
    void tearDown() {
        bookingJPARepository.deleteAll();
    }

    @Test
    void shouldGetEmptyListWhenNoBookingsExist() {
        // given + when
        ResponseEntity<List<Booking>> response =
                testRestTemplate.exchange(
                        "/booking",
                        HttpMethod.GET,
                        new HttpEntity<>(""),
                        new ParameterizedTypeReference<>() {});

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void shouldGetListWithOneBooking() {
        // given
        BookingEntity bookingEntity = getBookingEntity();
        bookingJPARepository.save(bookingEntity);

        // when
        ResponseEntity<List<Booking>> response =
                testRestTemplate.exchange(
                        "/booking",
                        HttpMethod.GET,
                        new HttpEntity<>(""),
                        new ParameterizedTypeReference<>() {});

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        Booking booking = response.getBody().getFirst();
        checkBooking(booking);
    }

    @Test
    void shouldSaveBooking() {
        // given
        OrderEntity orderEntity = getOrderEntity();
        orderJPARepository.save(orderEntity);

        BookingInput bookingInput =
                BookingInput.builder()
                        .bookingId(BOOKING_ID)
                        .customerCode(BOOKING_CUSTOMER_CODE)
                        .supplierCode(BOOKING_SUPPLIER_CODE)
                        .factoryCode(BOOKING_FACTORY_CODE)
                        .orderId(ORDER_ID)
                        .build();
        HttpEntity<BookingInput> requestEntity = new HttpEntity<>(bookingInput);

        // when
        ResponseEntity<Booking> response =
                testRestTemplate.exchange(
                        "/booking/save", HttpMethod.POST, requestEntity, Booking.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        checkBooking(response.getBody());
    }

    @Test
    void shouldNotSaveBookingIfAlreadyExists() {
        // given
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(BookingService.class);
        logger.addAppender(appender);

        BookingEntity bookingEntity = getBookingEntity();
        bookingJPARepository.save(bookingEntity);

        BookingInput bookingInput =
                BookingInput.builder()
                        .bookingId(BOOKING_ID)
                        .customerCode(BOOKING_CUSTOMER_CODE)
                        .supplierCode(BOOKING_SUPPLIER_CODE)
                        .factoryCode(BOOKING_FACTORY_CODE)
                        .orderId(ORDER_ID)
                        .build();
        HttpEntity<BookingInput> requestEntity = new HttpEntity<>(bookingInput);

        // when
        ResponseEntity<Booking> response =
                testRestTemplate.exchange(
                        "/booking/save", HttpMethod.POST, requestEntity, Booking.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(
                appender.list.stream()
                        .anyMatch(
                                event ->
                                        event.getFormattedMessage()
                                                        .contains(
                                                                format(
                                                                        "Booking with id '%s' already exists!",
                                                                        BOOKING_ID))
                                                && event.getLevel().equals(Level.ERROR)));
    }

    @Test
    void shouldNotSaveBookingIfOrderDoesNotExist() {
        // given
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(BookingService.class);
        logger.addAppender(appender);

        BookingInput bookingInput =
                BookingInput.builder()
                        .bookingId(BOOKING_ID)
                        .customerCode(BOOKING_CUSTOMER_CODE)
                        .supplierCode(BOOKING_SUPPLIER_CODE)
                        .factoryCode(BOOKING_FACTORY_CODE)
                        .orderId(ORDER_ID)
                        .build();
        HttpEntity<BookingInput> requestEntity = new HttpEntity<>(bookingInput);

        // when
        ResponseEntity<Booking> response =
                testRestTemplate.exchange(
                        "/booking/save", HttpMethod.POST, requestEntity, Booking.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(
                appender.list.stream()
                        .anyMatch(
                                event ->
                                        event.getFormattedMessage()
                                                        .contains(
                                                                format(
                                                                        "Order '%s' to create booking with id '%s' does not exists!",
                                                                        ORDER_ID, BOOKING_ID))
                                                && event.getLevel().equals(Level.ERROR)));
    }

    @Test
    void shouldNotSaveBookingIfOrderIsAlreadyAssignedToAnotherBooking() {
        // given
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(BookingService.class);
        logger.addAppender(appender);

        BookingEntity bookingEntity = getBookingEntity();
        bookingJPARepository.save(bookingEntity);

        BookingInput bookingInput =
                BookingInput.builder()
                        .bookingId(BOOKING_ID_EX)
                        .customerCode(BOOKING_CUSTOMER_CODE)
                        .supplierCode(BOOKING_SUPPLIER_CODE)
                        .factoryCode(BOOKING_FACTORY_CODE)
                        .orderId(ORDER_ID)
                        .build();
        HttpEntity<BookingInput> requestEntity = new HttpEntity<>(bookingInput);

        // when
        ResponseEntity<Booking> response =
                testRestTemplate.exchange(
                        "/booking/save", HttpMethod.POST, requestEntity, Booking.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(
                appender.list.stream()
                        .anyMatch(
                                event ->
                                        event.getFormattedMessage()
                                                        .contains(
                                                                format(
                                                                        "Order '%s' is already assigned to booking with id '%s'!",
                                                                        ORDER_ID, BOOKING_ID))
                                                && event.getLevel().equals(Level.ERROR)));
    }

    private void checkBooking(Booking booking) {
        assertEquals(BOOKING_ID, booking.getBookingId());
        assertEquals(BOOKING_CUSTOMER_CODE, booking.getCustomerCode());
        assertEquals(BOOKING_SUPPLIER_CODE, booking.getSupplierCode());
        assertEquals(BOOKING_FACTORY_CODE, booking.getFactoryCode());

        Order order = booking.getOrder();
        assertNotNull(order);
        assertEquals(ORDER_ID, order.getOrderId());
        assertEquals(CUSTOMER_NAME, order.getCustomerName());
        assertEquals(ORDER_DATE, order.getOrderDate());

        assertNotNull(order.getOrderLines());
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

    private static OrderEntity getOrderEntity() {
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
                .orderId(ORDER_ID)
                .orderDate(ORDER_DATE)
                .customerName(CUSTOMER_NAME)
                .orderLines(List.of(orderLineEntity1, orderLineEntity2))
                .build();
    }

    private static BookingEntity getBookingEntity() {
        return BookingEntity.builder()
                .order(getOrderEntity())
                .bookingId(BOOKING_ID)
                .customerCode(BOOKING_CUSTOMER_CODE)
                .supplierCode(BOOKING_SUPPLIER_CODE)
                .factoryCode(BOOKING_FACTORY_CODE)
                .build();
    }
}
