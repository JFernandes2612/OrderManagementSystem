package net.joelfernandes.OrderManagementSystem.application.booking.in.model;

import net.joelfernandes.OrderManagementSystem.domain.booking.model.dto.BookingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface BookingInputMapper {
    BookingInputMapper INSTANCE = Mappers.getMapper(BookingInputMapper.class);

    BookingDTO toBookingDTO(BookingInput bookingInput);
}
