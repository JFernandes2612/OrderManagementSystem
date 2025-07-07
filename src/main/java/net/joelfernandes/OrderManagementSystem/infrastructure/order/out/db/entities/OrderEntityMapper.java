package net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities;

import java.util.List;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderEntityMapper {
    Order toOrder(OrderEntity order);

    List<Order> toOrderList(List<OrderEntity> orders);
}
