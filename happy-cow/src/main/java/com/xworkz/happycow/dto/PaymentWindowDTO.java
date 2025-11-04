package com.xworkz.happycow.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
public class PaymentWindowDTO {

    private Long paymentId;
    private Integer agentId;
    private String agentName;
    private String agentEmail;
    private String agentPhone;
    private LocalDate windowStartDate;
    private LocalDate windowEndDate;
    private BigDecimal grossAmount;
    private String status;
    private LocalDateTime settledAt;
    private String referenceNo;
    private LocalDateTime createdOn;

    public PaymentWindowDTO(Long paymentId, Integer agentId, String agentName, String agentEmail, String agentPhone,
                            LocalDate windowStartDate, LocalDate windowEndDate,
                            BigDecimal grossAmount, String status, LocalDateTime settledAt, String referenceNo,
                            LocalDateTime createdOn) {
        this.paymentId = paymentId;
        this.agentId = agentId;
        this.agentName = agentName;
        this.agentEmail = agentEmail;
        this.agentPhone = agentPhone;
        this.windowStartDate = windowStartDate;
        this.windowEndDate = windowEndDate;
        this.grossAmount = grossAmount;
        this.status = status;
        this.settledAt = settledAt;
        this.referenceNo = referenceNo;
        this.createdOn = createdOn;
    }


}
