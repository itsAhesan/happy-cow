package com.xworkz.happycow.service;

import org.springframework.mail.SimpleMailMessage;

import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface EmailService {
    void sendPaymentSuccessToAdmin(String adminEmail, Integer agentId, String agentName, LocalDate from, LocalDate to, BigDecimal amount, String referenceNo);
    void sendPaymentSuccessToAgent(String agentEmail, String agentName, LocalDate from, LocalDate to, BigDecimal amount, String referenceNo);

    void sendAgentLoginOtpAsync(SimpleMailMessage msg);

    void sendUnlockEmail(SimpleMailMessage message);

    void sendAgentRegistrationEmail(MimeMessage message);

    void sendAgentResetOtpAsync(SimpleMailMessage message);

    void sendAgentBankDetailsSuccessEmail(SimpleMailMessage message);
}
