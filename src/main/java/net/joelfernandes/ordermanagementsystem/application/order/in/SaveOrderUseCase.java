package net.joelfernandes.ordermanagementsystem.application.order.in;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.joelfernandes.ordermanagementsystem.application.order.in.model.OrderInput;
import net.joelfernandes.ordermanagementsystem.application.order.in.model.OrderInputMapper;
import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import net.joelfernandes.ordermanagementsystem.domain.order.service.OrderService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SaveOrderUseCase {
    private final OrderService orderService;
    private final OrderInputMapper orderInputMapper;

    public Optional<Order> receiveOrder(OrderInput order) {
        return orderService.saveOrder(orderInputMapper.toOrder(order));
    }
}
