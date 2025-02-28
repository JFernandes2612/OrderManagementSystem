package net.joelfernandes.OrderManagementSystem.application.booking.in;

import lombok.RequiredArgsConstructor;
import net.joelfernandes.OrderManagementSystem.application.booking.in.model.BookingInput;
import net.joelfernandes.OrderManagementSystem.application.booking.in.model.BookingInputMapper;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import net.joelfernandes.OrderManagementSystem.domain.booking.service.BookingService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SaveBookingUseCase {
    private final BookingService bookingService;

    public Booking receiveBooking(BookingInput booking) {
        return bookingService.saveBooking(BookingInputMapper.INSTANCE.toBookingDTO(booking));
    }
}
