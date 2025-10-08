package com.xworkz.happycow.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "product_collection_audit")
public class ProductCollectionAuditEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collect_milk_audit_id")
    private Integer collectMilkAuditId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @OneToOne
    @JoinColumn(name = "product_collection_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductCollectionEntity productCollectionEntity;
}
