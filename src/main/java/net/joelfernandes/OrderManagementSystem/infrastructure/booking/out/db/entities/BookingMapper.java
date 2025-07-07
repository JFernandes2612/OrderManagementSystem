package net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.entities;

import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {
    BookingEntity toBookingEntity(Booking booking);
}
