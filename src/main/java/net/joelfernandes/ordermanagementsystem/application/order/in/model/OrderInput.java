package net.joelfernandes.ordermanagementsystem.application.order.in.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderInput {
    private String orderId;
    private String customerName;
    private LocalDateTime orderDate;
    private List<OrderLineInput> orderLines;
}
