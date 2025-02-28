package net.joelfernandes.OrderManagementSystem.application.order.in.model;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderInput {
    private String orderId;
    private String customerName;
    private Date orderDate;
    private List<OrderLineInput> orderLines;
}
