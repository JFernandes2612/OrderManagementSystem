package net.joelfernandes.ordermanagementsystem.application.booking.in;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.joelfernandes.ordermanagementsystem.domain.booking.model.Booking;
import net.joelfernandes.ordermanagementsystem.domain.booking.service.BookingService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetBookingsUseCase {
    private final BookingService bookingService;

    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }
}
