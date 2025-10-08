package com.xworkz.happycow.dto;

import lombok.Data;

@Data
public class ProductCollectionAndAgentDTO {


    private Integer productCollectionId;
    // Agent
    private String agentName;
    private String agentEmail;
    private String agentPhone;
    private String agentAddress;
    // Collection
    private String typeOfMilk;
    private Double price;
    private Float quantity;
    private Double totalAmount;
}
