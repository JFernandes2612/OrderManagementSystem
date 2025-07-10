package net.joelfernandes.ordermanagementsystem.application.order.in;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import net.joelfernandes.ordermanagementsystem.domain.order.service.OrderService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrdersUseCase {
    private final OrderService orderService;

    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}
