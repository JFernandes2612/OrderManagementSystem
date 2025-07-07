package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.rabbitmq;

import net.joelfernandes.OrderManagementSystem.OrderManagementSystemIntegrationTests;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
class OrderRabbitListenerIntegrationTests extends OrderManagementSystemIntegrationTests {
    @Autowired private RabbitTemplate rabbitTemplate;

    @Autowired private RabbitAdmin rabbitAdmin;

    @Autowired private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    @Test
    void shouldSaveOrder() {
        this.rabbitTemplate.convertAndSend(
                "inOrder", "{\"id\":\"1\",\"description\":\"Test Order\"}");
    }
}
