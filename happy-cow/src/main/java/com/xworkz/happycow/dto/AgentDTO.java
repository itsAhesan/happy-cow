package com.xworkz.happycow.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

    private byte[] profilePicture;

    private String profilePictureContentType;

    private transient MultipartFile imageFile;


}
