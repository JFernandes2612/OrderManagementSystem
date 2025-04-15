package net.joelfernandes.OrderManagementSystem.domain.booking.model.dto;

import static org.junit.jupiter.api.Assertions.*;

import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import org.junit.jupiter.api.Test;

class BookingDTOMapperTests {

    private static final String BOOKING_ID = "bookingId";
    private static final String BOOKING_CUSTOMER_CODE = "customerCode";
    private static final String BOOKING_SUPPLIER_CODE = "supplierCode";
    private static final String BOOKING_FACTORY_CODE = "factoryCode";
    private static final String BOOKING_ORDER_ID = "orderId";
    private final BookingDTOMapper bookingDTOMapper = BookingDTOMapper.INSTANCE;

    @Test
    void shouldMapFromBookingDTOToBooking() {
        // given
        BookingDTO bookingDTO =
                BookingDTO.builder()
                        .bookingId(BOOKING_ID)
                        .customerCode(BOOKING_CUSTOMER_CODE)
                        .supplierCode(BOOKING_SUPPLIER_CODE)
                        .factoryCode(BOOKING_FACTORY_CODE)
                        .orderId(BOOKING_ORDER_ID)
                        .build();

        // when
        Booking booking = bookingDTOMapper.toBooking(bookingDTO);

        // then
        assertEquals(BOOKING_ID, booking.getBookingId());
        assertEquals(BOOKING_CUSTOMER_CODE, booking.getCustomerCode());
        assertEquals(BOOKING_SUPPLIER_CODE, booking.getSupplierCode());
        assertEquals(BOOKING_FACTORY_CODE, booking.getFactoryCode());
        assertNull(booking.getOrder());
    }
}
