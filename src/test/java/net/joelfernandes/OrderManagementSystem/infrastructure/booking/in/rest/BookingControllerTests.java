package net.joelfernandes.OrderManagementSystem.infrastructure.booking.in.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import net.joelfernandes.OrderManagementSystem.application.booking.in.GetBookingsUseCase;
import net.joelfernandes.OrderManagementSystem.application.booking.in.SaveBookingUseCase;
import net.joelfernandes.OrderManagementSystem.application.booking.in.model.BookingInput;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.model.OrderLine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(BookingController.class)
class BookingControllerTests {
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

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetBookingsUseCase getBookingsUseCase;

    @MockitoBean private SaveBookingUseCase saveBookingUseCase;

    @Test
    void shouldReturnEmptyListWhenNoBookingsExist() throws Exception {
        // given
        when(getBookingsUseCase.getAllBookings()).thenReturn(List.of());

        // when
        ResultActions resultActions = mockMvc.perform(get("/booking"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
        verify(getBookingsUseCase, times(1)).getAllBookings();
    }

    @Test
    void shouldReturnListWithBookingsWhenBookingsExist() throws Exception {
        // given
        Booking booking = getBooking();
        List<Booking> bookingsReturning = List.of(booking);
        when(getBookingsUseCase.getAllBookings()).thenReturn(bookingsReturning);

        // when
        ResultActions resultActions = mockMvc.perform(get("/booking"));

        // then
        ObjectMapper objectMapper = new ObjectMapper();
        String bookingsJson = objectMapper.writeValueAsString(bookingsReturning);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(bookingsJson));
        verify(getBookingsUseCase, times(1)).getAllBookings();
    }

    @Test
    void shouldReturnBookingWhenSavingBookingSucceeds() throws Exception {
        // given
        Booking booking = getBooking();
        when(saveBookingUseCase.receiveBooking(any(BookingInput.class)))
                .thenReturn(Optional.of(booking));

        BookingInput bookingInput = getBookingInput();

        // when
        ObjectMapper objectMapper = new ObjectMapper();
        String bookingInputJson = objectMapper.writeValueAsString(bookingInput);
        ResultActions resultActions =
                mockMvc.perform(
                        post("/booking/save")
                                .content(bookingInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        // then
        String bookingJson = objectMapper.writeValueAsString(booking);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(bookingJson));
        verify(saveBookingUseCase, times(1)).receiveBooking(any(BookingInput.class));
    }

    @Test
    void shouldReturnBadRequestWhenSavingBookingFails() throws Exception {
        // given
        when(saveBookingUseCase.receiveBooking(any(BookingInput.class)))
                .thenReturn(Optional.empty());

        BookingInput bookingInput = getBookingInput();

        // when
        ObjectMapper objectMapper = new ObjectMapper();
        String bookingInputJson = objectMapper.writeValueAsString(bookingInput);
        ResultActions resultActions =
                mockMvc.perform(
                        post("/booking/save")
                                .content(bookingInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
        verify(saveBookingUseCase, times(1)).receiveBooking(any(BookingInput.class));
    }

    @Test
    void shouldReturnBadRequestWhenSavingBookingWithInvalidInput() throws Exception {
        // given
        BookingInput bookingInput = BookingInput.builder().build();

        // when
        ObjectMapper objectMapper = new ObjectMapper();
        String bookingInputJson = objectMapper.writeValueAsString(bookingInput);
        ResultActions resultActions =
                mockMvc.perform(
                        post("/booking/save")
                                .content(bookingInputJson)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
        verify(saveBookingUseCase, never()).receiveBooking(any(BookingInput.class));
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
