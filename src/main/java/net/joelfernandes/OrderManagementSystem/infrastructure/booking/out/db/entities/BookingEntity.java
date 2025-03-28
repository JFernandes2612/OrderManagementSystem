package net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.joelfernandes.OrderManagementSystem.infrastructure.order.out.db.entities.OrderEntity;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {
    @Id private String bookingId;

    private String customerCode;

    private String supplierCode;

    private String factoryCode;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ORDER_ID")
    private OrderEntity order;
}
