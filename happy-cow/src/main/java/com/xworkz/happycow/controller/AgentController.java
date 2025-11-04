package com.xworkz.happycow.controller;

import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.dto.BankForm;
import com.xworkz.happycow.entity.AgentBankEntity;
import com.xworkz.happycow.entity.AgentEntity;
import com.xworkz.happycow.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/")
public class AgentController {

    @Autowired
    private AgentService agentService;

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

    @GetMapping("agentLogin")
    public String showAgentLoginPage(Model model) {
        return "agentLoginForm";  // JSP page name
    }


    /*@PostMapping("agentLogin/sendAgentOtp")
    public Map<String, Object> sendAgentOtp(@RequestParam String email) {
        boolean sent = agentService.sendOtp(email);
        Map<String, Object> body = new HashMap<>();
        body.put("sent", sent);
        return body; // works on Java 8
    }*/

    /*@GetMapping("agentLogin/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        boolean exists = agentService.existsByEmail(email);
        // Use a concrete map for Java 8 compatibility
        Map<String, Boolean> body = new HashMap<>();
        body.put("exists", exists);
        return body;
    }

    @PostMapping("agentLogin/sendAgentOtp")
    @ResponseBody
    public Map<String, Object> sendAgentOtp(@RequestParam String email) {
        boolean sent = agentService.sendOtp(email);
        Map<String, Object> body = new HashMap<>();
        body.put("sent", sent);
        return body;  // jQuery will get JSON: { "sent": true/false }
    }*/

   /* @PostMapping("agentLogin/verifyAgentOtp")
    @ResponseBody
    public Map<String, Object> verifyAgentOtp(@RequestParam String email,
                                              @RequestParam String otp) {
        boolean ok = agentService.verifyOtp(email, otp);
        Map<String, Object> body = new HashMap<>();
        body.put("verified", ok);
        return body;  // JSON: { "verified": true/false }
    }*/

    @GetMapping("agentLoginSuccess")
    public String agentLoginSuccess(HttpSession session,Model model) {

        AgentDTO loggedInAgent = (AgentDTO) session.getAttribute("loggedInAgent");
        if (loggedInAgent == null) {
            return "redirect:/agentLogin";
        }

      //  log.info("Agent logged in successfully: {}", loggedInAgent);

        model.addAttribute("agent", loggedInAgent);



        return "agentLoginSuccess";  // JSP page name
    }

    @GetMapping("agentLogout")
    public String agentLogout(HttpSession session,RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out successfully.");
        return "redirect:/agentLogin";
    }

    @GetMapping("agentBankDetails")
    public String agentBankDetails(@RequestParam("id") Integer id,
                                   HttpSession session,
                                   Model model) {

        AdminDTO admin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/adminLogin";
        }

        AgentBankEntity bank = agentService.findByAgentId(id);  // returns null if not found

        if (bank != null) {
            model.addAttribute("bank", bank);
            model.addAttribute("bankMsg", ""); // no message if details exist
        } else {
            model.addAttribute("bank", null);
            model.addAttribute("bankMsg", "Bank details not added yet."); // professional message
        }

        // 5️⃣ Return the JSP view
        return "agentBankDetails";
    }


    @PostMapping("saveBankDetails")
    public String saveBankDetails(@ModelAttribute BankForm form, RedirectAttributes redirectAttributes,HttpSession session) {

        AdminDTO admin = (AdminDTO) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/adminLogin";
        }


        agentService.saveOrUpdateBankDetails(form);
        redirectAttributes.addFlashAttribute("success", "Bank details updated successfully!");
        return "redirect:/agentBankDetails?id=" + form.getAgentId();
    }














}
