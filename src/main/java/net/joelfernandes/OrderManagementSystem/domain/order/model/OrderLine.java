package net.joelfernandes.OrderManagementSystem.domain.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderLine {
    private Long id;
    private String productId;
    private long quantity;
    private float price;
}
