package net.joelfernandes.OrderManagementSystem.application.order.in;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import net.joelfernandes.OrderManagementSystem.domain.order.service.OrderService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrderUseCase {
    private final OrderService orderService;

    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}
