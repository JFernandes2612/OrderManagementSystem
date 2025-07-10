package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.kafka.config;

import java.net.ConnectException;
import net.joelfernandes.OrderManagementSystem.avro.OrderInput;
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
    public ConsumerFactory<String, OrderInput> orderConsumerFactory(
            KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.getKafkaConsumerProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderInput>
            orderListenerContainerFactory(
                    ConsumerFactory<String, OrderInput> cloudEventConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, OrderInput>();
        factory.setConsumerFactory(cloudEventConsumerFactory);
        var errorHandler =
                new CommonDelegatingErrorHandler(new CommonContainerStoppingErrorHandler());
        errorHandler.addDelegate(
                ConnectException.class,
                new CustomErrorHandler(new FixedBackOff(5000L, FixedBackOff.UNLIMITED_ATTEMPTS)));
        var exponentialBackOff = new ExponentialBackOff(1000L, 2);
        exponentialBackOff.setMaxAttempts(3);
        errorHandler.addDelegate(
                RetriableException.class, new CustomErrorHandler(exponentialBackOff));
        errorHandler.setCauseChainTraversing(true);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
