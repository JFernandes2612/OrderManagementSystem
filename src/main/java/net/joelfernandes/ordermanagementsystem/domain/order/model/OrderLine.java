package net.joelfernandes.ordermanagementsystem.domain.order.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderLine {
    private Long id;
    private String productId;
    private long quantity;
    private float price;
}
