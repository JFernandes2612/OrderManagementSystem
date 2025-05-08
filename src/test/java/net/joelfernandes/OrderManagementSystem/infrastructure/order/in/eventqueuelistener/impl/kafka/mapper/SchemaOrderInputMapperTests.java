package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener.impl.kafka.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.joelfernandes.OrderManagementSystem.application.order.in.model.OrderInput;
import net.joelfernandes.OrderManagementSystem.application.order.in.model.OrderLineInput;
import org.junit.jupiter.api.Test;

class SchemaOrderInputMapperTests {

    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER_ID = "orderId";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final String ORDER_DATE_STRING = "2024-09-30T20:57:46Z";
    private static final Date ORDER_DATE;

    static {
        try {
            ORDER_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(ORDER_DATE_STRING);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private final SchemaOrderInputMapper schemaOrderInputMapper = SchemaOrderInputMapper.INSTANCE;

    @Test
    public void shouldMapSchemaOrderInputToOrderInput() {
        // given
        net.joelfernandes.OrderManagementSystem.avro.OrderInput schemaOrderInput =
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

    private static net.joelfernandes.OrderManagementSystem.avro.OrderInput getSchemaOrderInput() {
        net.joelfernandes.OrderManagementSystem.avro.OrderLineInput orderLineInput1 =
                net.joelfernandes.OrderManagementSystem.avro
                        .OrderLineInput
                        .newBuilder()
                        .setQuantity(ORDER_LINE_QUANTITY)
                        .setPrice(ORDER_LINE_PRICE)
                        .setProductId(ORDER_LINE_PRODUCT1_ID)
                        .build();
        net.joelfernandes.OrderManagementSystem.avro.OrderLineInput orderLineInput2 =
                net.joelfernandes.OrderManagementSystem.avro
                        .OrderLineInput
                        .newBuilder()
                        .setQuantity(ORDER_LINE_QUANTITY)
                        .setPrice(ORDER_LINE_PRICE)
                        .setProductId(ORDER_LINE_PRODUCT2_ID)
                        .build();

        return net.joelfernandes.OrderManagementSystem.avro
                .OrderInput
                .newBuilder()
                .setOrderDate(ORDER_DATE_STRING)
                .setOrderId(ORDER_ID)
                .setCustomerName(ORDER_CUSTOMER_NAME)
                .setOrderLines(List.of(orderLineInput1, orderLineInput2))
                .build();
    }
}
