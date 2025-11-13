package com.xworkz.happycow.controller;


import com.xworkz.happycow.dto.*;
import com.xworkz.happycow.entity.AgentBankEntity;
import com.xworkz.happycow.entity.AgentEntity;

import com.xworkz.happycow.repo.AgentPaymentWindowRepo;
import com.xworkz.happycow.repo.ProductCollectionRepo;
import com.xworkz.happycow.service.AgentService;
import com.xworkz.happycow.service.PaymentService;
import com.xworkz.happycow.service.ProductCollectionService;
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
      byte[] pdf = buildInvoicePdfFromDto(p, logged);

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



    private byte[] buildInvoicePdfFromDto(PaymentViewDTO p, AgentDTO agent) throws IOException {
        // Create document and page
        try (org.apache.pdfbox.pdmodel.PDDocument doc = new org.apache.pdfbox.pdmodel.PDDocument();
             java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {

            org.apache.pdfbox.pdmodel.PDPage page =
                    new org.apache.pdfbox.pdmodel.PDPage(org.apache.pdfbox.pdmodel.common.PDRectangle.A4);
            doc.addPage(page);

            // load fonts (ensure these files exist under /fonts in classpath)
            org.apache.pdfbox.pdmodel.font.PDType0Font font;
            org.apache.pdfbox.pdmodel.font.PDType0Font fontBold;
            try (java.io.InputStream r0 = getClass().getResourceAsStream("/fonts/NotoSans-Regular.ttf");
                 java.io.InputStream r1 = getClass().getResourceAsStream("/fonts/NotoSans-Bold.ttf")) {
                if (r0 == null || r1 == null) {
                    throw new IOException("Required fonts not found under /fonts/ in classpath.");
                }
                font = org.apache.pdfbox.pdmodel.font.PDType0Font.load(doc, r0, true);
                fontBold = org.apache.pdfbox.pdmodel.font.PDType0Font.load(doc, r1, true);
            }

            final float M = 50f; // page margin
            final float PAGE_W = page.getMediaBox().getWidth();
            final float PAGE_H = page.getMediaBox().getHeight();
            final float CONTENT_W = PAGE_W - 2 * M;

            try (org.apache.pdfbox.pdmodel.PDPageContentStream cs =
                         new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page)) {

                // top anchor
                final float topY = PAGE_H - M;

                // --- Draw logo (left) ---
                float logoW = 72f, logoH = 72f;
                boolean logoDrawn = false;
                java.io.InputStream logoIs = getClass().getResourceAsStream("/images/happy-cow-logo.png");
                if (logoIs == null) {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl != null) logoIs = cl.getResourceAsStream("images/happy-cow-logo.png");
                }
                if (logoIs != null) {
                    try (java.io.InputStream ls = logoIs; java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                        byte[] buf = new byte[4096]; int r;
                        while ((r = ls.read(buf)) != -1) baos.write(buf, 0, r);
                        org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject logoImg =
                                org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject.createFromByteArray(doc, baos.toByteArray(), "logo");
                        cs.drawImage(logoImg, M, topY - logoH, logoW, logoH);
                        logoDrawn = true;
                    } catch (Exception ignored) { /* non-fatal */ }
                }

                // --- Company block (left, next to logo) ---
                float companyX = M + (logoDrawn ? logoW + 12f : 0f);
                float companyTopY = topY - 6f;
                float companyBlockHeight = 40f; // reasonable estimate for three lines
                float curY = companyTopY;

                cs.beginText();
                cs.setFont(fontBold, 18f);
                cs.newLineAtOffset(companyX, curY);
                cs.showText("HappyCow");
                cs.endText();

                curY -= 18f;
                cs.beginText();
                cs.setFont(font, 10f);
                cs.newLineAtOffset(companyX, curY);
                cs.showText("123 Milk Street, Bengaluru, India - 560001");
                cs.endText();

                curY -= 12f;
                cs.beginText();
                cs.setFont(font, 9f);
                cs.newLineAtOffset(companyX, curY);
                cs.showText("GSTIN: 29ABCDE1234F1Z5 | Phone: +91-9876543210");
                cs.endText();

                // header actual height is max(logoH, companyBlockHeight)
                float headerHeight = Math.max(logoDrawn ? logoH : 0f, companyBlockHeight);

                // --- Invoice title: place BELOW header (prevents overlap) ---
                String title = "INVOICE";
                float titleSize = 20f;
                float titleW = stringWidth(fontBold, titleSize, title);
                float titleX = M + (CONTENT_W - titleW) / 2f;
                float titleY = topY - headerHeight - 14f; // 14px gap after header
                cs.beginText();
                cs.setFont(fontBold, titleSize);
                cs.newLineAtOffset(titleX, titleY);
                cs.showText(title);
                cs.endText();

                // --- Meta (right side) — start from top of header and cascade down ---
                float rightX = M + CONTENT_W;
                float metaStartY = topY - 6f;
                float metaXWidth = 220f; // limit width for wrapping meta
                String ref = "Invoice Ref: " + (p.getReferenceNo() == null ? "" : p.getReferenceNo());
                java.util.List<String> refLines = wrapText(font, 9f, metaXWidth, ref);

                float metaY = metaStartY;
                for (String ln : refLines) {
                    float lnW = stringWidth(font, 9f, ln);
                    cs.beginText();
                    cs.setFont(font, 9f);
                    cs.newLineAtOffset(rightX - lnW, metaY);
                    cs.showText(ln);
                    cs.endText();
                    metaY -= 11f;
                }

                String pid = "Payment ID: " + (p.getPaymentId() != null ? p.getPaymentId().toString() : "");
                float pidW = stringWidth(font, 9f, pid);
                cs.beginText();
                cs.setFont(font, 9f);
                cs.newLineAtOffset(rightX - pidW, metaY);
                cs.showText(pid);
                cs.endText();
                metaY -= 11f;

                String settled = "Settled at: " + (p.getSettledAt() != null ? p.getSettledAt().toString() : "—");
                float settledW = stringWidth(font, 9f, settled);
                cs.beginText();
                cs.setFont(font, 9f);
                cs.newLineAtOffset(rightX - settledW, metaY);
                cs.showText(settled);
                cs.endText();

                // --- Horizontal separator ---
                float sepY = topY - headerHeight - 40f;
                cs.setStrokingColor(200, 200, 200);
                cs.setLineWidth(0.9f);
                cs.moveTo(M, sepY);
                cs.lineTo(M + CONTENT_W, sepY);
                cs.stroke();

                // --- Billed to block (below separator) ---
                float billedY = sepY - 18f;
                cs.beginText();
                cs.setFont(fontBold, 11f);
                cs.newLineAtOffset(M, billedY);
                cs.showText("Billed to:");
                cs.endText();

                billedY -= 14f;
                String agentName = (agent.getFirstName() == null ? "" : agent.getFirstName())
                        + (agent.getLastName() == null ? "" : " " + agent.getLastName());
                cs.beginText();
                cs.setFont(font, 10f);
                cs.newLineAtOffset(M, billedY);
                cs.showText(agentName.trim());
                cs.endText();

                billedY -= 12f;
                cs.beginText();
                cs.setFont(font, 9f);
                cs.newLineAtOffset(M, billedY);
                cs.showText(agent.getEmail() != null ? agent.getEmail() : "");
                cs.endText();

                // --- Table header and single row ---
                float tableTop = sepY - 60f;
                float tableX = M;
                float tableW = CONTENT_W;
                float amountRight = tableX + tableW - 12f;

                // header background
                cs.setNonStrokingColor(245, 245, 245);
                cs.addRect(tableX, tableTop, tableW, 24f);
                cs.fill();
                cs.setNonStrokingColor(0, 0, 0);

                // header labels
                cs.beginText();
                cs.setFont(fontBold, 10f);
                cs.newLineAtOffset(tableX + 8f, tableTop + 7f);
                cs.showText("Description");
                cs.endText();

                String amtLabel = "Amount";
                float amtLabelW = stringWidth(fontBold, 10f, amtLabel);
                cs.beginText();
                cs.setFont(fontBold, 10f);
                cs.newLineAtOffset(amountRight - amtLabelW, tableTop + 7f);
                cs.showText(amtLabel);
                cs.endText();

                // row content (desc)
                float rowY = tableTop - 28f;
                String desc = "Payout for window "
                        + (p.getWindowStartDate() != null ? p.getWindowStartDate().toString() : "")
                        + " to "
                        + (p.getWindowEndDate() != null ? p.getWindowEndDate().toString() : "");
                java.util.List<String> descLines = wrapText(font, 9f, tableW - 160f, desc);
                float curLnY = rowY + 10f;
                for (String dl : descLines) {
                    cs.beginText();
                    cs.setFont(font, 9f);
                    cs.newLineAtOffset(tableX + 8f, curLnY);
                    cs.showText(dl);
                    cs.endText();
                    curLnY -= 12f;
                }

                // amount right-aligned
                String amountText = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("en", "IN"))
                        .format(p.getGrossAmount() != null ? p.getGrossAmount() : 0.0);
                if (amountText.contains("₹")) amountText = amountText.replace("₹", "Rs.");
                float amtW = stringWidth(font, 9f, amountText);
                cs.beginText();
                cs.setFont(font, 9f);
                cs.newLineAtOffset(amountRight - amtW, rowY + 10f);
                cs.showText(amountText);
                cs.endText();

                // totals
                float totalsY = rowY - 36f;
                cs.beginText();
                cs.setFont(fontBold, 11f);
                cs.newLineAtOffset(amountRight - 140f, totalsY);
                cs.showText("Total:");
                cs.endText();

                String totalText = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("en", "IN"))
                        .format(p.getGrossAmount() != null ? p.getGrossAmount() : 0.0);
                if (totalText.contains("₹")) totalText = totalText.replace("₹", "Rs.");
                float totalTextW = stringWidth(fontBold, 11f, totalText);
                cs.beginText();
                cs.setFont(fontBold, 11f);
                cs.newLineAtOffset(amountRight - totalTextW, totalsY);
                cs.showText(totalText);
                cs.endText();

                // footer notes
                float footerY = totalsY - 72f;
                cs.setNonStrokingColor(110, 110, 110);
                cs.beginText();
                cs.setFont(font, 8.5f);
                cs.newLineAtOffset(M, footerY);
                cs.showText("Payment will be processed to the bank account on record. This is a computer-generated invoice.");
                cs.endText();

                footerY -= 12f;
                cs.beginText();
                cs.setFont(font, 8.5f);
                cs.newLineAtOffset(M, footerY);
                cs.showText("If you have questions, contact payroll@happycow.example or call +91-9876543210.");
                cs.endText();

                cs.setNonStrokingColor(0, 0, 0);
            } // content stream closed

            doc.save(out);
            return out.toByteArray();
        } // document close
    }

    // helper: width for string
    private float stringWidth(org.apache.pdfbox.pdmodel.font.PDFont font, float fontSize, String text) throws IOException {
        if (text == null || text.isEmpty()) return 0f;
        return font.getStringWidth(text) / 1000f * fontSize;
    }

    // simple greedy wrap
    private java.util.List<String> wrapText(org.apache.pdfbox.pdmodel.font.PDFont font, float fontSize, float maxWidth, String text) throws IOException {
        java.util.List<String> lines = new java.util.ArrayList<>();
        if (text == null || text.trim().isEmpty()) return lines;
        String[] words = text.split("\\s+");
        StringBuilder cur = new StringBuilder();
        for (String w : words) {
            String cand = cur.length() == 0 ? w : cur + " " + w;
            if (stringWidth(font, fontSize, cand) <= maxWidth) {
                if (cur.length() == 0) cur.append(w);
                else cur.append(" ").append(w);
            } else {
                if (cur.length() > 0) lines.add(cur.toString());
                cur = new StringBuilder(w);
            }
        }
        if (cur.length() > 0) lines.add(cur.toString());
        return lines;
    }




  }

