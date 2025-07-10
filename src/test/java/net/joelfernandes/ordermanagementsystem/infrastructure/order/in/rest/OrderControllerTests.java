package net.joelfernandes.ordermanagementsystem.infrastructure.order.in.rest;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import net.joelfernandes.ordermanagementsystem.application.order.in.GetOrdersUseCase;
import net.joelfernandes.ordermanagementsystem.domain.order.model.Order;
import net.joelfernandes.ordermanagementsystem.domain.order.model.OrderLine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(OrderController.class)
class OrderControllerTests {
    private static final String ORDER_LINE_PRODUCT1_ID = "Product1";
    private static final String ORDER_LINE_PRODUCT2_ID = "Product2";
    private static final long ORDER_LINE_QUANTITY = 1;
    private static final float ORDER_LINE_PRICE = 2.0f;

    private static final String ORDER_ID = "orderId";
    private static final String ORDER_CUSTOMER_NAME = "customerName";
    private static final LocalDateTime ORDER_DATE = LocalDateTime.now();

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetOrdersUseCase getOrdersUseCase;

    @Test
    void shouldGetEmptyListWhenNoOrdersExist() throws Exception {
        // given
        when(getOrdersUseCase.getAllOrders()).thenReturn(Collections.emptyList());

        // when
        ResultActions resultActions = mockMvc.perform(get("/order"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
        verify(getOrdersUseCase, times(1)).getAllOrders();
    }

    @Test
    void shouldGetListWithOrdersWhenOrdersExist() throws Exception {
        // given
        Order order = getOrder();
        List<Order> ordersReturning = List.of(order);
        when(getOrdersUseCase.getAllOrders()).thenReturn(ordersReturning);

        // when
        ResultActions resultActions = mockMvc.perform(get("/order"));

        // then
        ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        String ordersJson = objectMapper.writeValueAsString(ordersReturning);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(ordersJson));
        verify(getOrdersUseCase, times(1)).getAllOrders();
    }

    private static Order getOrder() {
        OrderLine orderLine1 =
                OrderLine.builder()
                        .quantity(ORDER_LINE_QUANTITY)
                        .price(ORDER_LINE_PRICE)
                        .productId(ORDER_LINE_PRODUCT1_ID)
                        .build();
        OrderLine orderLine2 =
                OrderLine.builder()
                        .quantity(ORDER_LINE_QUANTITY)
                        .price(ORDER_LINE_PRICE)
                        .productId(ORDER_LINE_PRODUCT2_ID)
                        .build();

        return Order.builder()
                .orderDate(ORDER_DATE)
                .orderId(ORDER_ID)
                .customerName(ORDER_CUSTOMER_NAME)
                .orderLines(List.of(orderLine1, orderLine2))
                .build();
    }
}
