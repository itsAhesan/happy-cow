package com.xworkz.happycow.controller;

import com.xworkz.happycow.entity.AgentAuditEntity;
import com.xworkz.happycow.entity.AgentEntity;
import com.xworkz.happycow.entity.AgentPaymentWindowEntity;
import com.xworkz.happycow.entity.ProductCollectionEntity;
import com.xworkz.happycow.repo.AgentAuditRepo;
import com.xworkz.happycow.repo.AgentPaymentWindowRepo;
import com.xworkz.happycow.repo.AgentRepo;
import com.xworkz.happycow.repo.ProductCollectionRepo;
import com.xworkz.happycow.service.NotificationService;
import com.xworkz.happycow.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProductCollectionPageController {

  @Autowired private AgentAuditRepo agentAuditRepo;

  @Autowired private ProductCollectionRepo productCollectionRepo;

  @Autowired private AgentRepo agentRepo;

  @Autowired private PaymentService paymentService;

  @Autowired private AgentPaymentWindowRepo paymentRepo;

  @GetMapping("/agent/{agentId}/product-collections")
  public String productCollectionsPage(
      @PathVariable Integer agentId,
      @RequestParam(value = "from", required = false) String fromStr,
      @RequestParam(value = "to", required = false) String toStr,
      Model model) {
    log.info("productCollectionsPage: agentId={}, fromStr={}, toStr={}", agentId, fromStr, toStr);

    // 0) Agent header
    AgentEntity agent = null;
    try {
      agent = agentRepo.findById(agentId);
    } catch (Exception e) {
      log.warn("Agent fetch failed", e);
    }
    model.addAttribute("agent", agent);
    model.addAttribute("agentId", agentId);

    // 1) Window selection
    final java.time.ZoneId ZONE = java.time.ZoneId.of("Asia/Kolkata");
    java.time.LocalDate today = java.time.LocalDate.now(ZONE);

    java.time.LocalDate from = null, to = null;
    if (fromStr != null && !fromStr.trim().isEmpty()) {
      try {
        from = java.time.LocalDate.parse(fromStr.trim());
      } catch (Exception ignore) {
      }
    }
    if (toStr != null && !toStr.trim().isEmpty()) {
      try {
        to = java.time.LocalDate.parse(toStr.trim());
      } catch (Exception ignore) {
      }
    }

    java.time.LocalDate startDate;
    java.time.LocalDate endDate;
    String windowLabel;

    if (from != null && to != null) {
      startDate = from;
      endDate = to;
      windowLabel = buildLabelFor(startDate, endDate);
    } else {
      com.xworkz.happycow.util.BiMonthlyPayCalendar.Window active =
          com.xworkz.happycow.util.BiMonthlyPayCalendar.activePayable(today);

      if (active != null) {
        startDate = active.getStart();
        endDate = active.getEnd();
        windowLabel = active.getLabel();
      } else {
        java.time.YearMonth ym = java.time.YearMonth.from(today);
        boolean firstHalf = today.getDayOfMonth() <= 15;
        com.xworkz.happycow.util.BiMonthlyPayCalendar.Window w =
            firstHalf
                ? com.xworkz.happycow.util.BiMonthlyPayCalendar.firstHalf(ym)
                : com.xworkz.happycow.util.BiMonthlyPayCalendar.secondHalf(ym);
        startDate = w.getStart();
        endDate = w.getEnd();
        windowLabel = w.getLabel();
      }
    }

    // 2) Rows for THIS window
    java.util.List<ProductCollectionEntity> rowsWindow =
        productCollectionRepo.findForAgentBetweenDates(agentId, startDate, endDate);

    double winTotal =
        (rowsWindow == null
            ? 0.0
            : rowsWindow.stream()
                .map(ProductCollectionEntity::getTotalAmount)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(Number::doubleValue) // works for Double
                .sum());

    double winQty =
        (rowsWindow == null
            ? 0.0
            : rowsWindow.stream()
                .map(ProductCollectionEntity::getQuantity)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(Number::doubleValue) // works for Float
                .sum());

    // 3) Payment record for this window
    AgentPaymentWindowEntity payment = paymentRepo.findOne(agentId, startDate, endDate);

    // 4) Next half preview
    java.time.YearMonth curYm = java.time.YearMonth.from(startDate);
    boolean currentIsFirstHalf = (startDate.getDayOfMonth() == 1 && endDate.getDayOfMonth() == 15);

    com.xworkz.happycow.util.BiMonthlyPayCalendar.Window nextW =
        currentIsFirstHalf
            ? com.xworkz.happycow.util.BiMonthlyPayCalendar.secondHalf(curYm)
            : com.xworkz.happycow.util.BiMonthlyPayCalendar.firstHalf(curYm.plusMonths(1));

    java.util.List<ProductCollectionEntity> nextRows =
        productCollectionRepo.findForAgentBetweenDates(agentId, nextW.getStart(), nextW.getEnd());

    long nextWindowCount = (nextRows == null ? 0L : nextRows.size());
    double nextWindowTotal =
        (nextRows == null
            ? 0.0
            : nextRows.stream()
                .map(ProductCollectionEntity::getTotalAmount)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(Number::doubleValue)
                .sum());

    // 5) Lifetime aggregates (NEW)
    Double lifetimeTotal =
        productCollectionRepo.sumTotalForAgent(agentId); // ₹ total collected overall
    Double lifetimeQty =
        productCollectionRepo.sumQuantityForAgent(agentId); // total liters overall (optional)
    Double lifetimePaid = paymentRepo.sumPaidForAgent(agentId); // ₹ already paid
    double lifetimePending =
        Math.max(
            (lifetimeTotal != null ? lifetimeTotal : 0.0)
                - (lifetimePaid != null ? lifetimePaid : 0.0),
            0.0);

    // 6) Model
    model.addAttribute("dueStart", startDate);
    model.addAttribute("dueEnd", endDate);
    model.addAttribute("windowLabel", windowLabel);
    model.addAttribute(
        "rowsWindow", rowsWindow != null ? rowsWindow : java.util.Collections.emptyList());
    model.addAttribute("payment", payment);
    model.addAttribute("winQty", winQty);
    model.addAttribute("winTotal", winTotal);

    model.addAttribute("nextWindowStart", nextW.getStart());
    model.addAttribute("nextWindowEnd", nextW.getEnd());
    model.addAttribute("nextWindowLabel", nextW.getLabel());
    model.addAttribute("nextWindowCount", nextWindowCount);
    model.addAttribute("nextWindowTotal", nextWindowTotal);

    // NEW: lifetime stats for display
    model.addAttribute("lifetimeTotal", lifetimeTotal != null ? lifetimeTotal : 0.0);
    model.addAttribute("lifetimeQty", lifetimeQty != null ? lifetimeQty : 0.0);
    model.addAttribute("lifetimePaid", lifetimePaid != null ? lifetimePaid : 0.0);
    model.addAttribute("lifetimePending", lifetimePending);

    log.info(
        "Rendered productCollectionsByAgent: rows={}, winTotal={}, paid?={}, lifetimeTotal={}, lifetimePaid={}, lifetimePending={}",
        (rowsWindow != null ? rowsWindow.size() : 0),
        winTotal,
        (payment != null && "SUCCESS".equals(payment.getStatus())),
        lifetimeTotal,
        lifetimePaid,
        lifetimePending);

    return "productCollectionsByAgent";
  }

  /** Builds a label like "1–15 OCTOBER 2025" or "16–31 MARCH 2025". */
  private String buildLabelFor(java.time.LocalDate start, java.time.LocalDate end) {
    java.time.format.TextStyle TS = java.time.format.TextStyle.FULL;
    java.util.Locale LOCALE = java.util.Locale.ENGLISH;
    String month =
        start.getMonth().getDisplayName(TS, LOCALE).toUpperCase(java.util.Locale.ENGLISH);
    return start.getDayOfMonth() + "–" + end.getDayOfMonth() + " " + month + " " + start.getYear();
  }

  /*   @GetMapping("/agent/{agentId}/product-collections")
      public String productCollectionsPage(@PathVariable Integer agentId,
                                           @RequestParam(required = false) String from,
                                           @RequestParam(required = false) String to,
                                           Model model) {
          final java.time.ZoneId ZONE = java.time.ZoneId.of("Asia/Kolkata");
          final java.time.LocalDate today = java.time.LocalDate.now(ZONE);

          // Use provided period (from notifications) or default to current active window if in one
          java.time.LocalDate startDate = null;
          java.time.LocalDate endDate = null;

          try { if (from != null) startDate = java.time.LocalDate.parse(from); } catch (Exception ignored) {}
          try { if (to   != null) endDate   = java.time.LocalDate.parse(to);   } catch (Exception ignored) {}

          if (startDate == null || endDate == null) {
              com.xworkz.happycow.util.BiMonthlyPayCalendar.Window w =
                      com.xworkz.happycow.util.BiMonthlyPayCalendar.activePayable(today);
              if (w != null) {
                  startDate = w.getStart();
                  endDate = w.getEnd();
              } else {
                  // fallback: 1..15 or 16..end depending on where today lies
                  java.time.YearMonth ym = java.time.YearMonth.from(today);
                  if (today.getDayOfMonth() <= 15) {
                      com.xworkz.happycow.util.BiMonthlyPayCalendar.Window prevSecond =
                              com.xworkz.happycow.util.BiMonthlyPayCalendar.secondHalf(ym.minusMonths(1));
                      startDate = prevSecond.getStart();
                      endDate = prevSecond.getEnd();
                  } else {
                      com.xworkz.happycow.util.BiMonthlyPayCalendar.Window first =
                              com.xworkz.happycow.util.BiMonthlyPayCalendar.firstHalf(ym);
                      startDate = first.getStart();
                      endDate = first.getEnd();
                  }
              }
          }

          // rows for window
          java.util.List<com.xworkz.happycow.entity.ProductCollectionEntity> rowsWindow =
                  productCollectionRepo.findForAgentBetweenDates(agentId, startDate, endDate);

          // agent details for header
          com.xworkz.happycow.entity.AgentEntity agent = agentRepo.findById(agentId);

          // since registration (optional: latest audit or earliest collection date)
          java.time.LocalDate baseDate =
                  rowsWindow.isEmpty() ? startDate : // fallback
                          rowsWindow.stream().map(com.xworkz.happycow.entity.ProductCollectionEntity::getCollectedAt)
                                  .min(java.time.LocalDate::compareTo).orElse(startDate);

          model.addAttribute("agentId", agentId);
          model.addAttribute("agent", agent);
          model.addAttribute("windowLabel", startDate + " to " + endDate);
          model.addAttribute("dueStart", startDate);
          model.addAttribute("dueEnd", endDate);
          model.addAttribute("baseDate", baseDate);
          model.addAttribute("rowsWindow", rowsWindow);

          // Optional: all since registration — if you want it:
          java.util.List<com.xworkz.happycow.entity.ProductCollectionEntity> rowsSinceReg =
                  productCollectionRepo.findForAgentBetweenDates(agentId, baseDate, today);
          model.addAttribute("rowsSinceReg", rowsSinceReg);

          return "productCollectionsByAgent";
      }


  */

  /*   @GetMapping("/agent/{agentId}/product-collections")       //working
      public String productCollectionsPage(@PathVariable Integer agentId,
                                           @RequestParam(required = false) String window,          // now optional/ignored for due logic
                                           @RequestParam(required = false) Long auditId,           // optional: pin registration audit
                                           @RequestParam(required = false) String base,            // optional: yyyy-MM-dd
                                           Model model) {
          final java.time.ZoneId ZONE = java.time.ZoneId.of("Asia/Kolkata");
          final java.time.LocalDate today = java.time.LocalDate.now(ZONE);

          // --- A) Resolve registration baseDate (same as before for "since registration") ---
          java.time.LocalDate baseDate = null;

          if (auditId != null) {
              com.xworkz.happycow.entity.AgentAuditEntity theAudit = agentAuditRepo.findById(auditId);
              if (theAudit != null && theAudit.getCreatedOn() != null) {
                  baseDate = theAudit.getCreatedOn().toLocalDate();
              }
          }
          if (baseDate == null && base != null && !base.isEmpty()) {
              try { baseDate = java.time.LocalDate.parse(base); } catch (Exception ignored) {}
          }
          if (baseDate == null) {
              // fallback to latest audit if nothing else provided
              com.xworkz.happycow.entity.AgentAuditEntity latestAudit = agentAuditRepo.findLatestByAgentId(agentId);
              if (latestAudit != null && latestAudit.getCreatedOn() != null) {
                  baseDate = latestAudit.getCreatedOn().toLocalDate();
              }
          }
          AgentEntity agent = agentRepo.findById(agentId);
          model.addAttribute("agent", agent);


          if (baseDate == null) {
              // still nothing? render empty safely
              model.addAttribute("agentId", agentId);
              model.addAttribute("windowLabel", "Last 15 days");
              model.addAttribute("dueStart", today.minusDays(15));
              model.addAttribute("dueEnd", today.minusDays(1));
              model.addAttribute("baseDate", null);
              model.addAttribute("rowsWindow", java.util.Collections.emptyList());
              model.addAttribute("rowsSinceReg", java.util.Collections.emptyList());
              return "productCollectionsByAgent";
          }

          // --- B) Due window = last 15 days (1..15 days ago) ---
          java.time.LocalDate dueStart = today.minusDays(15);
          java.time.LocalDate dueEnd   = today.minusDays(1);

          // Fetch Due window rows
          java.util.List<com.xworkz.happycow.entity.ProductCollectionEntity> rowsWindow =
                  productCollectionRepo.findForAgentBetweenDates(agentId, dueStart, dueEnd);

          // Fetch All since registration rows
          java.util.List<com.xworkz.happycow.entity.ProductCollectionEntity> rowsSinceReg =
                  productCollectionRepo.findForAgentBetweenDates(agentId, baseDate, today);

          // --- C) Model ---
          model.addAttribute("agentId", agentId);
          model.addAttribute("windowLabel", "Last 15 days"); // for header badge
          model.addAttribute("dueStart", dueStart);
          model.addAttribute("dueEnd", dueEnd);
          model.addAttribute("baseDate", baseDate);
          model.addAttribute("rowsWindow", rowsWindow);
          model.addAttribute("rowsSinceReg", rowsSinceReg);

          return "productCollectionsByAgent";
      }
  */

  @PostMapping("/agent/{agentId}/payments/settle")
  public String settlePayment(
      @PathVariable Integer agentId,
      @RequestParam("from")
          @org.springframework.format.annotation.DateTimeFormat(
              iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
          LocalDate from,
      @RequestParam("to")
          @org.springframework.format.annotation.DateTimeFormat(
              iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
          LocalDate to,
      @RequestParam("amount") java.math.BigDecimal amount,
      @RequestParam(value = "returnUrl", required = false) String returnUrl,
      HttpSession session,
      RedirectAttributes ra) {
    try {
      // current admin id from session (if you store the AdminDTO)
      com.xworkz.happycow.dto.AdminDTO admin =
          (com.xworkz.happycow.dto.AdminDTO) session.getAttribute("loggedInAdmin");
      Integer adminId = (admin != null ? admin.getAdminId() : null);

      String ref = paymentService.settleAgentWindow(agentId, from, to, amount, adminId);

      // Clear cached bell notifications so dropdown refreshes on next load
      session.removeAttribute("BELL_ITEMS");
      session.removeAttribute("BELL_COUNT");

      ra.addFlashAttribute("paymentSuccess", "Payment settled successfully (Ref: " + ref + ").");
    } catch (Exception e) {
      ra.addFlashAttribute("paymentError", e.getMessage());
    }
    return "redirect:/agent/" + agentId + "/product-collections";
  }

  /*  @GetMapping("/agent/{agentId}/product-collections")       //current
      public String productCollectionsPage(@PathVariable Integer agentId,
                                           @RequestParam String window,
                                           @RequestParam(required = false) Long auditId,
                                           @RequestParam(required = false) String base, // yyyy-MM-dd (optional)
                                           Model model) {

          // --- 1) Resolve baseDate (the registration audit that triggered the bell) ---
          LocalDate baseDate = null;
          if (auditId != null) {
              AgentAuditEntity theAudit = agentAuditRepo.findById(auditId);
              if (theAudit != null && theAudit.getCreatedOn() != null) {
                  baseDate = theAudit.getCreatedOn().toLocalDate();
              }
          }
          if (baseDate == null && base != null && !base.isEmpty()) {
              try { baseDate = LocalDate.parse(base); } catch (Exception ignored) {}
          }
          if (baseDate == null) {
              AgentAuditEntity latestAudit = agentAuditRepo.findLatestByAgentId(agentId);
              if (latestAudit != null && latestAudit.getCreatedOn() != null) {
                  baseDate = latestAudit.getCreatedOn().toLocalDate();
              } else {
                  model.addAttribute("agentId", agentId);
                  model.addAttribute("window", window);
                  model.addAttribute("rowsWindow", java.util.Collections.emptyList());
                  model.addAttribute("rowsSinceReg", java.util.Collections.emptyList());
                  return "productCollectionsByAgent";
              }
          }

          // --- 2) Parse window 13-15 ---
          int d1 = 13, d2 = 15;
          try {
              String[] parts = window.split("-");
              d1 = Integer.parseInt(parts[0].trim());
              d2 = Integer.parseInt(parts[1].trim());
          } catch (Exception ignored) {}
          if (d1 > d2) { int t = d1; d1 = d2; d2 = t; }

          LocalDate windowStart = baseDate.plusDays(d1);
          LocalDate windowEnd   = baseDate.plusDays(d2);

          // --- 3) Fetch lists ---
          List<ProductCollectionEntity> rowsWindow =
                  productCollectionRepo.findForAgentBetweenDates(agentId, windowStart, windowEnd);

          LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Kolkata"));
          List<ProductCollectionEntity> rowsSinceReg =
                  productCollectionRepo.findForAgentBetweenDates(agentId, baseDate, today);

          // --- 4) Model ---
          model.addAttribute("agentId", agentId);
          model.addAttribute("window", window);
          model.addAttribute("baseDate", baseDate);
          model.addAttribute("windowStart", windowStart);
          model.addAttribute("windowEnd", windowEnd);
          model.addAttribute("rowsWindow", rowsWindow);
          model.addAttribute("rowsSinceReg", rowsSinceReg);

          return "productCollectionsByAgent";
      }


  */

}
