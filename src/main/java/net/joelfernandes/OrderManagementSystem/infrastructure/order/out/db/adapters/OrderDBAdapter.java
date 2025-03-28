package net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.adapters;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.repository.OrderRepository;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderEntity;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderEntityMapper;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderMapper;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.repositories.OrderJPARepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Transactional
@Component
public class OrderDBAdapter implements OrderRepository {
    private final OrderJPARepository repository;

    @Override
    public Optional<Order> saveOrder(Order order) {
        OrderEntity orderEntity = OrderMapper.INSTANCE.toOrderEntity(order);
        return Optional.ofNullable(
                OrderEntityMapper.INSTANCE.toOrder(repository.save(orderEntity)));
    }

    @Override
    public Optional<Order> findOrderById(String orderId) {
        Optional<OrderEntity> order = repository.findById(orderId);
        return order.map(OrderEntityMapper.INSTANCE::toOrder);
    }

    @Override
    public List<Order> findAllOrders() {
        List<OrderEntity> orders = repository.findAll();
        return OrderEntityMapper.INSTANCE.toOrderList(orders);
    }
}
