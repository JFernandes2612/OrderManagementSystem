package net.joelfernandes.OrderManagementSystem.application.order.in.model;

import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderInputMapper {
    Order toOrder(OrderInput orderInput);
}
