package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.PendingPaymentNotification;
import com.xworkz.happycow.entity.AgentAuditEntity;
import com.xworkz.happycow.repo.AgentAuditRepo;
import com.xworkz.happycow.repo.AgentPaymentWindowRepo;
import com.xworkz.happycow.repo.ProductCollectionRepo;
import com.xworkz.happycow.repo.ProductCollectionRepoImpl.AgentPeriodAggregate;
import com.xworkz.happycow.util.BiMonthlyPayCalendar;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    @Autowired
    private  AgentAuditRepo agentAuditRepo;

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

        if (aggs.isEmpty()) return java.util.Collections.emptyList();

        // Build notifications only for unpaid agents with non-zero amounts
        return aggs.stream()
                .filter(a -> a.sumTotalAmount != null && a.sumTotalAmount > 0.0)
                .filter(a -> !paymentRepo.existsForWindow(a.agentId, payable.getStart(), payable.getEnd()))
                .map(a -> {
                    String fullName = (a.firstName != null ? a.firstName : "")
                            + (a.lastName != null && !a.lastName.isEmpty() ? " " + a.lastName : "");

                    BigDecimal amount = new BigDecimal(String.valueOf(a.sumTotalAmount))
                            .setScale(2, java.math.RoundingMode.HALF_UP);

                    String msg = "Salary window " + payable.getLabel()
                            + " • " + a.countRows + " collections • Amount ₹" + amount;

                    return PendingPaymentNotification.builder()
                            .agentId(a.agentId)
                            .agentName(fullName.trim().isEmpty() ? "Agent " + a.agentId : fullName)
                            .email(a.email)
                            .phoneNumber(a.phoneNumber)
                            .createdOn(java.time.LocalDateTime.now(ZONE)) // when we generated
                            .ageInDays(0L)
                            .message(msg)
                            .link("/agent/" + a.agentId +
                                    "/product-collections?from=" + payable.getStart() +
                                    "&to=" + payable.getEnd())
                            .build();
                })
                .collect(Collectors.toList());
    }

   /* public List<PendingPaymentNotification> buildLoginNotifications() {       //working
        LocalDate today = LocalDate.now(ZONE);
        LocalDate start = today.minusDays(15);
        LocalDate end   = today.minusDays(1);

        List<AgentAuditEntity> audits = agentAuditRepo.findCreatedBetweenWithAgent(
                start.atStartOfDay(), end.atTime(java.time.LocalTime.MAX));

        return audits.stream()
                .filter(audit -> {
                    Integer aid = audit.getAgent().getAgentId();
                    // skip if window already paid (exact match or overlap if you prefer stricter)
                    return !paymentRepo.existsForWindow(aid, start, end);
                })
                .map(audit -> {
                    long age = java.time.temporal.ChronoUnit.DAYS.between(
                            audit.getCreatedOn().atZone(java.time.ZoneId.systemDefault()).withZoneSameInstant(ZONE).toLocalDate(),
                            LocalDate.now(ZONE));
                    String agentName = (audit.getAgent() != null)
                            ? audit.getAgent().getFirstName() + (audit.getAgent().getLastName() != null ? " " + audit.getAgent().getLastName() : "")
                            : audit.getAgentName();

                    return PendingPaymentNotification.builder()
                            .agentId(audit.getAgent().getAgentId())
                            .agentName(agentName)
                            .email(audit.getAgent().getEmail())
                            .phoneNumber(audit.getAgent().getPhoneNumber())
                            .createdOn(audit.getCreatedOn())
                            .ageInDays(age)
                            .message("Payment pending for last 15 days window")
                            .link("/agent/" + audit.getAgent().getAgentId() + "/product-collections")
                            .build();
                })
                .collect(java.util.stream.Collectors.toList());
    }

*/

/*
    public List<PendingPaymentNotification> buildLoginNotifications() {
        // Window: 15 days ago 00:00:00  →  13 days ago 23:59:59.999 (inclusive)
        LocalDate today = LocalDate.now(ZONE);
        LocalDateTime start = today.minusDays(15).atStartOfDay();
        LocalDateTime end   = today.minusDays(13).atTime(LocalTime.MAX);

        List<AgentAuditEntity> audits = agentAuditRepo.findCreatedBetweenWithAgent(start, end);

        return audits.stream().map(audit -> {
            long age = ChronoUnit.DAYS.between(
                    audit.getCreatedOn().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZONE).toLocalDate(),
                    LocalDate.now(ZONE)
            );
            String agentName = (audit.getAgent() != null)
                    ? audit.getAgent().getFirstName() + (audit.getAgent().getLastName() != null ? " " + audit.getAgent().getLastName() : "")
                    : audit.getAgentName();

           *//* return PendingPaymentNotification.builder()
                    .agentId(audit.getAgent().getAgentId())
                    .agentName(agentName)
                    .email(audit.getAgent().getEmail())
                    .phoneNumber(audit.getAgent().getPhoneNumber())
                    .createdOn(audit.getCreatedOn())
                    .ageInDays(age)
                    .message("Payment pending since " + age + " days")
                    .link("/agent/" + audit.getAgent().getAgentId() + "/product-collections?window=13-15")
                    .build();*//*

            return PendingPaymentNotification.builder()
                    .agentId(audit.getAgent().getAgentId())
                    .agentName(agentName)
                    .email(audit.getAgent().getEmail())
                    .phoneNumber(audit.getAgent().getPhoneNumber())
                    .createdOn(audit.getCreatedOn())
                    .ageInDays(age)
                    .message("Payment pending since " + age + " days")
                    // 👇 include auditId so the page uses THIS audit's createdOn as base
                    .link("/agent/" + audit.getAgent().getAgentId()
                            + "/product-collections?window=13-15&auditId=" + audit.getAuditId())
                    .build();

        }).collect(java.util.stream.Collectors.toList());
    }*/

    // Click-through helper
  /*  public ProductCollectionView getProductCollectionsForAgentWindow(Integer agentId) {
        LocalDate today = LocalDate.now(ZONE);
        LocalDate startDate = today.minusDays(15);
        LocalDate endDate   = today.minusDays(13);
        return new ProductCollectionView(
                agentId,
                productCollectionRepo.findForAgentBetweenDates(agentId, startDate, endDate)
        );
    }*/

    @lombok.Value
    public static class ProductCollectionView {
        Integer agentId;
        List<com.xworkz.happycow.entity.ProductCollectionEntity> rows;
    }
}
