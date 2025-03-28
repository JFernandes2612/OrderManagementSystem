package net.joelfernandes.OrderManagementSystem.domain.order.model;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Order {
    private String orderId;
    private String customerName;
    private Date orderDate;
    private List<OrderLine> orderLines;
}
