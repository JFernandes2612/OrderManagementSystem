package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.kafka.config;

import io.cloudevents.CloudEvent;
import java.net.ConnectException;
import org.apache.kafka.common.errors.RetriableException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler;
import org.springframework.kafka.listener.CommonDelegatingErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
public class OrderConsumerConfig {

    @Bean
    public ConsumerFactory<String, CloudEvent> orderConsumerFactory(
            KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.getKafkaConsumerProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CloudEvent>
            orderListenerContainerFactory(
                    ConsumerFactory<String, CloudEvent> cloudEventConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, CloudEvent>();
        factory.setConsumerFactory(cloudEventConsumerFactory);
        var errorHandler =
                new CommonDelegatingErrorHandler(
                        // By default, when there's an error stop retrying
                        new CommonContainerStoppingErrorHandler());
        errorHandler.addDelegate(
                ConnectException.class,
                new CustomErrorHandler(
                        // If it can't connect to schema registry retry every 5 seconds
                        new FixedBackOff(5000L, FixedBackOff.UNLIMITED_ATTEMPTS)));
        var exponentialBackOff =
                new ExponentialBackOff(1000L, 2); // Start with 1 second and double every try
        exponentialBackOff.setMaxAttempts(5); // Fail after 5 attempts
        errorHandler.addDelegate(
                RetriableException.class, new CustomErrorHandler(exponentialBackOff));
        errorHandler.setCauseChainTraversing(true);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
