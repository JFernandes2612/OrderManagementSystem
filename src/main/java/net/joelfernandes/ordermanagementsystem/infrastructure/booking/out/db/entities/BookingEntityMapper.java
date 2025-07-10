package net.joelfernandes.ordermanagementsystem.infrastructure.booking.out.db.entities;

import java.util.List;
import net.joelfernandes.ordermanagementsystem.domain.booking.model.Booking;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingEntityMapper {
    Booking toBooking(BookingEntity bookingEntity);

    List<Booking> toBookingList(List<BookingEntity> bookingEntities);
}
