package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.kafka.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import net.joelfernandes.OrderManagementSystem.application.order.in.model.OrderInput;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SchemaOrderInputMapper {
    OrderInput toOrderInput(net.joelfernandes.OrderManagementSystem.avro.OrderInput orderInput)
            throws ParseException;

    default String toStringValue(CharSequence s) {
        return s.toString();
    }

    default LocalDateTime toDate(CharSequence s) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .parse(s.toString())
                .toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
