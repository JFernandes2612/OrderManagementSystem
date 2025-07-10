package net.joelfernandes.ordermanagementsystem.infrastructure.order.in.eventqueuelistener.impl.kafka.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import net.joelfernandes.ordermanagementsystem.application.order.in.model.OrderInput;
import net.joelfernandes.ordermanagementsystem.application.order.in.model.OrderLineInput;
import org.junit.jupiter.api.Test;

class SchemaOrderInputMapperTests {

    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER_ID = "orderId";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final String ORDER_DATE_STRING = "2024-09-30T20:57:46";
    private static final LocalDateTime ORDER_DATE = LocalDateTime.parse(ORDER_DATE_STRING);

    private final SchemaOrderInputMapper schemaOrderInputMapper = new SchemaOrderInputMapperImpl();

    @Test
    void shouldMapSchemaOrderInputToOrderInput() throws ParseException {
        // given
        net.joelfernandes.ordermanagementsystem.avro.OrderInput schemaOrderInput =
                getSchemaOrderInput();

        // when
        OrderInput order = schemaOrderInputMapper.toOrderInput(schemaOrderInput);

        // then
        assertEquals(ORDER_ID, order.getOrderId());
        assertEquals(ORDER_CUSTOMER_NAME, order.getCustomerName());
        assertEquals(ORDER_DATE, order.getOrderDate());
        assertEquals(schemaOrderInput.getOrderLines().size(), order.getOrderLines().size());

        OrderLineInput firstOrderLine = order.getOrderLines().getFirst();
        assertEquals(ORDER_LINE_PRODUCT1_ID, firstOrderLine.getProductId());
        assertEquals(ORDER_LINE_QUANTITY, firstOrderLine.getQuantity());
        assertEquals(ORDER_LINE_PRICE, firstOrderLine.getPrice());

        OrderLineInput secondOrderLine = order.getOrderLines().getLast();
        assertEquals(ORDER_LINE_PRODUCT2_ID, secondOrderLine.getProductId());
        assertEquals(ORDER_LINE_QUANTITY, secondOrderLine.getQuantity());
        assertEquals(ORDER_LINE_PRICE, secondOrderLine.getPrice());
    }

    @Test
    void shouldReturnNullWhenSchemaOrderInputIsNull() throws ParseException {
        // given + when
        OrderInput order = schemaOrderInputMapper.toOrderInput(null);

        // then
        assertNull(order);
    }

    @Test
    void shouldReturnNullWhenSchemaOrderLineInputIsNull() throws ParseException {
        // given
        net.joelfernandes.ordermanagementsystem.avro.OrderInput schemaOrderInput =
                net.joelfernandes.ordermanagementsystem.avro.OrderInput.newBuilder()
                        .setOrderId(ORDER_ID)
                        .setCustomerName(ORDER_CUSTOMER_NAME)
                        .setOrderDate(ORDER_DATE_STRING)
                        .setOrderLines(List.of())
                        .build();
        schemaOrderInput.setOrderLines(null);

        // when
        OrderInput order = schemaOrderInputMapper.toOrderInput(schemaOrderInput);

        // then
        assertNotNull(order);
        assertNull(order.getOrderLines());
    }

    @Test
    void shouldReturnNullSchemaOrderLineInputWhenSchemaOrderLineInputValueInArrayIsNull()
            throws ParseException {
        // given
        List<net.joelfernandes.ordermanagementsystem.avro.OrderLineInput> orderLines =
                new ArrayList<>();
        orderLines.add(null);
        net.joelfernandes.ordermanagementsystem.avro.OrderInput schemaOrderInput =
                net.joelfernandes.ordermanagementsystem.avro.OrderInput.newBuilder()
                        .setOrderId(ORDER_ID)
                        .setCustomerName(ORDER_CUSTOMER_NAME)
                        .setOrderDate(ORDER_DATE_STRING)
                        .setOrderLines(orderLines)
                        .build();

        // when
        OrderInput order = schemaOrderInputMapper.toOrderInput(schemaOrderInput);

        // then
        assertNotNull(order);
        assertEquals(1, order.getOrderLines().size());
        assertNull(order.getOrderLines().getFirst());
    }

    private static net.joelfernandes.ordermanagementsystem.avro.OrderInput getSchemaOrderInput() {
        net.joelfernandes.ordermanagementsystem.avro.OrderLineInput orderLineInput1 =
                net.joelfernandes.ordermanagementsystem.avro.OrderLineInput.newBuilder()
                        .setQuantity(ORDER_LINE_QUANTITY)
                        .setPrice(ORDER_LINE_PRICE)
                        .setProductId(ORDER_LINE_PRODUCT1_ID)
                        .build();
        net.joelfernandes.ordermanagementsystem.avro.OrderLineInput orderLineInput2 =
                net.joelfernandes.ordermanagementsystem.avro.OrderLineInput.newBuilder()
                        .setQuantity(ORDER_LINE_QUANTITY)
                        .setPrice(ORDER_LINE_PRICE)
                        .setProductId(ORDER_LINE_PRODUCT2_ID)
                        .build();

        return net.joelfernandes.ordermanagementsystem.avro.OrderInput.newBuilder()
                .setOrderDate(ORDER_DATE_STRING)
                .setOrderId(ORDER_ID)
                .setCustomerName(ORDER_CUSTOMER_NAME)
                .setOrderLines(List.of(orderLineInput1, orderLineInput2))
                .build();
    }
}
