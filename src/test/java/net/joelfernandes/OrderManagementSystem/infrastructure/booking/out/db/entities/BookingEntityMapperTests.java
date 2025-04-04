package net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.model.OrderLine;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderEntity;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderLineEntity;
import org.junit.jupiter.api.Test;

class BookingEntityMapperTests {

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
    private static final Date ORDER_DATE = new Date();

    private final BookingEntityMapper bookingEntityMapper = BookingEntityMapper.INSTANCE;

    @Test
    void shouldMapBookingEntityToBooking() {
        BookingEntity bookingEntity = getBookingEntity(BOOKING1_ID);

        Booking booking = bookingEntityMapper.toBooking(bookingEntity);

        checkBooking(booking, BOOKING1_ID);
    }

    @Test
    void shouldMapBookingEntityListToBookingList() {
        BookingEntity firstBookingEntity = getBookingEntity(BOOKING1_ID);
        BookingEntity secondBookingEntity = getBookingEntity(BOOKING2_ID);

        List<BookingEntity> bookingEntities =
                Arrays.asList(firstBookingEntity, secondBookingEntity);

        List<Booking> bookings = bookingEntityMapper.toBookingList(bookingEntities);

        assertEquals(bookingEntities.size(), bookings.size());

        checkBooking(bookings.get(0), BOOKING1_ID);
        checkBooking(bookings.get(1), BOOKING2_ID);
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
