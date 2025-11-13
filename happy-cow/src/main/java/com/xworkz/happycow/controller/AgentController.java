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

import javax.servlet.http.HttpServletResponse;
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

    @GetMapping("/view/{token}")
    public String viewAgentByToken(@PathVariable String token, Model model) {
        AgentEntity agent = agentService.findByToken(token);
        if (agent == null) {
            return "agentNotFound"; // create JSP/Thymeleaf page
        }
        model.addAttribute("agent", agent);
        return "agentProfile"; // render view showing name, phone, email, etc.
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




    @GetMapping("agentLoginSuccess")
    public String agentLoginSuccess(HttpSession session, Model model, HttpServletResponse response) {
      /*  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0);
*/
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
