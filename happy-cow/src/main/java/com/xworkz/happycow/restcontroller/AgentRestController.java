package com.xworkz.happycow.restcontroller;


import com.xworkz.happycow.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentRestController {


    @Autowired
    private AgentService agentService;

    @GetMapping("/registerAgent/checkEmail")
    public boolean checkEmail(@RequestParam String email) {
        return !agentService.existsByEmail(email); // return false if already registered
    }

    @GetMapping("/registerAgent/checkPhone")
    public boolean checkPhone(@RequestParam String phoneNumber) {
        return !agentService.existsByPhoneNumber(phoneNumber);
    }


}
