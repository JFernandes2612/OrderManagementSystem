package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.rabbitmq.config;

import static java.lang.String.format;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
    @Value("${ordman.rabbitmq.topic}")
    private String topic;

    @Value("${ordman.rabbitmq.host}")
    private String host;

    @Value("${ordman.rabbitmq.port}")
    private int port;

    @Value("${ordman.rabbitmq.username}")
    private String username;

    @Value("${ordman.rabbitmq.password}")
    private String password;

    @Bean
    Queue queue() {
        return new Queue(topic, true, false, false);
    }

    @Bean
    public ConnectionFactory connectionFactory() throws URISyntaxException {
        return new CachingConnectionFactory(
                new URI(format("amqp://%s:%s@%s:%s", username, password, host, port)));
    }

    @Bean
    public AmqpAdmin amqpAdmin() throws URISyntaxException {
        return new RabbitAdmin(connectionFactory());
    }
}
