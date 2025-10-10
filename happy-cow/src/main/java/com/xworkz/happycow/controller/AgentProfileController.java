package com.xworkz.happycow.controller;


import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.dto.PhotoDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;

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

    @GetMapping
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


}
