package net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.adapter;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import net.joelfernandes.OrderManagementSystem.domain.booking.repository.BookingRepository;
import net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.entities.BookingEntity;
import net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.entities.BookingEntityMapper;
import net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.entities.BookingMapper;
import net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.repositories.BookingJPARepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Transactional
@Component
public class BookingDBAdapter implements BookingRepository {
    private final BookingJPARepository bookingJPARepository;
    private final BookingMapper bookingMapper;
    private final BookingEntityMapper bookingEntityMapper;

    @Override
    public Optional<Booking> saveBooking(Booking booking) {
        BookingEntity bookingEntity = bookingMapper.toBookingEntity(booking);
        BookingEntity savedBooking = bookingJPARepository.save(bookingEntity);
        return Optional.ofNullable(bookingEntityMapper.toBooking(savedBooking));
    }

    @Override
    public Optional<Booking> findByBookingId(String bookingId) {
        Optional<BookingEntity> bookingEntity = bookingJPARepository.findById(bookingId);
        return bookingEntity.map(bookingEntityMapper::toBooking);
    }

    @Override
    public Optional<Booking> findByOrderId(String orderId) {
        Optional<BookingEntity> bookingEntity = bookingJPARepository.findByOrder_OrderId(orderId);
        return bookingEntity.map(bookingEntityMapper::toBooking);
    }

    @Override
    public List<Booking> findAllBookings() {
        List<BookingEntity> bookingEntities = bookingJPARepository.findAll();
        return bookingEntityMapper.toBookingList(bookingEntities);
    }
}
