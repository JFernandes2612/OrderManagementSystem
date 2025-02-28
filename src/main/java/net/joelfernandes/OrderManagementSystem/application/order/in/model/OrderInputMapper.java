package net.joelfernandes.OrderManagementSystem.application.order.in.model;

import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface OrderInputMapper {
    OrderInputMapper INSTANCE = Mappers.getMapper(OrderInputMapper.class);

    Order toOrder(OrderInput orderInput);
}
