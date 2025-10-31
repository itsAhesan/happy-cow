package com.xworkz.happycow.service;

import com.xworkz.happycow.entity.AdminEntity;
import com.xworkz.happycow.entity.AgentEntity;
import com.xworkz.happycow.entity.AgentPaymentWindowEntity;
import com.xworkz.happycow.entity.ProductCollectionEntity;
import com.xworkz.happycow.repo.AgentPaymentWindowRepo;
import com.xworkz.happycow.repo.AgentRepo;
import com.xworkz.happycow.repo.AdminRepo;
import com.xworkz.happycow.repo.ProductCollectionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional; // Jakarta if you use it, else manage in repo
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final ProductCollectionRepo productCollectionRepo;
    private final AgentPaymentWindowRepo paymentRepo;
    private final AgentRepo agentRepo;
    private final AdminRepo adminRepo;
    private final EmailService emailService;

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Override
    @Transactional
    public String settleAgentWindow(Integer agentId, LocalDate from, LocalDate to, BigDecimal clientAmount, Integer adminId) {
        if (agentId == null || from == null || to == null) {
            throw new IllegalArgumentException("Invalid request: agentId/from/to required");
        }
        if (from.isAfter(to)) {
            LocalDate tmp = from; from = to; to = tmp;
        }

        // Idempotency: reject if exactly this window already paid
        if (paymentRepo.existsForWindow(agentId, from, to)) {
            throw new IllegalStateException("This window is already settled.");
        }

        // Recompute totals server side
        List<ProductCollectionEntity> rows = productCollectionRepo.findForAgentBetweenDates(agentId, from, to);
        BigDecimal serverTotal = rows.stream()
                .map(r -> toBD(r.getTotalAmount()))
                .reduce(ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // Optional tolerance to allow paise rounding differences
        if (clientAmount != null) {
            BigDecimal client = clientAmount.setScale(2, RoundingMode.HALF_UP);
            if (serverTotal.subtract(client).abs().compareTo(new BigDecimal("0.01")) > 0) {
                throw new IllegalStateException("Amount mismatch. Expected ₹ " + serverTotal + " but got ₹ " + client);
            }
        }

        // Load entities
        AgentEntity agent = agentRepo.findById(agentId);
        if (agent == null) throw new IllegalStateException("Agent not found: " + agentId);
        AdminEntity admin = (adminId != null ? adminRepo.findById(adminId) : null);

        // Build payment record
        AgentPaymentWindowEntity pay = new AgentPaymentWindowEntity();
        pay.setAgent(agent);
        pay.setWindowStartDate(from);
        pay.setWindowEndDate(to);
        pay.setGrossAmount(serverTotal);
        pay.setSettledAt(LocalDateTime.now());
        pay.setSettledByAdmin(admin);
        pay.setReferenceNo(genRef(agentId, from, to));
        pay.setStatus("SUCCESS");
        pay.setCreatedOn(LocalDateTime.now());
        pay.setUpdatedOn(LocalDateTime.now());

        // Persist
        paymentRepo.save(pay);

        // Emails
        String agentName = (agent.getFirstName() != null ? agent.getFirstName() : "") +
                (agent.getLastName() != null ? " " + agent.getLastName() : "");
        emailService.sendPaymentSuccessToAdmin(agentId, agentName, from, to, serverTotal, pay.getReferenceNo());
        emailService.sendPaymentSuccessToAgent(agent.getEmail(), agentName, from, to, serverTotal, pay.getReferenceNo());

        log.info("Payment settled: agentId={}, window {}..{}, amount={}, ref={}",
                agentId, from, to, serverTotal, pay.getReferenceNo());

        return pay.getReferenceNo();
    }

    private static BigDecimal toBD(Double d) {
        if (d == null) return ZERO;
        return new BigDecimal(String.valueOf(d));
    }

    private static String genRef(Integer agentId, LocalDate from, LocalDate to) {
        return "PMT-" + agentId + "-" + from + "-" + to + "-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }
}
