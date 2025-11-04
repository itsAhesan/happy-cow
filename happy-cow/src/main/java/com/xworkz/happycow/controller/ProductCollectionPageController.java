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

import javax.servlet.http.HttpServletRequest;
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
      RedirectAttributes ra,
      HttpServletRequest request) { // <-- added request
    try {
      com.xworkz.happycow.dto.AdminDTO admin =
          (com.xworkz.happycow.dto.AdminDTO) session.getAttribute("loggedInAdmin");
      Integer adminId = (admin != null ? admin.getAdminId() : null);

      String ref = paymentService.settleAgentWindow(agentId, from, to, amount, adminId);

      // Clear cached bell notifications so dropdown refreshes on next load
      session.removeAttribute("BELL_ITEMS");
      session.removeAttribute("BELL_COUNT");

      ra.addFlashAttribute("paymentSuccess", "Payment settled successfully (Ref: " + ref + ").");

      if (returnUrl != null && !returnUrl.trim().isEmpty()) {
        String trimmed = returnUrl.trim();

        // Remove the contextPath if accidentally included → avoid double context path
        String ctx = request.getContextPath(); // e.g. "/happy-cow"
        if (ctx != null && !ctx.isEmpty() && trimmed.startsWith(ctx)) {
          trimmed = trimmed.substring(ctx.length());
          if (trimmed.isEmpty()) trimmed = "/";
        }

        // now ensure trimmed is an internal path (starts with '/'), not an absolute URL
        if (trimmed.startsWith("/")
            && !trimmed.startsWith("//")
            && !trimmed.toLowerCase().startsWith("http")) {
          return "redirect:" + trimmed; // Spring will add the app context automatically
        }
      }
    } catch (Exception e) {
      ra.addFlashAttribute("paymentError", e.getMessage());
    }

    // fallback
    return "redirect:/agent/" + agentId + "/product-collections";
  }
}
