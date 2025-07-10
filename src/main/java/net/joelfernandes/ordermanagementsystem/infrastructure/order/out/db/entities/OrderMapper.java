package net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities;

import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderMapper {
    OrderEntity toOrderEntity(Order order);
}
