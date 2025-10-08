package com.xworkz.happycow.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductCollectionDTO {

    private Integer productCollectionId;
    private AgentDTO agent;
    private AdminDTO admin;


    private String phoneNumber;


    private String typeOfMilk;


    private Double price;


    private Float quantity;


    private Double totalAmount;


    private LocalDate collectedAt;
}
