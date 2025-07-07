package net.joelfernandes.OrderManagementSystem.domain.booking.model.dto;

import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingDTOMapper {
    Booking toBooking(BookingDTO bookingDTO);
}
