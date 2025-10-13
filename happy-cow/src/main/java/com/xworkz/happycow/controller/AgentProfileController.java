package com.xworkz.happycow.controller;


import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.dto.BankForm;
import com.xworkz.happycow.dto.PhotoDTO;
import com.xworkz.happycow.entity.AgentBankEntity;
import com.xworkz.happycow.entity.AgentEntity;
import com.xworkz.happycow.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/agent/profile")
public class AgentProfileController {

    @Autowired
    private AgentService agentService;


    @GetMapping("/edit")
    public String showEditProfile(Model model, HttpSession session) {
        AgentDTO loggedInAgent = (AgentDTO) session.getAttribute("loggedInAgent");
        if (loggedInAgent == null) {
            return "redirect:/agentLogin";
        }

        // (Optional) refresh from DB to avoid stale session data:
        try {
            AgentEntity fresh = agentService.findByEmailEntity(loggedInAgent.getEmail());
            AgentDTO dto = new AgentDTO();
            dto.setAgentId(fresh.getAgentId());
            dto.setFirstName(fresh.getFirstName());
            dto.setLastName(fresh.getLastName());
            dto.setEmail(fresh.getEmail());
            dto.setPhoneNumber(fresh.getPhoneNumber());
            dto.setAddress(fresh.getAddress());
            dto.setTypesOfMilk(fresh.getTypesOfMilk());
            model.addAttribute("agent", dto);
        } catch (Exception e) {
            // fall back to session copy if something goes wrong
            model.addAttribute("agent", loggedInAgent);
        }

        return "agentEditProfile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("agent") AgentDTO dto,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                RedirectAttributes ra,
                                HttpSession session,
                                Model model) {

        AgentDTO loggedInAgent = (AgentDTO) session.getAttribute("loggedInAgent");
        if (loggedInAgent == null) {
            return "redirect:/agentLogin";
        }

        try {
            // Update in DB
            agentService.updateFromDto(loggedInAgent.getEmail(), dto, imageFile);

            // Refresh session DTO (so navbar/profile shows latest names)
            AgentEntity fresh = agentService.findByEmailEntity(loggedInAgent.getEmail());
            loggedInAgent.setFirstName(fresh.getFirstName());
            loggedInAgent.setLastName(fresh.getLastName());
            loggedInAgent.setAddress(fresh.getAddress());
            loggedInAgent.setTypesOfMilk(fresh.getTypesOfMilk());
            session.setAttribute("loggedInAgent", loggedInAgent);

            ra.addFlashAttribute("success", "Profile updated successfully!");
            return "redirect:/agent/profile";
        } catch (IllegalArgumentException | SecurityException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("agent", dto);
            return "agentEditProfile";
        }
    }

