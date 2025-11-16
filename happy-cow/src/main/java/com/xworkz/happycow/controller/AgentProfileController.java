package com.xworkz.happycow.controller;


import com.xworkz.happycow.dto.*;
import com.xworkz.happycow.entity.AgentBankEntity;
import com.xworkz.happycow.entity.AgentEntity;

import com.xworkz.happycow.repo.AgentPaymentWindowRepo;
import com.xworkz.happycow.repo.ProductCollectionRepo;
import com.xworkz.happycow.service.AgentService;
import com.xworkz.happycow.service.PaymentService;
import com.xworkz.happycow.service.ProductCollectionService;
import com.xworkz.happycow.util.InvoicePdfGenerator;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


import java.io.IOException;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/agent/profile")
public class AgentProfileController {

  @Autowired private AgentService agentService;

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
  public String updateProfile(
      @ModelAttribute("agent") AgentDTO dto,
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
  public void getPhoto(@PathVariable Integer id, HttpServletResponse response)
      throws java.io.IOException {
    PhotoDTO dto = agentService.findPhotoById(id); // may be null
    if (dto == null || dto.getBytes() == null || dto.getBytes().length == 0) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    String ct = (dto.getContentType() == null) ? "image/jpeg" : dto.getContentType();

    response.setContentType(ct);
    response.setContentLength(dto.getBytes().length);
    // If you want caching add headers here; otherwise keep it simple:
    // response.setHeader("Cache-Control", "public, max-age=86400");
    StreamUtils.copy(dto.getBytes(), response.getOutputStream());
  }

  @PostMapping("/photo/{id}/delete")
  public String deletePhoto(@PathVariable Integer id, RedirectAttributes ra, HttpSession session) {
    AgentDTO loggedInAgent = (AgentDTO) session.getAttribute("loggedInAgent");
    if (loggedInAgent == null) {
      return "redirect:/agentLogin";
    }
    agentService.clearPhoto(id, loggedInAgent.getEmail());
    ra.addFlashAttribute("success", "Profile photo removed");
    return "redirect:/agent/profile/edit";
  }

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
    AgentBankEntity bank =
        agentService.findByAgentId(agentId); // EM-based finder; return null if absent
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

    return "agentProfile";
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

    String masked = sb.toString();
    return masked.replaceAll("(.{4})(?=.)", "$1 ");
  }

  @Autowired
  private ProductCollectionRepo productCollectionRepo;

  @Autowired
  private AgentPaymentWindowRepo agentPaymentWindowRepo;

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
      Integer agentId = loggedInAgent.getAgentId();

      // 🔥 Get Quick Stats
      Double todayLiters = productCollectionRepo.getTodayCollectionLiters(agentId);
      Double todayEarnings = productCollectionRepo.getTodayEarnings(agentId);
      //  Double pendingPayments = agentPaymentWindowRepo.getPendingPayments(agentId);
      Double unsettledAmount = agentPaymentWindowRepo.getUnsettledAmount(agentId);
      Double monthlySettledAmount = agentPaymentWindowRepo.getMonthlySettledPayments(agentId);



      model.addAttribute("todayCollectionLiters", todayLiters);
      model.addAttribute("todayEarnings", todayEarnings);
      //  model.addAttribute("pendingPayments", pendingPayments);
      model.addAttribute("unsettledAmount", unsettledAmount);
      model.addAttribute("monthlySettledPayments", monthlySettledAmount);



    // 3) Add the agent object expected by the JSP
    model.addAttribute("agent", agentForView);

