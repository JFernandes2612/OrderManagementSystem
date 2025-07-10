package net.joelfernandes.ordermanagementsystem.infrastructure.booking.in.rest;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.joelfernandes.ordermanagementsystem.application.booking.in.GetBookingsUseCase;
import net.joelfernandes.ordermanagementsystem.application.booking.in.SaveBookingUseCase;
import net.joelfernandes.ordermanagementsystem.application.booking.in.model.BookingInput;
import net.joelfernandes.ordermanagementsystem.domain.booking.model.Booking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
    public ResponseEntity<Booking> saveBooking(@Valid @RequestBody BookingInput bookingInput) {
        return saveBookingUseCase
                .saveBooking(bookingInput)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
}
