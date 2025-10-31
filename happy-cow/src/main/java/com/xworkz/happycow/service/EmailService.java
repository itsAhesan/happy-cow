package com.xworkz.happycow.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface EmailService {
    void sendPaymentSuccessToAdmin(Integer agentId, String agentName, LocalDate from, LocalDate to, BigDecimal amount, String referenceNo);
    void sendPaymentSuccessToAgent(String agentEmail, String agentName, LocalDate from, LocalDate to, BigDecimal amount, String referenceNo);
}
