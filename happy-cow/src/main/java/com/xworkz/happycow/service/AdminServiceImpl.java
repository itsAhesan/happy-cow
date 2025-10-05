package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.entity.AdminEntity;
import com.xworkz.happycow.repo.AdminRepo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminDTO adminLogin(String email, String password) {

        if (email == null || password == null) {
            return null;
        }
        AdminEntity adminEntity = adminRepo.findByEmail(email);

        if (adminEntity == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, adminEntity.getPassword())) {
            return null;
        }

        AdminDTO adminDTO = new AdminDTO();
        BeanUtils.copyProperties(adminEntity, adminDTO);

        return adminDTO;
    }

    @Override
    public boolean updateProfile(AdminDTO adminDTO) {
        try {
            AdminEntity adminEntity = adminRepo.findById(adminDTO.getAdminId());
            if (adminEntity == null) {
                return false;
            }


            BeanUtils.copyProperties(adminDTO, adminEntity, "confirmPassword", "profilePictureFile", "profilePictureContentType", "profilePicture");

            // Update fields
            adminEntity.setConfirmPassword(adminDTO.getPassword());


            if (adminDTO.getPassword() != null && !adminDTO.getPassword().isEmpty()) {
                adminEntity.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
                //  adminEntity.setPassword(adminDTO.getPassword()); // Better: hash with BCrypt
            }

            return adminRepo.updateAdmin(adminEntity);
        } catch (Exception e) {
            log.error("Error updating admin profile", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateProfilePicture(AdminDTO adminDTO) {
        try {
            if (adminDTO == null || adminDTO.getEmailId() == null || adminDTO.getProfilePictureFile() == null) {
                log.error("Invalid parameters for profile picture update");
                return false;
            }

            try {
                // Get the file data
                byte[] profilePicture = adminDTO.getProfilePictureFile().getBytes();
                String contentType = adminDTO.getProfilePictureFile().getContentType();

                // Update the admin entity
                AdminEntity adminEntity = adminRepo.findByEmail(adminDTO.getEmailId());
                if (adminEntity == null) {
                    log.error("Admin not found with email: {}", adminDTO.getEmailId());
                    return false;
                }

                adminEntity.setProfilePicture(profilePicture);
                adminEntity.setProfilePictureContentType(contentType);

                return adminRepo.updateAdmin(adminEntity);

            } catch (IOException e) {
                log.error("Error reading file data: {}", e.getMessage(), e);
                return false;
            }

        } catch (Exception e) {
            log.error("Error in updateProfilePicture: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Boolean adminRegister(AdminDTO adminDTO) {

        AdminEntity adminEntity = new AdminEntity();

        BeanUtils.copyProperties(adminDTO, adminEntity);

        String encodedPassword = passwordEncoder.encode(adminDTO.getPassword());
        adminEntity.setPassword(encodedPassword);

        Boolean saved = adminRepo.save(adminEntity);

        if (saved) {
            System.out.println("Admin saved successfully");
            return true;
        } else {
            System.out.println("Admin not saved");
            return false;
        }
    }

    @Override
    public AdminDTO findByEmail(String email) {
        if (email == null) {
            return null;
        }
        AdminEntity adminEntity = adminRepo.findByEmail(email);
        if (adminEntity == null) {
            return null;
        }
        AdminDTO adminDTO = new AdminDTO();
        BeanUtils.copyProperties(adminEntity, adminDTO);
        return adminDTO;
    }

    @Override
    public AdminEntity findByEmailEntity(String email) {
        return adminRepo.findByEmail(email);
    }

    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        // If using BCrypt:
        // return new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);

        return passwordEncoder.matches(rawPassword, encodedPassword); // plain-text (not recommended)
    }

    @Override
    public void resetFailedAttempts(AdminEntity admin) {
        admin.setFailedAttempts(0);
        adminRepo.updateAdmin(admin);
    }

    @Override
    public void increaseFailedAttempts(AdminEntity admin) {
        admin.setFailedAttempts(admin.getFailedAttempts() + 1);
        adminRepo.updateAdmin(admin);
    }

    @Override
    public void lockAccount(AdminEntity admin) {
        admin.setAccountLocked(true);
        admin.setLockedAt(LocalDateTime.now());
        adminRepo.updateAdmin(admin);
    }

    @Autowired
    private JavaMailSender mailSender;

  /*  @Value("${app.base-url}") // e.g. http://localhost:8080/happy-cow
    private String baseUrl;*/

    @Override
    public void generateUnlockToken(AdminEntity admin) {
        String token = UUID.randomUUID().toString();
        admin.setUnlockToken(token);
        admin.setUnlockTokenExpiry(LocalDateTime.now().plusMinutes(15)); // 15 min validity
        adminRepo.updateAdmin(admin);
    }

    @Override
    public void sendUnlockEmail(AdminEntity admin) {
        //  String unlockLink = baseUrl + "/admin/unlock?token=" + admin.getUnlockToken();
        String unlockUrl = "http://localhost:8080/happy-cow/admin/unlock?token=" + admin.getUnlockToken();
        String subject = "Unlock Your HappyCow Admin Account";
        String body = "Hi " + admin.getAdminName() + ",\n\n"
                + "Your account has been locked due to 3 failed login attempts.\n"
                + "Click the link below to unlock and reset your password:\n\n"
                + unlockUrl + "\n\n"
                + "This link will expire in 15 minutes.\n\n"
                + "Regards,\nHappyCow Dairy Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(admin.getEmailId());
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        log.info("Unlock email sent to {}", admin.getEmailId());
    }

    @Override
    public AdminEntity findByUnlockToken(String token) {

        AdminEntity admin = adminRepo.findByUnlockToken(token);

        if (admin == null || admin.getUnlockTokenExpiry().isBefore(LocalDateTime.now())) {
            return null;
        }


        return admin;

    }

    @Override
    public boolean sendOtp(String email) {
        AdminEntity admin = adminRepo.findByEmail(email);
        if (admin != null) {
            //  AdminEntity admin = optional.get();

            // Generate 6-digit OTP
            String otp = String.format("%06d", new Random().nextInt(999999));

            admin.setOtp(otp);
            admin.setOtpExpiry(LocalDateTime.now().plusMinutes(5)); // valid for 5 mins
            adminRepo.updateAdmin(admin);

            // Send OTP via email
            String subject = "HappyCow Dairy - Password Reset OTP";
            String body = "Hi " + admin.getAdminName() + ",\n\nYour OTP for password reset is: "
                    + otp + "\n\nIt is valid for 5 minutes.";


            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(admin.getEmailId());
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("OTP sent successfully to {}", email);
            return true;
        }
        return false;
    }

    @Override
    public boolean verifyOtpAndResetPassword(String email, String otp, String newPassword) {
        AdminEntity admin = adminRepo.findByEmail(email);
        if (admin != null) {

            if (admin.getOtp() != null &&
                    admin.getOtp().equals(otp) &&
                    admin.getOtpExpiry().isAfter(LocalDateTime.now())) {

                // Reset password
                admin.setPassword(passwordEncoder.encode(newPassword));
                admin.setConfirmPassword(newPassword);
                admin.setOtp(null); // clear OTP
                admin.setOtpExpiry(null);
                adminRepo.updateAdmin(admin);

                log.info("Password reset successful for {}", email);
                return true;
            }
        }
        return false;
    }


}
