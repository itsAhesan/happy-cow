package com.xworkz.happycow.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PendingPaymentNotification {


    Integer agentId;
    String agentName;
    String email;
    String phoneNumber;
    java.time.LocalDateTime createdOn;
    long ageInDays;               // 13, 14, or 15
    String message;               // "Payment pending since X days"
    String link;                  // e.g., "/agent/{id}/product-collections?window=13-15"


}
