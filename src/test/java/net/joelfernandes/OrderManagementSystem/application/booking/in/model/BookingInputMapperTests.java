package net.joelfernandes.OrderManagementSystem.application.booking.in.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import net.joelfernandes.OrderManagementSystem.domain.booking.model.dto.BookingDTO;
import org.junit.jupiter.api.Test;

class BookingInputMapperTests {

    private static final String BOOKING_ID = "bookingId";
    private static final String BOOKING_CUSTOMER_CODE = "customerCode";
    private static final String BOOKING_SUPPLIER_CODE = "supplierCode";
    private static final String BOOKING_FACTORY_CODE = "factoryCode";
    private static final String BOOKING_ORDER_ID = "orderId";

    private final BookingInputMapper bookingInputMapper = new BookingInputMapperImpl();

    @Test
    void shouldMapFromBookingInputToBookingDTO() {
        // given
        BookingInput bookingInput =
                BookingInput.builder()
                        .bookingId(BOOKING_ID)
                        .customerCode(BOOKING_CUSTOMER_CODE)
                        .supplierCode(BOOKING_SUPPLIER_CODE)
                        .factoryCode(BOOKING_FACTORY_CODE)
                        .orderId(BOOKING_ORDER_ID)
                        .build();

        // when
        BookingDTO bookingDTO = bookingInputMapper.toBookingDTO(bookingInput);

        // then
        assertEquals(BOOKING_ID, bookingDTO.getBookingId());
        assertEquals(BOOKING_CUSTOMER_CODE, bookingDTO.getCustomerCode());
        assertEquals(BOOKING_SUPPLIER_CODE, bookingDTO.getSupplierCode());
        assertEquals(BOOKING_FACTORY_CODE, bookingDTO.getFactoryCode());
        assertEquals(BOOKING_ORDER_ID, bookingDTO.getOrderId());
    }

    @Test
    void shouldMapFromNullBookingInputToNullBookingDTO() {
        // when
        BookingDTO bookingDTO = bookingInputMapper.toBookingDTO(null);

        // then
        assertNull(bookingDTO);
    }
}
