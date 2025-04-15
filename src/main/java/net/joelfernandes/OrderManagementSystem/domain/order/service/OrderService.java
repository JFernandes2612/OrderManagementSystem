package net.joelfernandes.OrderManagementSystem.domain.order.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public Optional<Order> saveOrder(Order order) {
        if (orderRepository.findOrderById(order.getOrderId()).isPresent()) {
            log.error("Order with id '{}' already exists!", order.getOrderId());
            return Optional.empty();
        }

        return orderRepository.saveOrder(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllOrders();
    }
}
