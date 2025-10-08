package com.xworkz.happycow.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "product_collection")
public class ProductCollectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_collection_id")
    private Integer productCollectionId;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id",nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AgentEntity agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id",nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AdminEntity admin;

    @Column(name = "type_of_milk")
    private String typeOfMilk;

    @Column(name = "price")
    private Double price;

    @Column(name = "quantity")
    private Float quantity;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "collected_at")
    private LocalDate collectedAt;



    @OneToOne(cascade = CascadeType.ALL,mappedBy = "productCollectionEntity")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ProductCollectionAuditEntity productCollectionAuditEntity;


}
