package com.xworkz.happycow.entity;



import lombok.Data;
import lombok.ToString;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_audit")
@Data
public class AdminAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer auditId;

    // Relation to admin_info
    @OneToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "adminId")
    @ToString.Exclude
    private AdminEntity admin;

    @Column(name = "admin_name", nullable = false)
    private String adminName;

    @Column(name = "login_time")
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "action")
    private String action; // e.g. LOGIN, LOGOUT, SAVE
}
