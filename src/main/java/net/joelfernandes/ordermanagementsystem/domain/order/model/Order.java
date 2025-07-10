package net.joelfernandes.ordermanagementsystem.domain.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Order {
    private String orderId;
    private String customerName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEE MMM dd HH:mm:ss")
    @JsonProperty("orderDate")
    private LocalDateTime orderDate;

    private List<OrderLine> orderLines;
}
