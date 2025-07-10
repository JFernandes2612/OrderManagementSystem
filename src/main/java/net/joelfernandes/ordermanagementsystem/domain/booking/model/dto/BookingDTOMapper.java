package net.joelfernandes.ordermanagementsystem.domain.booking.model.dto;

import net.joelfernandes.ordermanagementsystem.domain.booking.model.Booking;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingDTOMapper {
    Booking toBooking(BookingDTO bookingDTO);
}
