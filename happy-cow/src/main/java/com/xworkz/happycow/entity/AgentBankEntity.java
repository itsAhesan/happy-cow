package com.xworkz.happycow.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@Table(name = "agent_bank_details", uniqueConstraints = {
        @UniqueConstraint(name = "uk_bank_agent", columnNames = "agent_id")
})
public class AgentBankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "agent_id", nullable = false, unique = true)
    private Integer agentId;

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(name = "branch_name", nullable = false, length = 100)
    private String branchName;

    @Column(name = "holder_name", nullable = false, length = 100)
    private String accountHolderName;

    @Column(name = "account_no", nullable = false, length = 32)
    private String accountNumber;

    @Column(name = "ifsc", nullable = false, length = 11)
    private String ifsc;

    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType; // SAVINGS/CURRENT/SALARY

   /* @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "created_by")
    private String createdBy;*/

    @OneToOne(mappedBy = "agentBankEntity",fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.ALL)
    private AgentBankAuditEntity agentBankAuditEntity;


}
