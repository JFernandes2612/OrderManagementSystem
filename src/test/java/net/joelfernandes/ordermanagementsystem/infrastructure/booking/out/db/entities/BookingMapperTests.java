package net.joelfernandes.ordermanagementsystem.infrastructure.booking.out.db.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import net.joelfernandes.ordermanagementsystem.domain.booking.model.Booking;
import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import net.joelfernandes.ordermanagementsystem.domain.order.model.OrderLine;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities.OrderEntity;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities.OrderLineEntity;
import org.junit.jupiter.api.Test;

class BookingMapperTests {

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
    private static final LocalDateTime ORDER_DATE = LocalDateTime.now();

    private final BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void shouldMapBookingToBookingEntity() {
        // given
        Booking booking = getBooking();

        // when
        BookingEntity bookingEntity = bookingMapper.toBookingEntity(booking);

        // then
        assertEquals(BOOKING_ID, bookingEntity.getBookingId());
        assertEquals(BOOKING_CUSTOMER_CODE, bookingEntity.getCustomerCode());
        assertEquals(BOOKING_SUPPLIER_CODE, bookingEntity.getSupplierCode());
        assertEquals(BOOKING_FACTORY_CODE, bookingEntity.getFactoryCode());

        OrderEntity orderEntity = bookingEntity.getOrder();
        assertEquals(ORDER_ID, orderEntity.getOrderId());
        assertEquals(ORDER_CUSTOMER_NAME, orderEntity.getCustomerName());
        assertEquals(ORDER_DATE, orderEntity.getOrderDate());

        assertEquals(booking.getOrder().getOrderLines().size(), orderEntity.getOrderLines().size());

        OrderLineEntity firstOrderLineEntity = orderEntity.getOrderLines().getFirst();
        assertEquals(ORDER_LINE_PRODUCT1_ID, firstOrderLineEntity.getProductId());
        assertEquals(ORDER_LINE_QUANTITY, firstOrderLineEntity.getQuantity());
        assertEquals(ORDER_LINE_PRICE, firstOrderLineEntity.getPrice());

        OrderLineEntity secondOrderLineEntity = orderEntity.getOrderLines().getLast();
        assertEquals(ORDER_LINE_PRODUCT2_ID, secondOrderLineEntity.getProductId());
        assertEquals(ORDER_LINE_QUANTITY, secondOrderLineEntity.getQuantity());
        assertEquals(ORDER_LINE_PRICE, secondOrderLineEntity.getPrice());
    }

    @Test
    void shouldMapNullBookingToNullBookingEntity() {
        // given + when
        BookingEntity bookingEntity = bookingMapper.toBookingEntity(null);

        // then
        assertNull(bookingEntity);
    }

    @Test
    void shouldMapNullOrderOfBookingToNullOrderEntityOfBookingEntity() {
        // given
        Booking booking = Booking.builder().build();

        // when
        BookingEntity bookingEntity = bookingMapper.toBookingEntity(booking);

        // then
        assertNull(bookingEntity.getOrder());
    }

    @Test
    void shouldMapNullOrderLineOfBookingToNullOrderLineEntityOfBookingEntity() {
        // given
        Order order = Order.builder().build();
        Booking booking = Booking.builder().order(order).build();

        // when
        BookingEntity bookingEntity = bookingMapper.toBookingEntity(booking);

        // then
        assertNull(bookingEntity.getOrder().getOrderLines());
    }

    @Test
    void shouldMapNullOrderLineInListOfBookingToNullOrderLineEntityInListOfBookingEntity() {
        // given
        List<OrderLine> orderLines = new ArrayList<>();
        orderLines.add(null);
        Order order = Order.builder().orderLines(orderLines).build();
        Booking booking = Booking.builder().order(order).build();

        // when
        BookingEntity bookingEntity = bookingMapper.toBookingEntity(booking);

        // then
        assertNull(bookingEntity.getOrder().getOrderLines().getFirst());
    }

    private static Booking getBooking() {
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
                        .customerName(ORDER_CUSTOMER_NAME)
                        .orderLines(List.of(orderLine1, orderLine2))
                        .build();

        return Booking.builder()
                .order(order)
                .bookingId(BOOKING_ID)
                .customerCode(BOOKING_CUSTOMER_CODE)
                .supplierCode(BOOKING_SUPPLIER_CODE)
                .factoryCode(BOOKING_FACTORY_CODE)
                .build();
    }
}
