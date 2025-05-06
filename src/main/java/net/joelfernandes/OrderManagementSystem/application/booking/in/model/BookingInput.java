package net.joelfernandes.OrderManagementSystem.application.booking.in.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BookingInput {
    @NotNull private String bookingId;
    @NotNull private String customerCode;
    @NotNull private String supplierCode;
    @NotNull private String factoryCode;
    @NotNull private String orderId;
}
