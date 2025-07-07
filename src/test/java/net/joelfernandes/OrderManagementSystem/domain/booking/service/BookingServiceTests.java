package net.joelfernandes.OrderManagementSystem.domain.booking.service;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.dto.BookingDTO;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.dto.BookingDTOMapper;
import net.joelfernandes.OrderManagementSystem.domain.booking.repository.BookingRepository;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.model.OrderLine;
import net.joelfernandes.OrderManagementSystem.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {
    private static final String BOOKING_ID = "bookingId";
    private static final String BOOKING_CUSTOMER_CODE = "customerCode";
    private static final String BOOKING_SUPPLIER_CODE = "supplierCode";
    private static final String BOOKING_FACTORY_CODE = "factoryCode";

    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER_ID = "orderId";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final Date ORDER_DATE = new Date();

    @Mock private BookingRepository bookingRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private BookingDTOMapper bookingDTOMapper;

    @Captor private ArgumentCaptor<Booking> bookingCaptor;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, orderRepository, bookingDTOMapper);
    }

    @Test
    public void
            shouldSaveBookingWhenBookingDoesNotExistAndOrdersExistsAndIsNotAssociatedWithAnyBooking() {
        // given
        BookingDTO bookingDTO = getBookingDTO();
        Order order = getOrder();
        Booking booking = getBooking(order);

        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.empty());
        when(orderRepository.findOrderById(ORDER_ID)).thenReturn(Optional.of(order));
        when(bookingRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.empty());
        when(bookingDTOMapper.toBooking(bookingDTO)).thenReturn(booking);

        // when
        bookingService.saveBooking(bookingDTO);

        // then
        verify(bookingRepository).saveBooking(bookingCaptor.capture());
        verifyBooking(bookingCaptor.getValue(), booking);
    }

    @Test
    public void shouldNotSaveBookingWhenBookingAlreadyExists() {
        // given
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(BookingService.class);
        logger.addAppender(appender);

        BookingDTO bookingDTO = getBookingDTO();
        Order order = getOrder();
        Booking booking = getBooking(order);

        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));

        // when
        bookingService.saveBooking(bookingDTO);

        // then
        verify(bookingRepository, never()).saveBooking(any());
        assertTrue(
                appender.list.stream()
                        .anyMatch(
                                event ->
                                        event.getFormattedMessage()
                                                .contains(
                                                        format(
                                                                "Booking with id '%s' already exists!",
                                                                BOOKING_ID))));
    }

    @Test
    public void shouldNotSaveBookingWhenOrderDoesNotExist() {
        // given
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(BookingService.class);
        logger.addAppender(appender);

        BookingDTO bookingDTO = getBookingDTO();

        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.empty());
        when(orderRepository.findOrderById(ORDER_ID)).thenReturn(Optional.empty());

        // when
        bookingService.saveBooking(bookingDTO);

        // then
        verify(bookingRepository, never()).saveBooking(any());
        assertTrue(
                appender.list.stream()
                        .anyMatch(
                                event ->
                                        event.getFormattedMessage()
                                                .contains(
                                                        format(
                                                                "Order '%s' to create booking with id '%s' does not exists!",
                                                                ORDER_ID, BOOKING_ID))));
    }

    @Test
    public void shouldNotSaveBookingWhenOrderIsAlreadyAssociatedWithABooking() {
        // given
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(BookingService.class);
        logger.addAppender(appender);

        BookingDTO bookingDTO = getBookingDTO();
        Order order = getOrder();
        Booking booking = getBooking(order);

        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.empty());
        when(orderRepository.findOrderById(ORDER_ID)).thenReturn(Optional.of(order));
        when(bookingRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(booking));

        // when
        bookingService.saveBooking(bookingDTO);

        // then
        verify(bookingRepository, never()).saveBooking(any());
        assertTrue(
                appender.list.stream()
                        .anyMatch(
                                event ->
                                        event.getFormattedMessage()
                                                .contains(
                                                        format(
                                                                "Order '%s' is already assigned to booking with id '%s'!",
                                                                ORDER_ID, BOOKING_ID))));
    }

    @Test
    public void shouldGetAllBookings() {
        // given
        Booking booking1 = getBooking(getOrder());
        Booking booking2 = getBooking(getOrder());
        List<Booking> bookings = List.of(booking1, booking2);

        when(bookingRepository.findAllBookings()).thenReturn(bookings);

        // when
        List<Booking> actualBookings = bookingService.getAllBookings();

        // then
        assertEquals(bookings.size(), actualBookings.size());
        for (int i = 0; i < bookings.size(); i++) {
            verifyBooking(bookings.get(i), actualBookings.get(i));
        }
    }

    private void verifyBooking(Booking expectedBooking, Booking actualBooking) {
        assertEquals(expectedBooking.getBookingId(), actualBooking.getBookingId());
        assertEquals(expectedBooking.getCustomerCode(), actualBooking.getCustomerCode());
        assertEquals(expectedBooking.getSupplierCode(), actualBooking.getSupplierCode());
        assertEquals(expectedBooking.getFactoryCode(), actualBooking.getFactoryCode());
        verifyOrder(expectedBooking.getOrder(), actualBooking.getOrder());
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

    private static BookingDTO getBookingDTO() {
        return BookingDTO.builder()
                .bookingId(BOOKING_ID)
                .customerCode(BOOKING_CUSTOMER_CODE)
                .supplierCode(BOOKING_SUPPLIER_CODE)
                .factoryCode(BOOKING_FACTORY_CODE)
                .orderId(ORDER_ID)
                .build();
    }

    private static Booking getBooking(Order order) {
        return Booking.builder()
                .order(order)
                .bookingId(BOOKING_ID)
                .customerCode(BOOKING_CUSTOMER_CODE)
                .supplierCode(BOOKING_SUPPLIER_CODE)
                .factoryCode(BOOKING_FACTORY_CODE)
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
