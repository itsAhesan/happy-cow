package com.xworkz.happycow.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "agent_payment_window",
        uniqueConstraints = @UniqueConstraint(name = "uq_agent_window",
                columnNames = {"agent_id","window_start_date","window_end_date"})
)
@Getter
@Setter
//@ToString
@ToString(exclude = {"agent", "settledByAdmin"})

public class AgentPaymentWindowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentEntity agent;

    @Column(name = "window_start_date", nullable = false)
    private LocalDate windowStartDate;

    @Column(name = "window_end_date", nullable = false)
    private LocalDate windowEndDate;

    @Column(name = "gross_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "settled_at", nullable = false)
    private LocalDateTime settledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settled_by_admin_id")
    private AdminEntity settledByAdmin;

    @Column(name = "reference_no", nullable = false, length = 64)
    private String referenceNo;

    @Column(name = "status", nullable = false, length = 32)
    private String status; // SUCCESS, etc.

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;


}