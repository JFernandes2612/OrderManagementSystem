package net.joelfernandes.OrderManagementSystem.domain.order.repository;

import java.util.List;
import java.util.Optional;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;

public interface OrderRepository {
    void saveOrder(Order order);

    Optional<Order> findOrderById(String orderId);

    List<Order> findAllOrders();
}
