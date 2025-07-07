package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.joelfernandes.OrderManagementSystem.application.order.in.SaveOrderUseCase;
import net.joelfernandes.OrderManagementSystem.application.order.in.model.OrderInput;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.OrderBasicListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@RabbitListener(queues = "${ordman.rabbitmq.topic}")
public class OrderRabbitListener implements OrderBasicListener<String> {
    private final SaveOrderUseCase saveOrderUseCase;

    @RabbitHandler
    @Override
    public void receive(String in) {
        OrderInput order = null;
        try {
            order = new ObjectMapper().readValue(in, OrderInput.class);
        } catch (JsonProcessingException e) {
            log.error("'%s' is invalid for Order Object.".formatted(in));
        }
        saveOrderUseCase.receiveOrder(order);
    }
}
