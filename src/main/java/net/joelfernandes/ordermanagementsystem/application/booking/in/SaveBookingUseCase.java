package net.joelfernandes.ordermanagementsystem.application.booking.in;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.joelfernandes.ordermanagementsystem.application.booking.in.model.BookingInput;
import net.joelfernandes.ordermanagementsystem.application.booking.in.model.BookingInputMapper;
import net.joelfernandes.ordermanagementsystem.domain.booking.model.Booking;
import net.joelfernandes.ordermanagementsystem.domain.booking.service.BookingService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SaveBookingUseCase {
    private final BookingService bookingService;
    private final BookingInputMapper bookingInputMapper;

    public Optional<Booking> saveBooking(BookingInput booking) {
        return bookingService.saveBooking(bookingInputMapper.toBookingDTO(booking));
    }
}
