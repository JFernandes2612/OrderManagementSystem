package net.joelfernandes.OrderManagementSystem.application.booking.in;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import net.joelfernandes.OrderManagementSystem.application.booking.in.model.BookingInput;
import net.joelfernandes.OrderManagementSystem.application.booking.in.model.BookingInputMapper;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.dto.BookingDTO;
import net.joelfernandes.OrderManagementSystem.domain.booking.service.BookingService;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.model.OrderLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaveBookingUseCaseTests {
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

    @Mock private BookingService bookingService;
    @Mock private BookingInputMapper bookingInputMapper;

    private SaveBookingUseCase saveBookingUseCase;

    @BeforeEach
    void setUp() {
        this.saveBookingUseCase = new SaveBookingUseCase(bookingService, bookingInputMapper);
    }

    @Test
    public void shouldReturnOptionalWithBookingWhenBookingIsSaved() {
        // given
        BookingInput bookingInput = getBookingInput();
        Booking booking = getBooking();

        when(bookingInputMapper.toBookingDTO(bookingInput)).thenReturn(getBookingDto());
        when(bookingService.saveBooking(any(BookingDTO.class)))
                .thenReturn(Optional.ofNullable(booking));

        // when
        Optional<Booking> savedBooking = saveBookingUseCase.saveBooking(bookingInput);

        // then
        assertTrue(savedBooking.isPresent());
        assertEquals(booking, savedBooking.get());
    }

    @Test
    public void shouldReturnEmptyOptionalWhenBookingIsNotSaved() {
        // given
        BookingInput bookingInput = getBookingInput();

        when(bookingInputMapper.toBookingDTO(bookingInput)).thenReturn(getBookingDto());
        when(bookingService.saveBooking(any(BookingDTO.class))).thenReturn(Optional.empty());

        // when
        Optional<Booking> savedBooking = saveBookingUseCase.saveBooking(bookingInput);

        // then
        assertTrue(savedBooking.isEmpty());
    }

    private static BookingInput getBookingInput() {
        return BookingInput.builder()
                .bookingId(BOOKING_ID)
                .customerCode(BOOKING_CUSTOMER_CODE)
                .supplierCode(BOOKING_SUPPLIER_CODE)
                .factoryCode(BOOKING_FACTORY_CODE)
                .orderId(ORDER_ID)
                .build();
    }

    private static BookingDTO getBookingDto() {
        return BookingDTO.builder()
                .bookingId(BOOKING_ID)
                .customerCode(BOOKING_CUSTOMER_CODE)
                .supplierCode(BOOKING_SUPPLIER_CODE)
                .factoryCode(BOOKING_FACTORY_CODE)
                .orderId(ORDER_ID)
                .build();
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
