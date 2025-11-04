package com.xworkz.happycow.controller;

import com.xworkz.happycow.dto.PaymentWindowDTO;
import com.xworkz.happycow.entity.AgentPaymentWindowEntity;
import com.xworkz.happycow.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class PaymentController {

  @Autowired private final PaymentService paymentService; // implement if not yet

  @GetMapping("/payments/history")
  public String paymentHistory(Model model) {
    List<PaymentWindowDTO> rows =
        paymentService.getAllPaymentWindowsDto(); // service delegates to repo.findAllAsDto()
    model.addAttribute("rows", rows);
    return "paymentHistory";
  }

  @GetMapping("/agent/{agentId}/payments")
  public String paymentHistoryForAgent(@PathVariable Integer agentId, Model model) {
    List<PaymentWindowDTO> rows = paymentService.getPaymentWindowsForAgentDto(agentId);
    model.addAttribute("rows", rows);
    model.addAttribute("agentId", agentId);
    return "paymentHistory";
  }
}
