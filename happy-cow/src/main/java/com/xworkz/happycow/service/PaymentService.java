package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.PaymentViewDTO;
import com.xworkz.happycow.dto.PaymentWindowDTO;
import com.xworkz.happycow.entity.AgentPaymentWindowEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    /**
     * Settles a 1..15 days-ago window for the given agent.
     * Server recomputes totals; throws if mismatch / already paid.
     * Returns referenceNo.
     */
    String settleAgentWindow(Integer agentId, LocalDate from, LocalDate to, BigDecimal clientAmount, Integer adminId);

   /* List<AgentPaymentWindowEntity> getAllPaymentWindows();

    List<AgentPaymentWindowEntity> getPaymentWindowsForAgent(Integer agentId);*/

    List<PaymentWindowDTO> getAllPaymentWindowsDto();

    List<PaymentWindowDTO> getPaymentWindowsForAgentDto(Integer agentId);

    List<PaymentViewDTO> findPaymentsByAgentId(Integer agentId);

    Optional<PaymentViewDTO> findPaymentByIdAndAgentId(Long paymentId, Integer agentId);
    
    
/*
    List<AgentPaymentWindowEntity> findPaymentsByAgentId(Integer agentId);

    List<AgentPaymentWindowEntity> findPaymentByIdAndAgentId(Long paymentId, Integer agentId);*/
}
