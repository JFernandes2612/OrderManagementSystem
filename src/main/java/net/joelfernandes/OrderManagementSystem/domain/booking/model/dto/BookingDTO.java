package net.joelfernandes.OrderManagementSystem.domain.booking.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BookingDTO {
    private String bookingId;
    private String customerCode;
    private String supplierCode;
    private String factoryCode;
    private String orderId;
}
