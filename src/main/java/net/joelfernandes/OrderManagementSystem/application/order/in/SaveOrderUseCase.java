package net.joelfernandes.OrderManagementSystem.application.order.in;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import net.joelfernandes.OrderManagementSystem.application.order.in.model.OrderInput;
import net.joelfernandes.OrderManagementSystem.application.order.in.model.OrderInputMapper;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.service.OrderService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SaveOrderUseCase {
    private final OrderService orderService;

    public Optional<Order> receiveOrder(OrderInput order) {
        return orderService.saveOrder(OrderInputMapper.INSTANCE.toOrder(order));
    }
}
