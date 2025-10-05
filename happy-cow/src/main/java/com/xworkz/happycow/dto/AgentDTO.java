package com.xworkz.happycow.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.Column;

@Data
@Component
public class AgentDTO {

    private Integer agentId;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String address;

    private String typesOfMilk;


}
