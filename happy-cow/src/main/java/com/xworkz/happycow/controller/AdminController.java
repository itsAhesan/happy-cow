package com.xworkz.happycow.controller;


import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.dto.PendingPaymentNotification;
import com.xworkz.happycow.entity.AdminEntity;
import com.xworkz.happycow.repo.AdminRepo;
import com.xworkz.happycow.service.AdminService;
import com.xworkz.happycow.service.AuditService;
import com.xworkz.happycow.service.NotificationPushService;
import com.xworkz.happycow.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AuditService auditService;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private AdminService adminService;


    @Autowired
    private NotificationPushService notificationPushService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("adminLogin")
    public String adminLogin() {
        log.info("admin login is working");
        return "adminLoginForm";
    }



    @PostMapping("adminLoginProcess")
    public String adminLoginProcess(@RequestParam String email,
                                    @RequestParam String password,
                                    HttpSession session,
                                    Model model) {
        log.info("Admin login attempt for email: {}", email);
        long startTime = System.currentTimeMillis();

        AdminEntity adminEntity = adminService.findByEmailEntity(email);
        log.debug("findByEmailEntity completed in {}ms", System.currentTimeMillis() - startTime);

        if (adminEntity == null) {
            log.warn("Login failed - no account found for email: {}", email);
            model.addAttribute("errorMessage", "Invalid email or password");
            return "adminLoginForm";
        }

        if (adminEntity.isAccountLocked()) {
            log.warn("Login attempt on locked account: {}", email);
            return "accountLocked";
        }

        boolean passwordMatches = adminService.checkPassword(password, adminEntity.getPassword());

        if (!passwordMatches) {
            // wrong password flow
            adminService.increaseFailedAttempts(adminEntity);

            if (adminEntity.getFailedAttempts() >= 3) {
                adminService.lockAccount(adminEntity);
                log.warn("Account locked due to 3 failed attempts: {}", email);
                return "accountLocked";
            }

            log.warn("Invalid password attempt {} for email: {}", adminEntity.getFailedAttempts(), email);
            model.addAttribute("errorMessage",
                    "Invalid email or password, attempt left: " + (3 - adminEntity.getFailedAttempts()));
            return "adminLoginForm";
        }

        // ✅ success path
        adminService.resetFailedAttempts(adminEntity);

        // Build session-safe DTO
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setAdminId(adminEntity.getAdminId());
        adminDTO.setAdminName(adminEntity.getAdminName());
        adminDTO.setEmailId(adminEntity.getEmailId());
        adminDTO.setPhoneNumber(adminEntity.getPhoneNumber());
        adminDTO.setProfilePicture(adminEntity.getProfilePicture());
        adminDTO.setProfilePictureContentType(adminEntity.getProfilePictureContentType());

        // Audit login
        auditService.logAdminLogin(adminEntity);

        // Clear any stale bell data from an old session (if browser reused a JSESSIONID)
        session.removeAttribute("BELL_ITEMS");
        session.removeAttribute("BELL_COUNT");

        // Put logged-in admin in session
        session.setAttribute("loggedInAdmin", adminDTO);

        // 🔹 Preload notifications into session so adminDashboard can SSR them on first paint
        try {
            List<PendingPaymentNotification> notifs = notificationService.buildLoginNotifications();
            session.setAttribute("BELL_ITEMS", notifs);
            session.setAttribute("BELL_COUNT", notifs != null ? notifs.size() : 0);
            log.debug("Preloaded {} notifications into session for {}",
                    (notifs != null ? notifs.size() : 0), adminDTO.getEmailId());
        } catch (Exception e) {
            log.error("Failed to build notifications at login for {}", adminDTO.getEmailId(), e);
            // Keep UI resilient
            session.setAttribute("BELL_ITEMS", java.util.Collections.emptyList());
            session.setAttribute("BELL_COUNT", 0);
        }

        log.info("Login successful for email: {} in {}ms",
                email, System.currentTimeMillis() - startTime);

        // ❌ Do NOT push via WebSocket here — the client is not yet subscribed
        // notificationPushService.sendToUser(adminDTO.getEmailId(), notifs);

        // ✅ PRG: redirect so the browser loads adminDashboard, where JSP will SSR notifications
        return "redirect:/adminDashboard";
    }


  /*  @PostMapping("adminLoginProcess")
    public String adminLoginProcess(@RequestParam String email,
                                    @RequestParam String password,
                                    HttpSession session,
                                    Model model) {
        log.info("Admin login attempt for email: {}", email);
        long startTime = System.currentTimeMillis();

        AdminEntity adminEntity = adminService.findByEmailEntity(email);
        log.debug("findByEmailEntity completed in {}ms", System.currentTimeMillis() - startTime);


        if (adminEntity == null) {
            log.warn("Login failed - no account found for email: {}", email);
            model.addAttribute("errorMessage", "Invalid email or password");
            return "adminLoginForm";
        }

        // 🚨 If account is already locked
        if (adminEntity.isAccountLocked()) {
            log.warn("Login attempt on locked account: {}", email);
            return "accountLocked"; // redirect to locked page
        }

        // ✅ Check password (you can replace with BCrypt check if using hashed passwords)
        boolean passwordMatches = adminService.checkPassword(password, adminEntity.getPassword());

        if (passwordMatches) {
            adminService.resetFailedAttempts(adminEntity);

            AdminDTO adminDTO = new AdminDTO();
            adminDTO.setAdminId(adminEntity.getAdminId());
            adminDTO.setAdminName(adminEntity.getAdminName());
            adminDTO.setEmailId(adminEntity.getEmailId());
            adminDTO.setPhoneNumber(adminEntity.getPhoneNumber());

            adminDTO.setProfilePicture(adminEntity.getProfilePicture());
            adminDTO.setProfilePictureContentType(adminEntity.getProfilePictureContentType());
            // copy other fields if needed

            auditService.logAdminLogin(adminEntity);
          //  log.info("Login successful for email: {}", email);
            session.setAttribute("loggedInAdmin", adminDTO);
            model.addAttribute("loggedInAdmin", adminDTO);

            log.info("Login successful for email: {} in {}ms",
                    email, System.currentTimeMillis() - startTime);

            // 1) Build notifications
            List<PendingPaymentNotification> notifs = notificationService.buildLoginNotifications();

            // 2) Store in session (helps AJAX fallback and JSP SSR if needed)
            session.setAttribute("BELL_ITEMS", notifs);
            session.setAttribute("BELL_COUNT", notifs.size());

            // 3) Push to this admin’s user queue (so the dropdown populates in realtime)
          //  notificationPushService.sendToUser(adminDTO.getEmailId(), notifs);

            return "redirect:/adminDashboard";
        } else {
            // ❌ Wrong password → increment failed attempts
            adminService.increaseFailedAttempts(adminEntity);

            if (adminEntity.getFailedAttempts() >= 3) {
                adminService.lockAccount(adminEntity);
                log.warn("Account locked due to 3 failed attempts: {}", email);
                return "accountLocked"; // locked page
            }

            log.warn("Invalid password attempt {} for email: {}", adminEntity.getFailedAttempts(), email);
            model.addAttribute("errorMessage", "Invalid email or password, attempt left: "+(3-adminEntity.getFailedAttempts()));
            return "adminLoginForm";
        }
    }*/

 /*   @GetMapping("adminDashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        // Check if admin is logged in
        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");

        if (loggedInAdmin == null) {
            // Not logged in → redirect to login form
            return "redirect:/adminLoginForm";
        }

        // Add admin info to model so JSP can use it
        model.addAttribute("loggedInAdmin", loggedInAdmin);

        return "adminDashboard"; // maps to adminDashboard.jsp
    }*/

    // AdminController
  //  @Autowired private NotificationService notificationService;

    @GetMapping("adminDashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/adminLogin";
        }

        // Preload notifications for first paint (SSR)
        @SuppressWarnings("unchecked")
        List<PendingPaymentNotification> items =
                (List<PendingPaymentNotification>) session.getAttribute("BELL_ITEMS");

        if (items == null) {
            items = notificationService.buildLoginNotifications();
            session.setAttribute("BELL_ITEMS", items);
            session.setAttribute("BELL_COUNT", items.size());
        }

        model.addAttribute("loggedInAdmin", loggedInAdmin);
        return "adminDashboard";
    }



    @GetMapping("adminProfile")
    public String adminProfile(HttpSession session, Model model) {
        // Get logged-in admin from session
        AdminDTO admin = (AdminDTO) session.getAttribute("loggedInAdmin");

        if (admin == null) {
            // Not logged in → go back to login

           return "redirect:/adminLogin";

        }

        model.addAttribute("admin", admin);
        return "adminProfile"; // ✅ loads adminProfile.jsp
    }

    @PostMapping("updateProfile")
    public String updateProfile(@ModelAttribute AdminDTO adminDTO,
                                HttpSession session,
                                Model model) {
        // Get the logged-in admin from session
        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");

        if (loggedInAdmin == null) {
            return "redirect:/adminLogin";
        }


        adminDTO.setAdminId(loggedInAdmin.getAdminId());
        adminDTO.setEmailId(loggedInAdmin.getEmailId());

        System.out.println("AdminDTO from updateProfile controller: " + adminDTO);
        boolean updated = adminService.updateProfile(adminDTO);

        if (updated) {

            session.setAttribute("loggedInAdmin", adminDTO);
            model.addAttribute("admin", adminDTO);
            model.addAttribute("successMessage", "Profile updated successfully!");
        } else {
            model.addAttribute("errorMessage", "Failed to update profile. Try again!");
        }

        return "adminProfile";
    }

    @PostMapping("/sendUnlockLink")
    public String sendUnlockLink(@RequestParam String email, Model model) {
        log.info("Unlock request for email: {}", email);

        AdminEntity admin = adminService.findByEmailEntity(email);

        if (admin == null) {
            log.warn("Unlock request failed - no account found for email: {}", email);
            model.addAttribute("message", "No account found with this email.");
            return "accountLocked";
        }

        if (!admin.isAccountLocked()) {
            model.addAttribute("message", "This account is not locked. You can try logging in.");
            return "accountLocked";
        }

        // ✅ Generate token and send email
        adminService.generateUnlockToken(admin);
        adminService.sendUnlockEmail(admin);

        model.addAttribute("message", "An unlock link has been sent to your email.");
        return "accountLocked";
    }

    @GetMapping("/admin/unlock")
    public String unlockAccount(@RequestParam String token, Model model) {
        AdminEntity admin = adminService.findByUnlockToken(token);

        if (admin == null || admin.getUnlockTokenExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Invalid or expired unlock link.");
            return "accountLocked";
        }

        // ✅ Unlock and redirect to reset password page
        admin.setAccountLocked(false);
        admin.setFailedAttempts(0);
        admin.setUnlockToken(null);
        admin.setUnlockTokenExpiry(null);
        adminRepo.updateAdmin(admin);

        model.addAttribute("email", admin.getEmailId());
        return "resetPassword"; // JSP where admin sets a new password
    }


    @PostMapping("/admin/resetPassword")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String password,
                                @RequestParam String confirmPassword,
                                Model model) {

        AdminEntity admin = adminService.findByEmailEntity(email);

        if (admin == null) {
            model.addAttribute("message", "Invalid request. User not found.");
            return "resetPassword";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match.");
            model.addAttribute("email", email);
            return "resetPassword";
        }

        // ✅ Save new password (use BCrypt for security)
        String hashedPassword = new BCryptPasswordEncoder().encode(password);
        admin.setPassword(hashedPassword);
        admin.setConfirmPassword(confirmPassword);

        admin.setAccountLocked(false);
        admin.setFailedAttempts(0);
        admin.setUnlockToken(null);
        admin.setUnlockTokenExpiry(null);

        adminRepo.updateAdmin(admin);

        model.addAttribute("message", "Password reset successfully. Please log in with your new password.");
        return "resetSuccess";
    }


    @GetMapping("logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");

        if (loggedInAdmin != null) {
            log.info("Admin '{}' (ID: {}) logged out", loggedInAdmin.getEmailId(), loggedInAdmin.getAdminId());
        } else {
            log.info("Logout request with no active session");
        }

        AdminEntity adminEntity = adminService.findByEmailEntity(loggedInAdmin.getEmailId());


        auditService.logAdminLogout(adminEntity);
        // Clear the session
        session.invalidate();


        // Add flash attribute so message survives redirect


        redirectAttributes.addFlashAttribute("logoutMessage", "You have been logged out successfully.");

        return "redirect:/adminLogin";
    }

    @GetMapping("forgetPassword")
    public String forgetPassword() {

        log.info("forgetPassword is working");
        return "forgetPassword";
    }


    @PostMapping("sendOtp")
    public String sendOtp(@RequestParam("email") String email, Model model) {
        log.info("Sending OTP to email: {}", email);

        boolean sent = adminService.sendOtp(email);
        if (sent) {
            model.addAttribute("email", email);
            model.addAttribute("message", "OTP has been sent to your registered email.");
            return "verifyOtp"; // go to verifyOtp.jsp
        } else {
            model.addAttribute("message", "Email not registered. Please try again.");
            return "forgetPassword";
        }
    }

    @PostMapping("verifyOtp")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            @RequestParam("newPassword") String newPassword,
                            @RequestParam("confirmPassword") String confirmPassword,
                            Model model) {
        log.info("Verifying OTP for email: {}", email);

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match!");
            model.addAttribute("email", email);
            return "verifyOtp";
        }

        boolean verified = adminService.verifyOtpAndResetPassword(email, otp, newPassword);
        if (verified) {
            model.addAttribute("errorMessage", "Password reset successful. Please login.");
            return "adminLoginForm";
        } else {
            model.addAttribute("message", "Invalid or expired OTP. Please try again.");
            model.addAttribute("email", email);
            return "verifyOtp";
        }
    }

}
