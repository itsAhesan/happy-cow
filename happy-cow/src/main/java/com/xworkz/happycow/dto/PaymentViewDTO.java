package com.xworkz.happycow.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PaymentViewDTO {

    private Long paymentId;
    private String referenceNo;
    private LocalDate windowStartDate;
    private LocalDate windowEndDate;
    private BigDecimal grossAmount;
    private LocalDateTime settledAt;
    private String status;
    // Optional agent info
    private Integer agentId;
    private String agentName;

    private List<OrderItemDTO> orders;

}
