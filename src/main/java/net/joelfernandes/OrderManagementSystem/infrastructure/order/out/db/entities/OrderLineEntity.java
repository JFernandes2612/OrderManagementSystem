package net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class OrderLineEntity {
    @GeneratedValue(strategy = GenerationType.AUTO)
    private @Id long id;

    private String productId;

    private long quantity;

    private float price;
}
