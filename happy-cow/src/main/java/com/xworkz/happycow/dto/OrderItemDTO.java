package com.xworkz.happycow.dto;



import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderItemDTO {
    private Integer productCollectionId;
    private String productName;   // maps to ProductCollectionEntity.typeOfMilk
    private Double unitPrice;     // maps to price
    private Float quantity;       // maps to quantity
    private Double lineTotal;     // maps to totalAmount
    private LocalDate collectedAt; // maps to collectedAt
}