    @GetMapping("/photo/{id}")
    public void getPhoto(@PathVariable Integer id, HttpServletResponse response) throws java.io.IOException {
        PhotoDTO dto = agentService.findPhotoById(id); // may be null
        if (dto == null || dto.getBytes() == null || dto.getBytes().length == 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String ct = (dto.getContentType() == null )
                ? "image/jpeg" : dto.getContentType();

        response.setContentType(ct);
        response.setContentLength(dto.getBytes().length);
        // If you want caching add headers here; otherwise keep it simple:
        // response.setHeader("Cache-Control", "public, max-age=86400");
        StreamUtils.copy(dto.getBytes(), response.getOutputStream());
    }

    @PostMapping("/photo/{id}/delete")
    public String deletePhoto(@PathVariable Integer id,
                              RedirectAttributes ra,
                              HttpSession session) {
        AgentDTO loggedInAgent = (AgentDTO) session.getAttribute("loggedInAgent");
        if (loggedInAgent == null) {
            return "redirect:/agentLogin";
        }
        agentService.clearPhoto(id, loggedInAgent.getEmail());
        ra.addFlashAttribute("success", "Profile photo removed");
        return "redirect:/agent/profile/edit";
    }

    // inside AgentProfileController

 /*   @GetMapping
    public String viewProfile(Model model, HttpSession session) {
        AgentDTO loggedInAgent = (AgentDTO) session.getAttribute("loggedInAgent");
        if (loggedInAgent == null) {
            return "redirect:/agentLogin";
        }

        // Optional: refresh from DB to avoid stale session data
        try {
            AgentEntity fresh = agentService.findByEmailEntity(loggedInAgent.getEmail());
            AgentDTO dto = new AgentDTO();
            dto.setAgentId(fresh.getAgentId());
            dto.setFirstName(fresh.getFirstName());
            dto.setLastName(fresh.getLastName());
            dto.setEmail(fresh.getEmail());
            dto.setPhoneNumber(fresh.getPhoneNumber());
            dto.setAddress(fresh.getAddress());
            dto.setTypesOfMilk(fresh.getTypesOfMilk());
            model.addAttribute("agent", dto);
        } catch (Exception e) {
            // fallback to session object
            model.addAttribute("agent", loggedInAgent);
        }

        return "agentProfile"; // <-- resolves to agentProfile.jsp
    }*/

    @GetMapping
    public String viewProfile(Model model, HttpSession session) {
        AgentDTO loggedInAgent = (AgentDTO) session.getAttribute("loggedInAgent");
        if (loggedInAgent == null) {
            return "redirect:/agentLogin";
        }

        // Refresh agent
        try {
            AgentEntity fresh = agentService.findByEmailEntity(loggedInAgent.getEmail());
            AgentDTO dto = new AgentDTO();
            dto.setAgentId(fresh.getAgentId());
            dto.setFirstName(fresh.getFirstName());
            dto.setLastName(fresh.getLastName());
            dto.setEmail(fresh.getEmail());
            dto.setPhoneNumber(fresh.getPhoneNumber());
            dto.setAddress(fresh.getAddress());
            dto.setTypesOfMilk(fresh.getTypesOfMilk());
            model.addAttribute("agent", dto);
        } catch (Exception e) {
            model.addAttribute("agent", loggedInAgent);
        }

        // ---- NEW: load bank info for the card (read-only) ----
        Integer agentId = ((AgentDTO) model.getAttribute("agent")).getAgentId();
        AgentBankEntity bank = agentService.findByAgentId(agentId); // EM-based finder; return null if absent
        if (bank != null) {
            // prepare a lightweight view model with masked account no
            String acc = bank.getAccountNumber() == null ? "" : bank.getAccountNumber().trim();
            String masked = maskAccount(acc); // helper below

            Map<String, Object> bankVm = new HashMap<>();
            bankVm.put("bankName", bank.getBankName());
            bankVm.put("branchName", bank.getBranchName());
            bankVm.put("accountHolderName", bank.getAccountHolderName());
            bankVm.put("ifsc", bank.getIfsc());
            bankVm.put("accountType", bank.getAccountType());
            bankVm.put("maskedAccountNumber", masked);

            model.addAttribute("bankInfo", bankVm);
        } else {
            model.addAttribute("bankInfo", null);
        }
        // ------------------------------------------------------

        return "agentProfile"; // resolves to agentProfile.jsp
    }

    /** Mask account number like ************1234 (last 4 shown). */
    private String maskAccount(String raw) {
        if (raw == null) return "";
        String v = raw.replaceAll("\\s+", "");
        int n = v.length();
        if (n <= 4) return v; // nothing to mask sensibly
        String last4 = v.substring(n - 4);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n - 4; i++) sb.append('*');
        sb.append(last4);
        // optional: group with spaces every 4 for readability
        String masked = sb.toString();
        return masked.replaceAll("(.{4})(?=.)", "$1 ");
    }



    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        // 1) Ensure the user is logged in
        AgentDTO loggedInAgent = (AgentDTO) session.getAttribute("loggedInAgent");
        if (loggedInAgent == null) {
            return "redirect:/agentLogin";
        }

        // 2) (Optional) Refresh the agent record from DB to avoid stale session
        AgentDTO agentForView;
        try {
            AgentEntity fresh = agentService.findByEmailEntity(loggedInAgent.getEmail());
            AgentDTO dto = new AgentDTO();
            dto.setAgentId(fresh.getAgentId());
            dto.setFirstName(fresh.getFirstName());
            dto.setLastName(fresh.getLastName());
            dto.setEmail(fresh.getEmail());
            dto.setPhoneNumber(fresh.getPhoneNumber());
            dto.setAddress(fresh.getAddress());
            dto.setTypesOfMilk(fresh.getTypesOfMilk());
            agentForView = dto;
        } catch (Exception e) {
            agentForView = loggedInAgent; // fallback to session object
        }

        // 3) Add the agent object expected by the JSP
        model.addAttribute("agent", agentForView);

        // 4) Populate the quick stats & recent activity expected by agentLoginSuccess.jsp
        //    (You can replace this with real data anytime.)
      /*  DashboardStats stats = dashboardService.getStatsForAgent(agentForView.getAgentId());

        model.addAttribute("totalDeliveries", stats.getTotalDeliveries());
        model.addAttribute("pendingOrders", stats.getPendingOrders());
        model.addAttribute("monthEarnings", stats.getMonthEarnings()); // ₹ shown in JSP
        model.addAttribute("rating", stats.getRating());                // can be "—" or number
        model.addAttribute("recentActivities", stats.getRecentActivities());*/

