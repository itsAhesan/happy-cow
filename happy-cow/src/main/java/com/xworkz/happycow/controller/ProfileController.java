package com.xworkz.happycow.controller;

import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/upload-picture")
    public ResponseEntity<Map<String, Object>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        log.info("Received file upload request. File name: {}, Size: {} bytes",
                file.getOriginalFilename(), file.getSize());

        try {
            // Get the logged-in admin from session
            AdminDTO adminDTO = (AdminDTO) session.getAttribute("loggedInAdmin");
            if (adminDTO == null) {
                log.warn("No admin found in session");
                response.put("success", false);
                response.put("message", "Session expired. Please log in again.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Validate file
            if (file.isEmpty()) {
                log.warn("Empty file received");
                response.put("success", false);
                response.put("message", "Please select a file to upload");
                return ResponseEntity.badRequest().body(response);
            }

            // Check file size (max 2MB)
            if (file.getSize() > 2 * 1024 * 1024) {
                log.warn("File size {} exceeds limit", file.getSize());
                response.put("success", false);
                response.put("message", "File size exceeds the 2MB limit");
                return ResponseEntity.badRequest().body(response);
            }

            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("Invalid file type: {}", contentType);
                response.put("success", false);
                response.put("message", "Only image files are allowed");
                return ResponseEntity.badRequest().body(response);
            }

            log.info("Updating profile picture for admin: {}", adminDTO.getEmailId());

            // ✅ set both bytes and content type
            adminDTO.setProfilePicture(file.getBytes());
            adminDTO.setProfilePictureContentType(file.getContentType());
            adminDTO.setProfilePictureFile(file); // optional, if you need raw file object

            boolean updated = adminService.updateProfilePicture(adminDTO);


            if (!updated) {
                throw new RuntimeException("Failed to update profile picture in database");
            }

            // Update session with new admin data
            AdminDTO updatedAdmin = adminService.findByEmail(adminDTO.getEmailId());
            if (updatedAdmin == null) {
                throw new RuntimeException("Failed to retrieve updated admin data");
            }

            session.setAttribute("loggedInAdmin", updatedAdmin);

            // Prepare response
            response.put("success", true);
            response.put("message", "Profile picture updated successfully");
            response.put("contentType", updatedAdmin.getProfilePictureContentType());

            // Convert byte[] to Base64 for the response
            if (updatedAdmin.getProfilePicture() != null) {
                String base64Image = Base64.getEncoder().encodeToString(updatedAdmin.getProfilePicture());
                response.put("profilePicture", base64Image);
                log.info("Profile picture updated successfully for admin: {}", adminDTO.getEmailId());
            } else {
                log.warn("Profile picture is null after update for admin: {}", adminDTO.getEmailId());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error uploading profile picture: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while uploading the file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/update")
    public String updateProfile(
            @ModelAttribute("admin") AdminDTO adminDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Get the logged-in admin from session
            AdminDTO loggedInAdmin = (AdminDTO) session.getAttribute("loggedInAdmin");
            if (loggedInAdmin == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please login to update profile");
                return "redirect:/login";
            }

            // Set the admin ID from session to ensure we're updating the correct admin
            adminDTO.setAdminId(loggedInAdmin.getAdminId());

            // Update the profile
            boolean updated = adminService.updateProfile(adminDTO);

            if (updated) {
                // Update the admin in session
                AdminDTO updatedAdmin = adminService.adminLogin(
                        adminDTO.getEmailId() != null ? adminDTO.getEmailId() : loggedInAdmin.getEmailId(),
                        adminDTO.getPassword() != null ? adminDTO.getPassword() : loggedInAdmin.getPassword()
                );

                if (updatedAdmin != null) {
                    session.setAttribute("loggedInAdmin", updatedAdmin);
                    redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
                } else {
                    // If login fails after update, it might be due to password change
                    // In a real app, you might want to handle this differently
                    redirectAttributes.addFlashAttribute("successMessage", "Profile updated. Please login again with your new password.");
                    return "redirect:/logout";
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile");
            }

        } catch (Exception e) {
            log.error("Error updating profile", e);
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while updating profile");
        }

        return "redirect:/adminProfile";
    }
}
