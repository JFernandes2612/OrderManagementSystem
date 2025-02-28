package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.kafka.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.listener.DefaultBackOffHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.BackOffExecution;

class CustomErrorHandler extends DefaultErrorHandler {
    protected BackOffExecution backOffExecution;

    public CustomErrorHandler(BackOff backOff) {
        super(backOff);
        backOffExecution = backOff.start();
    }

    // By default, error handler doesn't catch Deserialization Errors which include no connection to
    // the Schema Registry
    // We need to override this function to have a timeout between retries on those errors
    // Make sure that NonRetryable exceptions such as InvalidConfiguration are not retried
    @Override
    public void handleOtherException(
            Exception thrownException,
            Consumer<?, ?> consumer,
            MessageListenerContainer container,
            boolean batchListener) {
        long interval = backOffExecution.nextBackOff();
        if (interval > 0) {
            logger.error(
                    thrownException,
                    "Error reading message. Retrying in " + interval + " milliseconds.");
            new DefaultBackOffHandler().onNextBackOff(container, thrownException, interval);
        } else {
            // After retrying the maximum attempts stop the container
            logger.error(
                    thrownException,
                    "Error reading message. Stopping message consumption for this topic.");
            container.stopAbnormally(() -> {});
        }
    }
}
