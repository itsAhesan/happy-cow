package com.xworkz.happycow.controller;

import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.dto.PendingPaymentNotification;
import com.xworkz.happycow.entity.ProductCollectionEntity;
import com.xworkz.happycow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/notifications", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Map<String, Object> getNotificationsForLogin(HttpSession session) {
        // Optional: cache in session so you don't recompute several times during one visit
        @SuppressWarnings("unchecked")
        List<PendingPaymentNotification> cached = (List<PendingPaymentNotification>) session.getAttribute("BELL_ITEMS");
        if (cached == null) {
            cached = notificationService.buildLoginNotifications();
            session.setAttribute("BELL_ITEMS", cached);
            session.setAttribute("BELL_COUNT", cached.size());
        }
        log.info("getNotificationsForLogin method started");
        log.info("cached size: {}", cached.size());
        Map<String, Object> body = new HashMap<>();
        body.put("count", cached.size());
        body.put("items", cached);
        return body;
    }

  /*  @GetMapping(value = "/agent/{agentId}/product-collections", produces = "application/json")
    @ResponseBody
    public NotificationService.ProductCollectionView getProductCollectionsForAgent(
            @PathVariable Integer agentId,
            @RequestParam(name = "window", defaultValue = "13-15") String window
    ) {

        log.info("getProductCollectionsForAgent method started");
        log.info("agentId: {}", agentId);
        log.info("window: {}", window);


        return notificationService.getProductCollectionsForAgentWindow(agentId);
    }*/
}




