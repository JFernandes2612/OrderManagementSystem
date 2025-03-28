package net.joelfernandes.OrderManagementSystem.infrastructure.booking.in.rest;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.joelfernandes.OrderManagementSystem.application.booking.in.GetBookingsUseCase;
import net.joelfernandes.OrderManagementSystem.application.booking.in.SaveBookingUseCase;
import net.joelfernandes.OrderManagementSystem.application.booking.in.model.BookingInput;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("booking")
public class BookingController {

    private final SaveBookingUseCase saveBookingUseCase;
    private final GetBookingsUseCase getBookingsUseCase;

    @GetMapping()
    public List<Booking> getBookings() {
        return getBookingsUseCase.getAllBookings();
    }

    @PostMapping("save")
    public ResponseEntity<Booking> saveBooking(BookingInput bookingInput) {
        return saveBookingUseCase
                .receiveBooking(bookingInput)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
