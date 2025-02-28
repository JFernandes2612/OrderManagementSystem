package net.joelfernandes.OrderManagementSystem.domain.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.joelfernandes.OrderManagementSystem.domain.order.model.Order;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking {
    private String bookingId;
    private String customerCode;
    private String supplierCode;
    private String factoryCode;
    private Order order;
}
