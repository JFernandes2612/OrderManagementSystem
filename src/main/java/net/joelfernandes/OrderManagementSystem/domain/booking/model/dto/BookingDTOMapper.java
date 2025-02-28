package net.joelfernandes.OrderManagementSystem.domain.booking.model.dto;

import net.joelfernandes.OrderManagementSystem.domain.booking.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface BookingDTOMapper {
    BookingDTOMapper INSTANCE = Mappers.getMapper(BookingDTOMapper.class);

    Booking toBooking(BookingDTO bookingDTO);
}
