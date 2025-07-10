package net.joelfernandes.ordermanagementsystem.infrastructure.order.in.eventqueuelistener.impl.kafka;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.joelfernandes.ordermanagementsystem.application.order.in.SaveOrderUseCase;
import net.joelfernandes.ordermanagementsystem.avro.OrderInput;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.in.eventqueuelistener.OrderBasicListener;
import net.joelfernandes.ordermanagementsystem.infrastructure.order.in.eventqueuelistener.impl.kafka.mapper.SchemaOrderInputMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderKafkaListener implements OrderBasicListener<Message<OrderInput>> {
    private final SaveOrderUseCase saveOrderUseCase;
    private final SchemaOrderInputMapper schemaOrderInputMapper;

    @SneakyThrows
    @KafkaListener(
            topics = "${ordman.kafka.consumer.topic}",
            containerFactory = "orderListenerContainerFactory")
    @Override
    public void receive(Message<OrderInput> in) {
        saveOrderUseCase.receiveOrder(schemaOrderInputMapper.toOrderInput(in.getPayload()));
    }
}
