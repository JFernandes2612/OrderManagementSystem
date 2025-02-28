package net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.entities;

import java.util.List;
import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface BookingEntityMapper {
    BookingEntityMapper INSTANCE = Mappers.getMapper(BookingEntityMapper.class);

    Booking toBooking(BookingEntity bookingEntity);

    List<Booking> toBookingList(List<BookingEntity> bookingEntities);
}