        // 5) Render the JSP
        return "agentLoginSuccess"; // resolves to agentLoginSuccess.jsp via your ViewResolver
    }





    @PostMapping("/bank/save")
    public String saveBankInfo(@Valid @ModelAttribute BankForm bankForm,
                               BindingResult result,
                               HttpSession session,
                               RedirectAttributes ra) {

        AgentDTO loggedInAgent = (AgentDTO) session.getAttribute("loggedInAgent");
        if (loggedInAgent == null) {
            return "redirect:/agentLogin";
        }



        if (loggedInAgent.getAgentId() == null) {
            ra.addFlashAttribute("bankError", "Please login again.");
            return "redirect:/agent/profile";
        }

        // prevent tampering: must match session agent
        if (!loggedInAgent.getAgentId().equals(bankForm.getAgentId())) {
            ra.addFlashAttribute("bankError", "Invalid request.");
            return "redirect:/agent/profile";
        }

        // server-side match check (in case JS was bypassed)
        if (!bankForm.getAccountNumber().equals(bankForm.getConfirmAccountNumber())) {
            result.rejectValue("confirmAccountNumber", "Match", "Account numbers must match.");
        }

        if (result.hasErrors()) {
            // put errors into flash so you can show messages (or re-render via forward)
            ra.addFlashAttribute("bankError", "Please correct the highlighted errors.");
            return "redirect:/agent/profile";
        }

        try {
            String createdBy = loggedInAgent.getFirstName()+" "+loggedInAgent.getLastName(); // or current username/email
            agentService.saveFirstTime(bankForm, createdBy,loggedInAgent.getAgentId(),loggedInAgent.getEmail());
            ra.addFlashAttribute("bankSuccess", "Bank details saved successfully. Further edits are locked.");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("bankError", "Bank details already exist. Contact admin for changes.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("bankError", ex.getMessage());
        } catch (Exception ex) {
            ra.addFlashAttribute("bankError", "Something went wrong. Please try again or contact support.");
        }

        return "redirect:/agent/profile";
    }

    @GetMapping("/bank")
    public String routeToBankForm(HttpSession session, RedirectAttributes ra, Model model) {
        AgentDTO loggedInAgent = (AgentDTO) session.getAttribute("loggedInAgent");
        if (loggedInAgent == null) return "redirect:/agentLogin";

        Integer agentId = loggedInAgent.getAgentId();
        if (agentId == null) {
            ra.addFlashAttribute("bankError", "Your session expired. Please sign in again.");
            return "redirect:/agent/profile";
        }

        boolean exists = agentService.existsByAgentId(agentId); // EM-based check
        if (exists) {
            // “real project” style message
            ra.addFlashAttribute("bankInfoMsg",
                    "Your bank details are already on file and locked to protect your payouts. " +
                            "If you need to update them, please raise a request with Payroll Support or contact your branch admin.");
            return "redirect:/agent/profile";
        }

        // No row yet → open the one-time entry form
        BankForm form = new BankForm();
        form.setAgentId(agentId);
        model.addAttribute("bankForm", form);
        return "bankDetailsForm"; // JSP name (bankDetailsForm.jsp)
    }

 /*   @PostMapping("/bank/save")
    public String saveBankInfo(@Valid @ModelAttribute BankForm bankForm,
                               BindingResult result,
                               HttpSession session,
                               RedirectAttributes ra) {
        AgentDTO loggedInAgent = (AgentDTO) session.getAttribute("loggedInAgent");
        if (loggedInAgent == null) return "redirect:/agentLogin";

        if (loggedInAgent.getAgentId() == null) {
            ra.addFlashAttribute("bankError", "Please login again.");
            return "redirect:/agent/profile";
        }
        if (!loggedInAgent.getAgentId().equals(bankForm.getAgentId())) {
            ra.addFlashAttribute("bankError", "Invalid request.");
            return "redirect:/agent/profile";
        }
        if (!bankForm.getAccountNumber().equals(bankForm.getConfirmAccountNumber())) {
            result.rejectValue("confirmAccountNumber", "Match", "Account numbers must match.");
        }
        if (result.hasErrors()) {
            ra.addFlashAttribute("bankError", "Please correct the highlighted errors.");
            return "redirect:/agent/profile/bank";
        }

        try {
            String createdBy = String.valueOf(loggedInAgent.getAgentId());
            agentService.saveFirstTime(bankForm, createdBy); // EM-based persist guarded by exists check + DB unique
            ra.addFlashAttribute("bankSuccess",
                    "Bank details submitted successfully. Further edits are locked. " +
                            "To request a change, contact Payroll Support.");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("bankInfoMsg",
                    "Bank details already exist and are locked. For changes, please contact Payroll Support.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("bankError", ex.getMessage());
        } catch (Exception ex) {
            ra.addFlashAttribute("bankError", "Something went wrong. Please try again or contact support.");
        }
        return "redirect:/agent/profile";
    }*/




}
