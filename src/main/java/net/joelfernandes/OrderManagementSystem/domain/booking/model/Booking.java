package net.joelfernandes.OrderManagementSystem.domain.booking.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;

@Builder
@Getter
public class Booking {
    private String bookingId;
    private String customerCode;
    private String supplierCode;
    private String factoryCode;
    @Setter private Order order;
}
