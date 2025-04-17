package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.rest;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.joelfernandes.OrderManagementSystem.application.order.in.GetOrdersUseCase;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("order")
public class OrderController {
    private final GetOrdersUseCase getOrdersUseCase;

    @GetMapping()
    public List<Order> getAllOrders() {
        return getOrdersUseCase.getAllOrders();
    }
}
