package net.joelfernandes.OrderManagementSystem.domain.booking.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.dto.BookingDTO;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.dto.BookingDTOMapper;
import net.joelfernandes.OrderManagementSystem.domain.booking.repository.BookingRepository;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final OrderRepository orderRepository;

    public Optional<Booking> saveBooking(BookingDTO booking) {
        if (bookingRepository.findByBookingId(booking.getBookingId()).isPresent()) {
            log.error("Booking with id '{}' already exists!", booking.getBookingId());
            return Optional.empty();
        }

        Optional<Order> order = orderRepository.findOrderById(booking.getOrderId());

        if (order.isEmpty()) {
            log.error(
                    "Order '{}' to create booking with id '{}' does not exists!",
                    booking.getOrderId(),
                    booking.getBookingId());
            return Optional.empty();
        }

        Optional<Booking> bookingWithSameOrderId =
                bookingRepository.findByOrderId(booking.getOrderId());

        if (bookingWithSameOrderId.isPresent()) {
            log.error(
                    "Order '{}' is already assigned to booking with id '{}'!",
                    bookingWithSameOrderId.get().getOrder().getOrderId(),
                    bookingWithSameOrderId.get().getBookingId());
            return Optional.empty();
        }

        Booking bookingDomain = BookingDTOMapper.INSTANCE.toBooking(booking);
        bookingDomain.setOrder(order.get());
        return bookingRepository.saveBooking(bookingDomain);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAllBookings();
    }
}
