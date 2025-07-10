package net.joelfernandes.ordermanagementsystem.domain.order.repository;

import java.util.List;
import java.util.Optional;
import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;

public interface OrderRepository {
    Optional<Order> saveOrder(Order order);

    Optional<Order> findOrderById(String orderId);

    List<Order> findAllOrders();
}
