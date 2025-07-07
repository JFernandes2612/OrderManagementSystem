package net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities;

import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderMapper {
    OrderEntity toOrderEntity(Order order);
}
