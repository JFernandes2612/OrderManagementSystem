package net.joelfernandes.ordermanagementsystem.application.order.in.model;

import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderInputMapper {
    Order toOrder(OrderInput orderInput);
}
