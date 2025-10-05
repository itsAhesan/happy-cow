package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.entity.AdminEntity;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {
    Boolean adminRegister(AdminDTO adminDTO);

    AdminDTO adminLogin(String email, String password);

    boolean updateProfile(AdminDTO adminDTO);
    
    boolean updateProfilePicture(AdminDTO adminDTO);
    
    AdminDTO findByEmail(String email);

    AdminEntity findByEmailEntity(String email);
    boolean checkPassword(String rawPassword, String encodedPassword);
    void resetFailedAttempts(AdminEntity admin);
    void increaseFailedAttempts(AdminEntity admin);
    void lockAccount(AdminEntity admin);

    void generateUnlockToken(AdminEntity admin);
    void sendUnlockEmail(AdminEntity admin);


    AdminEntity findByUnlockToken(String token);

    boolean sendOtp(String email);

    boolean verifyOtpAndResetPassword(String email, String otp, String newPassword);
}
