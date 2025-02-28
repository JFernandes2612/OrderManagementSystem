package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.joelfernandes.OrderManagementSystem.application.order.in.SaveOrderUseCase;
import net.joelfernandes.OrderManagementSystem.avro.OrderInput;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.OrderBasicListener;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.kafka.mapper.SchemaOrderInputMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderKafkaListener implements OrderBasicListener<Message<OrderInput>> {
    private final SaveOrderUseCase saveOrderUseCase;

    @KafkaListener(topics = topic, containerFactory = "orderListenerContainerFactory")
    @Override
    public void receive(Message<OrderInput> in) {
        saveOrderUseCase.receiveOrder(
                SchemaOrderInputMapper.INSTANCE.toOrderInput(in.getPayload()));
    }
}