    return "agentLoginSuccess";
  }

  @PostMapping("/bank/save")
  public String saveBankInfo(
      @Valid @ModelAttribute BankForm bankForm,
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
      String createdBy =
          loggedInAgent.getFirstName()
              + " "
              + loggedInAgent.getLastName(); // or current username/email
      agentService.saveFirstTime(
          bankForm, createdBy, loggedInAgent.getAgentId(), loggedInAgent.getEmail());
      ra.addFlashAttribute(
          "bankSuccess", "Bank details saved successfully. Further edits are locked.");
    } catch (IllegalStateException ex) {
      ra.addFlashAttribute("bankError", "Bank details already exist. Contact admin for changes.");
    } catch (IllegalArgumentException ex) {
      ra.addFlashAttribute("bankError", ex.getMessage());
    } catch (Exception ex) {
      ra.addFlashAttribute(
          "bankError", "Something went wrong. Please try again or contact support.");
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
      ra.addFlashAttribute(
          "bankInfoMsg",
          "Your bank details are already on file and locked to protect your payouts. "
              + "If you need to update them, please raise a request with Payroll Support or contact your branch admin.");
      return "redirect:/agent/profile";
    }

    // No row yet → open the one-time entry form
    BankForm form = new BankForm();
    form.setAgentId(agentId);
    model.addAttribute("bankForm", form);
    return "bankDetailsForm"; // JSP name (bankDetailsForm.jsp)
  }

  @Autowired private ProductCollectionService productCollectionService;

  @GetMapping("/orders")
  public String showOrdersPage(HttpSession session, Model model) {

    AgentDTO loggedInAgent = (AgentDTO) session.getAttribute("loggedInAgent");
    if (loggedInAgent == null) {
      return "redirect:/agentLogin";
    }

    List<ProductCollectionAndAgentDTO> detailsList =
        productCollectionService.getDetailsDTOByAgentId(loggedInAgent.getAgentId());

    // Add to model
    model.addAttribute("detailsList", detailsList);

    return "agentOrders";
  }

  @Autowired private PaymentService paymentService;

  @GetMapping("/earnings")
  public String showEarnings(Model model, HttpSession session) {
    AgentDTO logged = (AgentDTO) session.getAttribute("loggedInAgent");
    if (logged == null) {
      return "redirect:/agentLogin";
    }

    Integer agentId = logged.getAgentId();
    if (agentId == null) return "redirect:/agentLogin";

    // fetch DTOs (safe)
    List<PaymentViewDTO> payments = paymentService.findPaymentsByAgentId(agentId);

    log.info("Payments : {}", payments);
    log.info("Payments count: {}", payments == null ? 0 : payments.size());
    // safe to log IDs or refs now
    if (payments != null && !payments.isEmpty()) {
      log.info(
          "Payment refs: {}",
          payments.stream().map(PaymentViewDTO::getReferenceNo).collect(Collectors.joining(", ")));
    }

    double total =
        payments.stream()
            .mapToDouble(p -> p.getGrossAmount() == null ? 0.0 : p.getGrossAmount().doubleValue())
            .sum();
    long pending = payments.stream().filter(p -> "PENDING".equalsIgnoreCase(p.getStatus())).count();
    String lastSettled =
        payments.stream()
            .filter(p -> p.getSettledAt() != null)
            .map(p -> p.getSettledAt().toLocalDate().format(DateTimeFormatter.ISO_DATE))
            .findFirst()
            .orElse("—");

    model.addAttribute("payments", payments);
    model.addAttribute("totalPayouts", total);
    model.addAttribute("pendingCount", pending);
    model.addAttribute("lastSettledDate", lastSettled);

    return "earnings";
  }

  @GetMapping("/earnings/{paymentId}/json")
  @ResponseBody
  public Object paymentJson(@PathVariable Long paymentId, HttpSession session) {
    AgentDTO logged = (AgentDTO) session.getAttribute("loggedInAgent");
    if (logged == null) return ResponseEntity.status(401).build();

    Integer agentId = logged.getAgentId();

    Optional<PaymentViewDTO> opt = paymentService.findPaymentByIdAndAgentId(paymentId, agentId);
    if (!opt.isPresent()) {
      return ResponseEntity.status(404)
          .body(Collections.singletonMap("error", "Payment not found"));
    }

    return opt.get();
  }

  @GetMapping("/earnings/{paymentId}/invoice")
  public void downloadInvoice(
      @PathVariable Long paymentId, HttpSession session, HttpServletResponse response)
      throws IOException {
    AgentDTO logged = (AgentDTO) session.getAttribute("loggedInAgent");
    if (logged == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    Integer agentId = logged.getAgentId();
    Optional<PaymentViewDTO> opt = paymentService.findPaymentByIdAndAgentId(paymentId, agentId);

    if (!opt.isPresent()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Payment not found");
      return;
    }

    PaymentViewDTO p = opt.get();

    try {
     
        byte[] pdf = InvoicePdfGenerator.generate(p, logged);

      String filename =
          "invoice_"
              + p.getPaymentId()
              + "_"
              + (p.getWindowStartDate() != null ? p.getWindowStartDate().toString() : "")
              + ".pdf";
      response.setContentType("application/pdf");
      // use inline if you want to open in browser: inline; filename="..."
      response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
      response.setContentLength(pdf.length);
      response.getOutputStream().write(pdf);
      response.getOutputStream().flush();
    } catch (IOException ex) {
      log.error(
          "Failed to generate invoice PDF for paymentId={} agentId={}", paymentId, agentId, ex);
      response.sendError(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to generate invoice");
    }
  }
    @Autowired
    private ServletContext servletContext;


  }

