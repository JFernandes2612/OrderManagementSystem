package net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    private @Id String orderId;

    private String customerName;

    private Date orderDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ORDER_ID")
    private List<OrderLineEntity> orderLines;
}
