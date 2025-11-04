package com.xworkz.happycow.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;



    @Override
    public void sendPaymentSuccessToAdmin(String adminEmail, Integer agentId, String agentName, LocalDate from, LocalDate to, BigDecimal amount, String referenceNo) {
        // TODO integrate JavaMailSender or your SMTP impl


        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(adminEmail);
        msg.setSubject("HappyCow Dairy - Agent Payment Received");

        msg.setText("Dear Admin,\n\n"
                + "A payment from an agent has been successfully processed.\n\n"
                + "Agent Details:\n"
                + "----------------------------\n"
                + "Agent ID: " + agentId + "\n"
                + "Agent Name: " + agentName + "\n"
                + "Payment Period: " + from + " to " + to + "\n"
                + "Amount: " + amount + "\n"
                + "Reference No: " + referenceNo + "\n\n"
                + "Please verify and update the payment records accordingly.\n\n"
                + "— HappyCow Dairy System");

        mailSender.send(msg);




        log.info("[EMAIL->ADMIN] Payment SUCCESS | agentId={}, agentName='{}', window={}..{}, amount={}, ref={}",
                agentId, agentName, from, to, amount, referenceNo);
    }

    @Override
    public void sendPaymentSuccessToAgent(String agentEmail, String agentName, LocalDate from, LocalDate to, BigDecimal amount, String referenceNo) {
        // TODO integrate JavaMailSender or your SMTP impl

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(agentEmail); // or agent.getEmail() if that’s your field
        msg.setSubject("HappyCow Dairy - Agent Payment Success");
        msg.setText("Hi " + agentName + ",\n\n"
                + "Your payment for Product " + from + " to " + to + " is successful.\n\n"
                + "Amount: " + amount + "\n"
                + "Reference No: " + referenceNo + "\n\n"
                + "— HappyCow Dairy");
        mailSender.send(msg);



        log.info("[EMAIL->AGENT:{}] Payment SUCCESS | agentName='{}', window={}..{}, amount={}, ref={}",
                agentEmail, agentName, from, to, amount, referenceNo);
    }
}
