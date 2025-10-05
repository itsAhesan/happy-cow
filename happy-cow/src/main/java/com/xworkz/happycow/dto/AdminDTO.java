package com.xworkz.happycow.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Base64;

@Component
@Data
@ToString(exclude = "profilePicture")
public class AdminDTO {

    private Integer adminId;

    private String adminName;

    private String emailId;

    private Long phoneNumber;

    private String password;

    private String confirmPassword;
    
    private MultipartFile profilePictureFile;
    
    private byte[] profilePicture;
    
    private String profilePictureContentType;

    public String getProfilePictureBase64() {
        if (profilePicture != null && profilePicture.length > 0) {
            return Base64.getEncoder().encodeToString(profilePicture);
        }
        return null;
    }


    private int failedAttempts = 0;


    private boolean accountLocked = false;


    private LocalDateTime lockedAt;

    // 🔑 Unlock token for email reset

    private String unlockToken;


    private LocalDateTime unlockTokenExpiry;

}
