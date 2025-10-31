package com.xworkz.happycow.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PaymentService {
    /**
     * Settles a 1..15 days-ago window for the given agent.
     * Server recomputes totals; throws if mismatch / already paid.
     * Returns referenceNo.
     */
    String settleAgentWindow(Integer agentId, LocalDate from, LocalDate to, BigDecimal clientAmount, Integer adminId);
}
