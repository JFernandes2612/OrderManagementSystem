package net.joelfernandes.ordermanagementsystem.infrastructure.order.in.eventqueuelistener.impl.rabbitmq;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static org.galaxio.gatling.amqp.javaapi.AmqpDsl.amqp;
import static org.galaxio.gatling.amqp.javaapi.AmqpDsl.rabbitmq;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import java.util.List;
import org.galaxio.gatling.amqp.javaapi.protocol.AmqpProtocolBuilder;

public class OrderRabbitListenerPeformanceTests extends Simulation {
    private static final AmqpProtocolBuilder AMQP_PROTOCOL_BUILDER = setupProtocolForSimulation();

    private static final String ORDER_QUEUE = "inOrder";

    private static final ScenarioBuilder ORDER_SCENARIO = getOrderScenario();

    public OrderRabbitListenerPeformanceTests() {
        setUp(ORDER_SCENARIO.injectOpen(injectionStepList()))
                .protocols(AMQP_PROTOCOL_BUILDER)
                .assertions(
                        global().responseTime().max().lte(100000),
                        global().successfulRequests().percent().gt(90d));
    }

    private static List<OpenInjectionStep> injectionStepList() {
        return List.of(rampUsersPerSec(2).to(1000).during(15));
    }

    private static ScenarioBuilder getOrderScenario() {
        return scenario("Create orders via RabbitMQ Scenario")
                .exec(
                        amqp("Create orders via RabbitMQ")
                                .publish()
                                .queueExchange(ORDER_QUEUE)
                                .textMessage(
                                        """
                                        {"orderId":"#{randomAlphanumeric(10)}","customerName":"John Doe","orderDate":"2024-09-30T20:57:46","orderLines":[{"productId":"98765","quantity":2,"price":19.99},{"productId":"54321","quantity":1,"price":99.99}]}
                                        """)
                                .contentType("text/plain"));
    }

    private static AmqpProtocolBuilder setupProtocolForSimulation() {
        return amqp().connectionFactory(
                        rabbitmq()
                                .host("localhost")
                                .port(5672)
                                .username("user")
                                .password("password")
                                .vhost("/")
                                .build())
                .usePersistentDeliveryMode();
    }
}
