package net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.model.OrderLine;
import net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.entities.BookingEntity;
import net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.entities.BookingEntityMapper;
import net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.entities.BookingMapper;
import net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.repositories.BookingJPARepository;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderEntity;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderLineEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingDBAdapterTests {
    private static final String BOOKING1_ID = "bookingId1";
    private static final String BOOKING2_ID = "bookingId2";
    private static final String BOOKING_CUSTOMER_CODE = "customerCode";
    private static final String BOOKING_SUPPLIER_CODE = "supplierCode";
    private static final String BOOKING_FACTORY_CODE = "factoryCode";

    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER_ID = "orderId";
    private static final String CUSTOMER_NAME = "customerName";
    private static final LocalDateTime ORDER_DATE = LocalDateTime.now();

    @Mock private BookingJPARepository bookingJPARepository;
    @Mock private BookingMapper bookingMapper;
    @Mock private BookingEntityMapper bookingEntityMapper;

    private BookingDBAdapter bookingDBAdapter;

    @BeforeEach
    void setUp() {
        this.bookingDBAdapter =
                new BookingDBAdapter(bookingJPARepository, bookingMapper, bookingEntityMapper);
    }

    @Test
    void shouldSaveAndFindBooking() {
        // given
        Booking booking = getBooking(BOOKING1_ID);
        BookingEntity bookingEntity = getBookingEntity(BOOKING1_ID);

        when(bookingMapper.toBookingEntity(booking)).thenReturn(bookingEntity);
        when(bookingJPARepository.save(bookingEntity)).thenReturn(bookingEntity);
        when(bookingEntityMapper.toBooking(bookingEntity)).thenReturn(booking);

        // when
        Optional<Booking> savedBookingOptional = bookingDBAdapter.saveBooking(booking);

        // then
        assertTrue(savedBookingOptional.isPresent());
        Booking savedBooking = savedBookingOptional.get();
        checkBooking(savedBooking, BOOKING1_ID);
    }

    @Test
    void shouldFindBookingById() {
        // given
        Booking booking = getBooking(BOOKING1_ID);
        BookingEntity bookingEntity = getBookingEntity(BOOKING1_ID);

        when(bookingJPARepository.findById(BOOKING1_ID)).thenReturn(Optional.of(bookingEntity));
        when(bookingEntityMapper.toBooking(bookingEntity)).thenReturn(booking);

        // when
        Optional<Booking> foundBookingOptional = bookingDBAdapter.findByBookingId(BOOKING1_ID);

        // then
        assertTrue(foundBookingOptional.isPresent());
        Booking foundBooking = foundBookingOptional.get();
        checkBooking(foundBooking, BOOKING1_ID);
    }

    @Test
    void shouldFindBookingByOrderId() {
        // given
        Booking booking = getBooking(BOOKING1_ID);
        BookingEntity bookingEntity = getBookingEntity(BOOKING1_ID);

        when(bookingJPARepository.findByOrder_OrderId(ORDER_ID))
                .thenReturn(Optional.of(bookingEntity));
        when(bookingEntityMapper.toBooking(bookingEntity)).thenReturn(booking);

        // when
        Optional<Booking> foundBookingOptional = bookingDBAdapter.findByOrderId(ORDER_ID);

        // then
        assertTrue(foundBookingOptional.isPresent());
        Booking foundBooking = foundBookingOptional.get();
        checkBooking(foundBooking, BOOKING1_ID);
    }

    @Test
    void shouldFindAllBookings() {
        // given
        Booking booking1 = getBooking(BOOKING1_ID);
        Booking booking2 = getBooking(BOOKING2_ID);
        List<BookingEntity> bookingEntities =
                List.of(getBookingEntity(BOOKING1_ID), getBookingEntity(BOOKING2_ID));

        when(bookingJPARepository.findAll()).thenReturn(bookingEntities);
        when(bookingEntityMapper.toBookingList(bookingEntities))
                .thenReturn(List.of(booking1, booking2));

        // when
        List<Booking> allBookings = bookingDBAdapter.findAllBookings();

        // then
        assertEquals(2, allBookings.size());
        checkBooking(allBookings.getFirst(), BOOKING1_ID);
        checkBooking(allBookings.getLast(), BOOKING2_ID);
    }

    private void checkBooking(Booking booking, String bookingId) {
        assertEquals(bookingId, booking.getBookingId());
        assertEquals(BOOKING_CUSTOMER_CODE, booking.getCustomerCode());
        assertEquals(BOOKING_SUPPLIER_CODE, booking.getSupplierCode());
        assertEquals(BOOKING_FACTORY_CODE, booking.getFactoryCode());

        Order order = booking.getOrder();
        assertEquals(ORDER_ID, order.getOrderId());
        assertEquals(CUSTOMER_NAME, order.getCustomerName());
        assertEquals(ORDER_DATE, order.getOrderDate());

        assertEquals(booking.getOrder().getOrderLines().size(), order.getOrderLines().size());

        OrderLine firstOrderLine = order.getOrderLines().getFirst();
        assertEquals(ORDER_LINE_PRODUCT1_ID, firstOrderLine.getProductId());
        assertEquals(ORDER_LINE_QUANTITY, firstOrderLine.getQuantity());
        assertEquals(ORDER_LINE_PRICE, firstOrderLine.getPrice());

        OrderLine secondOrderLine = order.getOrderLines().getLast();
        assertEquals(ORDER_LINE_PRODUCT2_ID, secondOrderLine.getProductId());
        assertEquals(ORDER_LINE_QUANTITY, secondOrderLine.getQuantity());
        assertEquals(ORDER_LINE_PRICE, secondOrderLine.getPrice());
    }

    private static Booking getBooking(String bookingId) {
        OrderLine orderLine1 =
                OrderLine.builder()
                        .quantity(ORDER_LINE_QUANTITY)
                        .price(ORDER_LINE_PRICE)
                        .productId(ORDER_LINE_PRODUCT1_ID)
                        .build();
        OrderLine orderLine2 =
                OrderLine.builder()
                        .id(0L)
                        .quantity(ORDER_LINE_QUANTITY)
                        .price(ORDER_LINE_PRICE)
                        .productId(ORDER_LINE_PRODUCT2_ID)
                        .build();

        Order order =
                Order.builder()
                        .orderId(ORDER_ID)
                        .orderDate(ORDER_DATE)
                        .customerName(CUSTOMER_NAME)
                        .orderLines(List.of(orderLine1, orderLine2))
                        .build();

        return Booking.builder()
                .order(order)
                .bookingId(bookingId)
                .customerCode(BOOKING_CUSTOMER_CODE)
                .supplierCode(BOOKING_SUPPLIER_CODE)
                .factoryCode(BOOKING_FACTORY_CODE)
                .build();
    }

    private static BookingEntity getBookingEntity(String bookingId) {
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

        OrderEntity orderEntity =
                OrderEntity.builder()
                        .orderId(ORDER_ID)
                        .orderDate(ORDER_DATE)
                        .customerName(CUSTOMER_NAME)
                        .orderLines(List.of(orderLineEntity1, orderLineEntity2))
                        .build();

        return BookingEntity.builder()
                .order(orderEntity)
                .bookingId(bookingId)
                .customerCode(BOOKING_CUSTOMER_CODE)
                .supplierCode(BOOKING_SUPPLIER_CODE)
                .factoryCode(BOOKING_FACTORY_CODE)
                .build();
    }
}
