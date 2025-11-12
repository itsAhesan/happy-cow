package com.xworkz.happycow.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(name = "findAllAgents", query = "SELECT a FROM AgentEntity a WHERE a.active = true")
@NamedQuery(name = "findByAgentEmail", query = "SELECT a FROM AgentEntity a WHERE a.email = :email")
@NamedQuery(name = "findByAgentPhoneNumber", query = "SELECT a FROM AgentEntity a WHERE a.phoneNumber = :phoneNumber")
public class AgentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_id")
    private Integer agentId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "phone_number", unique = true, nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "types_of_milk", length = 100)
    private String typesOfMilk;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;



    @Lob
    @Basic(fetch = FetchType.LAZY) // don't fetch the blob unless needed
    @Column(name = "profile_picture", columnDefinition = "LONGBLOB")
    @ToString.Exclude // avoid dumping bytes in logs
    private byte[] profilePicture;

    @Column(name = "profile_picture_content_type", length = 100)
    private String profilePictureContentType;





    @Column(name = "profile_token", length = 64, unique = true)
    private String profileToken;

    @Column(name = "qr_code", columnDefinition = "LONGBLOB")
    private byte[] qrCode;



}
