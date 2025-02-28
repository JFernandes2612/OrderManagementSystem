package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.kafka.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.joelfernandes.OrderManagementSystem.application.order.in.model.OrderInput;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface SchemaOrderInputMapper {
    SchemaOrderInputMapper INSTANCE = Mappers.getMapper(SchemaOrderInputMapper.class);

    OrderInput toOrderInput(net.joelfernandes.OrderManagementSystem.avro.OrderInput orderInput);

    default String toStringValue(CharSequence s) {
        return s.toString();
    }

    default Date toDate(CharSequence s) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(s.toString());
        } catch (ParseException e) {
            return new Date(0);
        }
    }
}
