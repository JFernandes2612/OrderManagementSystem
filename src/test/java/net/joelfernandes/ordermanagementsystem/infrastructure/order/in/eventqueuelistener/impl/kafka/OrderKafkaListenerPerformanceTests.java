package net.joelfernandes.ordermanagementsystem.infrastructure.order.in.eventqueuelistener.impl.kafka;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static org.galaxio.gatling.kafka.javaapi.KafkaDsl.kafka;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.joelfernandes.ordermanagementsystem.avro.OrderInput;
import net.joelfernandes.ordermanagementsystem.avro.OrderLineInput;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.galaxio.gatling.kafka.javaapi.protocol.KafkaProtocolBuilder;
import org.galaxio.gatling.kafka.javaapi.request.expressions.Builders;

public class OrderKafkaListenerPerformanceTests extends Simulation {
    private static final SchemaRegistryClient client =
            new CachedSchemaRegistryClient(Arrays.asList("schRegUrl".split(",")), 16);

    private static final Iterator<Map<String, Object>> orderIdFeeder =
            Stream.generate(
                            () ->
                                    Collections.singletonMap(
                                            "orderId",
                                            (Object)
                                                    RandomStringUtils.secure()
                                                            .nextAlphanumeric(10)))
                    .iterator();
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final String ORDER_DATE_STRING = "2024-09-30T20:57:46";

    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final KafkaProtocolBuilder KAFKA_PROTOCOL_BUILDER = getOrdersProtocol();

    private static final String ORDER_TOPIC = "inOrder";

    private static final ScenarioBuilder ORDER_SCENARIO = getOrdersScenario();

    public OrderKafkaListenerPerformanceTests() {
        setUp(ORDER_SCENARIO.injectOpen(injectionStepList()))
                .protocols(KAFKA_PROTOCOL_BUILDER)
                .assertions(
                        global().responseTime().max().lte(100000),
                        global().successfulRequests().percent().gt(90d));
    }

    private static List<OpenInjectionStep> injectionStepList() {
        return List.of(rampUsersPerSec(2).to(1000).during(15));
    }

    private static ScenarioBuilder getOrdersScenario() {
        return scenario("Create orders via Kafka Scenario")
                .feed(orderIdFeeder)
                .exec(
                        kafka("Create orders via Kafka")
                                .send(
                                        "key",
                                        new Builders.AvroExpressionBuilder(
                                                session ->
                                                        getOrderInput(session.getString("orderId")),
                                                client)));
    }

    private static OrderInput getOrderInput(String orderId) {
        return OrderInput.newBuilder()
                .setOrderId(orderId)
                .setCustomerName(ORDER_CUSTOMER_NAME)
                .setOrderDate(ORDER_DATE_STRING)
                .setOrderLines(
                        List.of(
                                OrderLineInput.newBuilder()
                                        .setProductId(ORDER_LINE_PRODUCT1_ID)
                                        .setQuantity(ORDER_LINE_QUANTITY)
                                        .setPrice(ORDER_LINE_PRICE)
                                        .build(),
                                OrderLineInput.newBuilder()
                                        .setProductId(ORDER_LINE_PRODUCT2_ID)
                                        .setQuantity(ORDER_LINE_QUANTITY)
                                        .setPrice(ORDER_LINE_PRICE)
                                        .build()))
                .build();
    }

    private static KafkaProtocolBuilder getOrdersProtocol() {
        return kafka().topic(ORDER_TOPIC)
                .properties(
                        Map.of(
                                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                                "http://localhost:9092",
                                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                                "org.apache.kafka.common.serialization.StringSerializer",
                                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                                "io.confluent.kafka.serializers.KafkaAvroSerializer",
                                AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                                "http://localhost:8081",
                                AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS,
                                true,
                                "encoding-config",
                                "BINARY",
                                ProducerConfig.RETRIES_CONFIG,
                                Integer.toString(Integer.MAX_VALUE),
                                ProducerConfig.RETRY_BACKOFF_MS_CONFIG,
                                "500",
                                ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,
                                "300000"));
    }
}
