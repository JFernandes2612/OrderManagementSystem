package net.joelfernandes.OrderManagementSystem.application.order.in.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderLineInput {
    private String productId;
    private long quantity;
    private float price;
}
