package com.xworkz.happycow.restcontroller;


import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.service.AdminService;
import com.xworkz.happycow.service.AuditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
@Api(value = "Admin Management", tags = "Admin API")
@Slf4j
public class HappyCowRestController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private AuditService auditService;

    @PostMapping(value = "/adminRegister", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Register a new Admin", notes = "Provide admin details in JSON format")
    public String adminRegister(@RequestBody AdminDTO adminDTO) {
        log.info("admin register is working");
        System.out.println(adminDTO);

        Boolean saved = adminService.adminRegister(adminDTO);


        //   auditService.logAdminAuditSave(adminDTO);


        if (saved) {
            return "Saved Successfully";
        } else {
            return "Failed to Save";
        }
    }

    @PostMapping("/uploadProfilePicture")
    @ApiOperation(value = "Upload profile picture", notes = "Upload a profile picture for the logged-in admin")
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file) {
        try {
            // Get the logged-in admin's email from session
            AdminDTO loggedInAdmin = (AdminDTO) httpSession.getAttribute("admin");
            if (loggedInAdmin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }

            // Convert MultipartFile to byte array
            byte[] imageBytes = file.getBytes();

            // Create AdminDTO with the updated profile picture
            AdminDTO adminToUpdate = new AdminDTO();
            adminToUpdate.setEmailId(loggedInAdmin.getEmailId());
            adminToUpdate.setProfilePicture(imageBytes);

            // Update profile picture
            boolean updated = adminService.updateProfilePicture(adminToUpdate);

            if (updated) {
                // Update the admin in session with new profile picture
                AdminDTO updatedAdmin = adminService.adminLogin(
                        loggedInAdmin.getEmailId(),
                        loggedInAdmin.getPassword()
                );
                httpSession.setAttribute("admin", updatedAdmin);

                return ResponseEntity.ok("Profile picture updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to update profile picture");
            }

        } catch (IOException e) {
            log.error("Error processing profile picture upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing profile picture: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during profile picture upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }
}
