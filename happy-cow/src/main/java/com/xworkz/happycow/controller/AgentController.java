package com.xworkz.happycow.controller;

import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.entity.AgentEntity;
import com.xworkz.happycow.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/")
public class AgentController {

    @Autowired
    private AgentService agentService;


   /* @GetMapping("agentDashboard")
    public String agentDashboard(HttpSession session, Model model) {

        AdminDTO admin = (AdminDTO) session.getAttribute("loggedInAdmin");

        if (admin == null) {
            return "redirect:/adminLogin";
        }
        List<AgentDTO> agents=agentService.getAllAgents();

        log.info("List of Agents: {}", agents);
        model.addAttribute("agents", agents);

        return "agentDashboard";
    }*/


  /*  @GetMapping("agentDashboard")
    public String agentDashboard(HttpSession session, Model model,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "8") int size) {

        AdminDTO admin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/adminLogin";
        }

        List<AgentDTO> agents = agentService.getAllAgents(page, size);

        long totalAgents = agentService.getAgentCount();
        int totalPages = (int) Math.ceil((double) totalAgents / size);

        model.addAttribute("totalPages", totalPages);
        model.addAttribute("agents", agents);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "agentDashboard";
    }*/


    @GetMapping("agentDashboard")
    public String agentDashboard(HttpSession session, Model model,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "8") int size,
                                 @RequestParam(required = false) String search) {

        AdminDTO admin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/adminLogin";
        }

        List<AgentDTO> agents;
        long totalAgents;

        if (search != null && !search.trim().isEmpty()) {
            // 🔍 Search filter applied
            agents = agentService.searchAgents(search.trim(), page, size);
            totalAgents = agentService.getAgentSearchCount(search.trim());
            model.addAttribute("search", search);
        } else {
            // 📄 Normal pagination
            agents = agentService.getAllAgents(page, size);
            totalAgents = agentService.getAgentCount();
        }

        int totalPages = (int) Math.ceil((double) totalAgents / size);

        model.addAttribute("totalPages", totalPages);
        model.addAttribute("agents", agents);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalRecords", totalAgents);

        return "agentDashboard";
    }

/*
    @GetMapping("registerAgent")
    public String registerAgent() {
        return "registerAgent";
    }*/

    @PostMapping("saveAgent")
    public String saveAgent(@ModelAttribute AgentDTO agentDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        AdminDTO admin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/adminLogin";
        }

        log.info("Registering new Agent: {}", agentDTO);

        agentService.registerAgent(agentDTO, admin.getAdminName());

        redirectAttributes.addFlashAttribute("successMessage", "Agent registered successfully and email sent.");
        return "redirect:/agentDashboard";
    }

    @GetMapping("editAgent")
    public String editAgent(@RequestParam("id") Integer id, Model model) {
        AgentDTO agent = agentService.findById(id);
        if (agent == null) {
            return "redirect:/dashboard"; // fallback
        }

        List<String> milkTypes = agentService.getAllMilkTypes();
        model.addAttribute("milkTypes", milkTypes);


        model.addAttribute("agent", agent);
        return "editAgent";
    }

    // ✅ Update Agent
    @PostMapping("/update")
    public String updateAgent(@ModelAttribute AgentDTO agentDTO, RedirectAttributes redirectAttributes, HttpSession session) {
        AdminDTO admin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/adminLogin";
        }


        boolean updated = agentService.updateAgent(agentDTO,admin.getAdminName());

        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "Agent updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update agent!");
        }

        return "redirect:/agentDashboard";
    }

    // ✅ Delete Agent
    @GetMapping("deleteAgent")
    public String deleteAgent(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        boolean deleted = agentService.deleteAgent(id);

        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Agent deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete agent!");
        }

        return "redirect:/agentDashboard";
    }

    @GetMapping("registerAgent")
    public String showRegisterAgentPage(Model model) {
        List<String> milkTypes = agentService.getAllMilkTypes();
        model.addAttribute("milkTypes", milkTypes);
        return "registerAgent";  // JSP page name
    }


}
