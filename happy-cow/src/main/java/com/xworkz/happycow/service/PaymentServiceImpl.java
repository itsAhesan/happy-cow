package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.PaymentViewDTO;
import com.xworkz.happycow.dto.PaymentWindowDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
// Jakarta if you use it, else manage in repo
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
        emailService.sendPaymentSuccessToAdmin(admin.getEmailId(),agentId, agentName, from, to, serverTotal, pay.getReferenceNo());
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

    public List<AgentPaymentWindowEntity> getAllPaymentWindows() {
        return paymentRepo.findAll();
    }

    public List<AgentPaymentWindowEntity> getPaymentWindowsForAgent(Integer agentId) {
        return paymentRepo.findByAgent(agentId);
    }

    public List<PaymentWindowDTO> getAllPaymentWindowsDto() {
        return paymentRepo.findAllAsDto();
    }
    public List<PaymentWindowDTO> getPaymentWindowsForAgentDto(Integer agentId) {
        return paymentRepo.findByAgentAsDto(agentId);
    }

    @Autowired
    private EntityManagerFactory emf;

    @Transactional(readOnly = true)
    public List<PaymentViewDTO> findPaymentsByAgentId(Integer agentId) {
        EntityManager em = emf.createEntityManager();
        try {
            String q = "SELECT p FROM AgentPaymentWindowEntity p WHERE p.agent.agentId = :aid ORDER BY p.windowStartDate DESC";
            TypedQuery<AgentPaymentWindowEntity> tq = em.createQuery(q, AgentPaymentWindowEntity.class);
            tq.setParameter("aid", agentId);
            List<AgentPaymentWindowEntity> results = tq.getResultList();

            List<PaymentViewDTO> dtos = new ArrayList<>(results.size());
            for (AgentPaymentWindowEntity p : results) {
                PaymentViewDTO dto = mapToDto(p);
                dtos.add(dto);
            }
            return dtos;
        } finally {
            em.close();
        }
    }

    /**
     * Returns a single PaymentViewDTO for a paymentId and agentId.
     */
    @Transactional(readOnly = true)
    public Optional<PaymentViewDTO> findPaymentByIdAndAgentId(Long paymentId, Integer agentId) {
        EntityManager em = emf.createEntityManager();
        try {
            String q = "SELECT p FROM AgentPaymentWindowEntity p WHERE p.paymentId = :pid AND p.agent.agentId = :aid";
            TypedQuery<AgentPaymentWindowEntity> tq = em.createQuery(q, AgentPaymentWindowEntity.class);
            tq.setParameter("pid", paymentId);
            tq.setParameter("aid", agentId);
            List<AgentPaymentWindowEntity> list = tq.getResultList();
            log.info("list: " + list);
            if (list.isEmpty()) return Optional.empty();
            return Optional.of(mapToDto(list.get(0)));
        } finally {
            em.close();
        }
    }

    /** Helper: map an entity to DTO while EM is open. */
    private PaymentViewDTO mapToDto(AgentPaymentWindowEntity p) {
        PaymentViewDTO dto = new PaymentViewDTO();
        dto.setPaymentId(p.getPaymentId());
        dto.setReferenceNo(p.getReferenceNo());
        dto.setWindowStartDate(p.getWindowStartDate());
        dto.setWindowEndDate(p.getWindowEndDate());
        dto.setGrossAmount(p.getGrossAmount());
        dto.setSettledAt(p.getSettledAt());
        dto.setStatus(p.getStatus());

        // safe to read agent fields here while EM is open
        if (p.getAgent() != null) {

            Integer agentId = p.getAgent().getAgentId();
            dto.setAgentId(agentId == null ? null : agentId.intValue());
            String fn = p.getAgent().getFirstName() == null ? "" : p.getAgent().getFirstName();
            String ln = p.getAgent().getLastName() == null ? "" : p.getAgent().getLastName();
            String name = (fn + " " + ln).trim();
            dto.setAgentName(name.isEmpty() ? null : name);
        }
        return dto;
    }

    /*@Override
    public List<AgentPaymentWindowEntity> findPaymentsByAgentId(Integer agentId) {
        List<AgentPaymentWindowEntity> findByAgentID = paymentRepo.findPaymentsByAgentId(agentId);
        return findByAgentID;
    }

    @Override
    public List<AgentPaymentWindowEntity> findPaymentByIdAndAgentId(Long paymentId, Integer agentId) {
        List<AgentPaymentWindowEntity> findByPaymentIdAndAgentId = paymentRepo.findByPaymentIdAndAgentId(paymentId, agentId);
        return findByPaymentIdAndAgentId;
    }*/


}
