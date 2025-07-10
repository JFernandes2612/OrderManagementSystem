package net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.adapters;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import net.joelfernandes.ordermanagementsystem.domain.order.repository.OrderRepository;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities.OrderEntity;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities.OrderEntityMapper;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities.OrderMapper;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.repositories.OrderJPARepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Transactional
@Component
public class OrderDBAdapter implements OrderRepository {
    private final OrderJPARepository orderJPARepository;
    private final OrderMapper orderMapper;
    private final OrderEntityMapper orderEntityMapper;

    @Override
    public Optional<Order> saveOrder(Order order) {
        OrderEntity orderEntity = orderMapper.toOrderEntity(order);
        return Optional.ofNullable(orderEntityMapper.toOrder(orderJPARepository.save(orderEntity)));
    }

    @Override
    public Optional<Order> findOrderById(String orderId) {
        Optional<OrderEntity> order = orderJPARepository.findById(orderId);
        return order.map(orderEntityMapper::toOrder);
    }

    @Override
    public List<Order> findAllOrders() {
        List<OrderEntity> orders = orderJPARepository.findAll();
        return orderEntityMapper.toOrderList(orders);
    }
}
