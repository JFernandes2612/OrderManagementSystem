package net.joelfernandes.ordermanagementsystem.infrastructure.order.in.eventqueuelistener.impl.rabbitmq;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.joelfernandes.ordermanagementsystem.application.order.in.SaveOrderUseCase;
import net.joelfernandes.ordermanagementsystem.application.order.in.model.OrderInput;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.in.eventqueuelistener.OrderBasicListener;
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

        ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        try {
            order = objectMapper.readValue(in, OrderInput.class);
        } catch (JsonProcessingException e) {
            log.error("'{}' is invalid for Order Object due to '{}'.", in, e.getMessage());
            return;
        }

        saveOrderUseCase.receiveOrder(order);
    }
}
