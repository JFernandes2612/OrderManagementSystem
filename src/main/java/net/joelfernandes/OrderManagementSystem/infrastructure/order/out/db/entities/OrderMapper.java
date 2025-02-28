package net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities;

import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderEntity toOrderEntity(Order order);
}
