package net.joelfernandes.OrderManagementSystem.domain.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDTO {
    private String bookingId;
    private String customerCode;
    private String supplierCode;
    private String factoryCode;
    private String orderId;
}
