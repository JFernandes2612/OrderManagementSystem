package net.joelfernandes.OrderManagementSystem.infrastructure.booking.in.rest;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.joelfernandes.OrderManagementSystem.application.booking.in.GetBookingsUseCase;
import net.joelfernandes.OrderManagementSystem.application.booking.in.SaveBookingUseCase;
import net.joelfernandes.OrderManagementSystem.application.booking.in.model.BookingInput;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("booking")
public class BookingController {

    private final SaveBookingUseCase saveBookingUseCase;
    private final GetBookingsUseCase getBookingsUseCase;

    @GetMapping()
    public @ResponseBody List<Booking> getBookings() {
        return getBookingsUseCase.getAllBookings();
    }

    @PostMapping("save")
    public @ResponseBody ResponseEntity<Booking> saveBooking(
            @Valid @RequestBody BookingInput bookingInput) {
        return saveBookingUseCase
                .receiveBooking(bookingInput)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
}
