package net.joelfernandes.ordermanagementsystem.domain.booking.repository;

import java.util.List;
import java.util.Optional;
import net.joelfernandes.ordermanagementsystem.domain.booking.model.Booking;

public interface BookingRepository {
    Optional<Booking> saveBooking(Booking booking);

    Optional<Booking> findByBookingId(String bookingId);

    Optional<Booking> findByOrderId(String orderId);

    List<Booking> findAllBookings();
}
