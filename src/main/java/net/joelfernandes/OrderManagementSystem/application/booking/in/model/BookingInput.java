package net.joelfernandes.OrderManagementSystem.application.booking.in.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingInput {
    private String bookingId;
    private String customerCode;
    private String supplierCode;
    private String factoryCode;
    private String orderId;
}
