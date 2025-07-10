package net.joelfernandes.ordermanagementsystem.infrastructure.booking.out.db.entities;

import net.joelfernandes.ordermanagementsystem.domain.booking.model.Booking;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {
    BookingEntity toBookingEntity(Booking booking);
}
