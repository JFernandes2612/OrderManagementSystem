package net.joelfernandes.ordermanagementsystem.infrastructure.order.in.rest;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.util.List;

public class OrderControllerPerformanceTests extends Simulation {
    private static final HttpProtocolBuilder HTTP_PROTOCOL_BUILDER = setupProtocolForSimulation();

    private static final String GET_ORDERS_ENDPOINT = "/order";

    private static final ScenarioBuilder GET_ORDERS_SCENARIO = getOrdersScenario();

    public OrderControllerPerformanceTests() {
        setUp(GET_ORDERS_SCENARIO.injectOpen(injectionStepList()))
                .protocols(HTTP_PROTOCOL_BUILDER)
                .assertions(
                        global().responseTime().max().lte(100000),
                        global().successfulRequests().percent().gt(90d));
    }

    private static List<OpenInjectionStep> injectionStepList() {
        return List.of(rampUsersPerSec(2).to(1000).during(15));
    }

    private static ScenarioBuilder getOrdersScenario() {
        return scenario("Get All Orders Scenario")
                .exec(
                        HttpDsl.http("Get All Orders")
                                .get(GET_ORDERS_ENDPOINT)
                                .check(HttpDsl.status().is(200)));
    }

    private static HttpProtocolBuilder setupProtocolForSimulation() {
        return HttpDsl.http
                .baseUrl("http://localhost:8080")
                .acceptHeader("application/json")
                .maxConnectionsPerHost(100)
                .userAgentHeader("Gatling/Performance Test");
    }
}
