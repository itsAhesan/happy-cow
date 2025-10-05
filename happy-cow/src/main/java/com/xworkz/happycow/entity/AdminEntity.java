package com.xworkz.happycow.entity;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString(exclude = "profilePicture")
@ToString
@NamedQuery(name = "findByEmail", query = "SELECT a FROM AdminEntity a WHERE a.emailId = :email")
@NamedQuery(name = "findByUnlockToken", query = "SELECT a FROM AdminEntity a WHERE a.unlockToken = :token")
public class AdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adminId;

    @Column(name = "admin_name")
    private String adminName;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "phone_number")
    private Long phoneNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "confirm_password")
    private String confirmPassword;
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profile_picture", columnDefinition = "LONGBLOB")
    @ToString.Exclude
    private byte[] profilePicture;

    @Column(name = "profile_picture_content_type")
    private String profilePictureContentType;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts = 0;

    @Column(name = "account_locked", nullable = false)
    private boolean accountLocked = false;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Column(name = "unlock_token")
    private String unlockToken;

    @Column(name = "unlock_token_expiry")
    private LocalDateTime unlockTokenExpiry;

    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;

    @OneToOne(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @ToString.Exclude
    private AdminAuditEntity audit;


}
