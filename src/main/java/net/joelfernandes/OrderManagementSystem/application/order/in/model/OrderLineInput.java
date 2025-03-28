package net.joelfernandes.OrderManagementSystem.application.order.in.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderLineInput {
    private String productId;
    private long quantity;
    private float price;
}
