package com.xworkz.happycow.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@Component
public class ProductDTO {


    private Integer productId;


    private String productName;


    private Double productPrice;


    private String createdBy;


    private LocalDateTime createdAt;


    private String updatedBy;


    private LocalDateTime updatedAt;


    private Boolean active = true;

    private String productType;
}
