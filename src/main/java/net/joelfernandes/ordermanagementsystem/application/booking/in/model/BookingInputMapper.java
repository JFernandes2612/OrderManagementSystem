package net.joelfernandes.ordermanagementsystem.application.booking.in.model;

import net.joelfernandes.ordermanagementsystem.domain.booking.model.dto.BookingDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingInputMapper {
    BookingDTO toBookingDTO(BookingInput bookingInput);
}
