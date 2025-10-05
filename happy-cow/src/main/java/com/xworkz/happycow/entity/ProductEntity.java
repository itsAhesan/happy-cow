package com.xworkz.happycow.entity;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
@NamedQuery(name = "findAllProducts", query = "SELECT p FROM ProductEntity p WHERE p.active = true")
@NamedQuery(name = "findByProductName", query = "SELECT p FROM ProductEntity p WHERE p.productName = :productName AND p.active = true")
//@NamedQuery(name = "searchProducts", query = "SELECT p FROM ProductEntity p WHERE p.productName LIKE :productName AND p.active = true")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_price")
    private Double productPrice;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "product_type")
    private String productType;






}
