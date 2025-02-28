package net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities;

import java.util.List;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface OrderEntityMapper {
    OrderEntityMapper INSTANCE = Mappers.getMapper(OrderEntityMapper.class);

    Order toOrder(OrderEntity order);

    List<Order> toOrderList(List<OrderEntity> orders);
}
