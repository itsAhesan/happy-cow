package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.PendingPaymentNotification;
import com.xworkz.happycow.repo.AgentPaymentWindowRepo;
import com.xworkz.happycow.repo.AgentAuditRepo;
import com.xworkz.happycow.repo.ProductCollectionRepo;
import com.xworkz.happycow.repo.ProductCollectionRepoImpl.AgentPeriodAggregate;
import com.xworkz.happycow.util.BiMonthlyPayCalendar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Autowired
    private AgentAuditRepo agentAuditRepo;

    @Autowired
    private ProductCollectionRepo productCollectionRepo;

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

    @Autowired
    private AgentPaymentWindowRepo paymentRepo;

    public List<PendingPaymentNotification> buildLoginNotifications() {
        LocalDate today = LocalDate.now(ZONE);

        // Only show notifications if we are in a 3-day window
        BiMonthlyPayCalendar.Window payable = BiMonthlyPayCalendar.activePayable(today);
        if (payable == null) {
            return java.util.Collections.emptyList();
        }

        // Aggregate collections for the payable period
        List<AgentPeriodAggregate> aggs =
                productCollectionRepo.aggregateForAgentsBetweenDates(payable.getStart(), payable.getEnd());

        if (aggs == null || aggs.isEmpty()) return java.util.Collections.emptyList();

        // Build notifications only for unpaid agents with non-zero amounts
        return aggs.stream()
                .filter(a -> a.sumTotalAmount != null && a.sumTotalAmount > 0.0)
                .filter(a -> !paymentRepo.existsForWindow(a.agentId, payable.getStart(), payable.getEnd()))
                .map(a -> {
                    String fullName = (a.firstName != null ? a.firstName : "")
                            + (a.lastName != null && !a.lastName.isEmpty() ? " " + a.lastName : "");

                    BigDecimal amount = new BigDecimal(String.valueOf(a.sumTotalAmount))
                            .setScale(2, java.math.RoundingMode.HALF_UP);

                    // Try to recover/normalize label
                    String rawLabel = payable.getLabel();
                    String fixedLabel = recoverLabelHeuristically(rawLabel);

                    // Normalize spacing and prefer en-dash for ranges
                    if (fixedLabel != null) {
                        fixedLabel = fixedLabel.trim()
                                .replaceAll("\\s*-\\s*", " \u2013 ")   // ascii hyphen -> en-dash
                                .replaceAll("[–—]+", "\u2013");       // various dashes -> en-dash
                    } else {
                        fixedLabel = "";
                    }

                    String msg = "Salary window " + fixedLabel
                            + " \u2022 " + a.countRows + " collections \u2022 Amount \u20B9" + amount;

                    return PendingPaymentNotification.builder()
                            .agentId(a.agentId)
                            .agentName(fullName.trim().isEmpty() ? "Agent " + a.agentId : fullName)
                            .email(a.email)
                            .phoneNumber(a.phoneNumber)
                            .createdOn(java.time.LocalDateTime.now(ZONE))
                            .ageInDays(0L)
                            .message(msg)
                            .link("/agent/" + a.agentId +
                                    "/product-collections?from=" + payable.getStart() +
                                    "&to=" + payable.getEnd())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Try several heuristics to recover the label text:
     * 1) If it looks clean, return it.
     * 2) If it contains common mojibake patterns, try:
     *    - ISO-8859-1 -> UTF-8 decode
     *    - Windows-1252 -> UTF-8 decode
     * 3) Try simple string replacements of common mangled fragments (e.g. sequences starting with "â€")
     * 4) Fallback: if label contains two numbers with garbage in between (like "16??31"), replace with en-dash.
     *
     * Logs what strategy fixed it (if any).
     */
    private String recoverLabelHeuristically(String raw) {
        if (raw == null) return null;

        // If already looks OK (contains normal digits and either en-dash or hyphen or plain words), return early
        if (!containsMojibake(raw) && !raw.contains("\uFFFD")) {
            return raw;
        }

        log.info("Label appears corrupted, attempting recovery. raw='{}'", raw);

        // 1) try ISO-8859-1 -> UTF-8
        try {
            String isoToUtf = new String(raw.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            if (!containsMojibake(isoToUtf) && !isoToUtf.contains("\uFFFD")) {
                log.info("Recovered label by ISO-8859-1 -> UTF-8: '{}' -> '{}'", raw, isoToUtf);
                return isoToUtf;
            }
        } catch (Exception e) {
            log.debug("ISO->UTF attempt failed: {}", e.getMessage());
        }

        // 2) try Windows-1252 -> UTF-8 (covers some Windows-specific bytes)
        try {
            Charset win1252 = Charset.forName("windows-1252");
            String winToUtf = new String(raw.getBytes(win1252), StandardCharsets.UTF_8);
            if (!containsMojibake(winToUtf) && !winToUtf.contains("\uFFFD")) {
                log.info("Recovered label by Windows-1252 -> UTF-8: '{}' -> '{}'", raw, winToUtf);
                return winToUtf;
            }
        } catch (Exception e) {
            log.debug("Windows-1252->UTF attempt failed: {}", e.getMessage());
        }

        // 3) quick heuristic replace of common mangled fragments like "â€“", "â€”", "â€œ", "â€\u009d" etc.
        String heuristic = raw
                .replaceAll("â€“|â€”|â€", "\u2013")   // attempt to normalize several variants to en-dash
                .replaceAll("â€¢|â€¢", "\u2022")
                .replaceAll("â‚¹|â‚«|â‚»", "\u20B9")
                .replaceAll("[\\uFFFD\\?]+", ""); // remove replacement chars and stray question marks
        if (!containsMojibake(heuristic) && !heuristic.isEmpty()) {
            log.info("Recovered label by heuristic replacements: '{}' -> '{}'", raw, heuristic);
            return heuristic;
        }

        // 4) Last resort: if label looks like "16??31" — replace garbage between two numbers with en-dash
        String fallback = raw.replaceAll("(\\d{1,2})\\D{1,10}(\\d{1,2})", "$1 \u2013 $2");
        if (!containsMojibake(fallback) && !fallback.equals(raw)) {
            log.info("Recovered label by numeric-range fallback: '{}' -> '{}'", raw, fallback);
            return fallback;
        }

        // Nothing recovered — log a hex dump for diagnostics (inspect logs)
        log.warn("Failed to reliably recover label; raw bytes (UTF-8 hex): {}", hexDump(raw.getBytes(StandardCharsets.UTF_8)));
        return raw; // return original so we don't hide data
    }

    private boolean containsMojibake(String s) {
        if (s == null) return false;
        // common mojibake fragments:
        return s.contains("â€“") || s.contains("â€”") || s.contains("â€¢") || s.contains("â‚¹") ||
                s.contains("â€") || s.contains("\uFFFD") || s.contains("??");
    }

    private String hexDump(byte[] bytes) {
        if (bytes == null) return "";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b)).append(" ");
        }
        return sb.toString().trim();
    }

    @lombok.Value
    public static class ProductCollectionView {
        Integer agentId;
        List<com.xworkz.happycow.entity.ProductCollectionEntity> rows;
    }
}
