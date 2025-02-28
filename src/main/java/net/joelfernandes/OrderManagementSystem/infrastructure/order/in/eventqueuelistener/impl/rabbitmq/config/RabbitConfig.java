package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.rabbitmq.config;

import static net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.OrderBasicListener.topic;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
    @Bean
    Queue queue() {
        return new Queue(topic, true, false, false);
    }

    @Bean
    public ConnectionFactory connectionFactory() throws URISyntaxException {
        return new CachingConnectionFactory(new URI("amqp://user:password@localhost:5672"));
    }

    @Bean
    public AmqpAdmin amqpAdmin() throws URISyntaxException {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(topic + "Exchange", true, false);
    }

    @Bean
    public Binding orderExchangeBinding() {
        return BindingBuilder.bind(queue()).to(orderExchange()).with("from.*");
    }
}
