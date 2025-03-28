package net.joelfernandes.OrderManagementSystem.application.booking.in.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BookingInput {
    private String bookingId;
    private String customerCode;
    private String supplierCode;
    private String factoryCode;
    private String orderId;
}
