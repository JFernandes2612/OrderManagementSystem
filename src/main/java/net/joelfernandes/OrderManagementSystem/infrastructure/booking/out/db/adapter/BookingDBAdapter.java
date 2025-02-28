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

    @Override
    public Booking saveBooking(Booking booking) {
        BookingEntity bookingEntity = BookingMapper.INSTANCE.toBookingEntity(booking);
        BookingEntity savedBooking = bookingJPARepository.save(bookingEntity);
        return BookingEntityMapper.INSTANCE.toBooking(savedBooking);
    }

    @Override
    public Optional<Booking> findByBookingId(String bookingId) {
        Optional<BookingEntity> bookingEntity = bookingJPARepository.findById(bookingId);
        return bookingEntity.map(BookingEntityMapper.INSTANCE::toBooking);
    }

    @Override
    public Optional<Booking> findByOrderId(String orderId) {
        Optional<BookingEntity> bookingEntity = bookingJPARepository.findByOrder_OrderId(orderId);
        return bookingEntity.map(BookingEntityMapper.INSTANCE::toBooking);
    }

    @Override
    public List<Booking> findAllBookings() {
        List<BookingEntity> bookingEntities = bookingJPARepository.findAll();
        return BookingEntityMapper.INSTANCE.toBookingList(bookingEntities);
    }
}
