package net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.entities;

import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    BookingEntity toBookingEntity(Booking booking);
}
