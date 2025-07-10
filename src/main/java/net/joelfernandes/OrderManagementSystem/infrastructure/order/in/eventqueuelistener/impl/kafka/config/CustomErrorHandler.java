package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.kafka.config;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.BackOffExecution;

@Slf4j
public class CustomErrorHandler extends DefaultErrorHandler {
    protected BackOffExecution backOffExecution;

    public CustomErrorHandler(BackOff backOff) {
        super(backOff);
        backOffExecution = backOff.start();
    }

    @Override
    public void handleRemaining(
            Exception thrownException,
            List<ConsumerRecord<?, ?>> records,
            Consumer<?, ?> consumer,
            MessageListenerContainer container) {
        long interval = backOffExecution.nextBackOff();
        if (interval > 0) {
            log.warn(
                    "Error occurred consuming message in topic {} at offset {}: {}. Retrying in {} milliseconds.",
                    records.getFirst().topic(),
                    records.getFirst().offset(),
                    thrownException.getMessage(),
                    interval);
        } else {
            log.error(
                    "Error occurred consuming message in topic {} at offset {}: {}. Retries exhausted!",
                    records.getFirst().topic(),
                    records.getFirst().offset(),
                    thrownException.getMessage());
        }
        super.handleRemaining(thrownException, records, consumer, container);
    }
}
