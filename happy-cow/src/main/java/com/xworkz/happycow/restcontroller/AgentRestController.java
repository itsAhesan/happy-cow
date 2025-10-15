package com.xworkz.happycow.restcontroller;


import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.entity.AgentEntity;
import com.xworkz.happycow.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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




    @GetMapping("agentLogin/check-email")
       public Map<String, Boolean> checkEmailForLogin(@RequestParam String email) {
        boolean exists = agentService.existsByEmail(email);
        // Use a concrete map for Java 8 compatibility
        Map<String, Boolean> body = new HashMap<>();
        body.put("exists", exists);
        return body;
    }

    @PostMapping("agentLogin/sendAgentOtp")
    public Map<String, Object> sendAgentOtp(@RequestParam String email) {
        boolean sent = agentService.sendOtp(email);
        Map<String, Object> body = new HashMap<>();
        body.put("sent", sent);
        return body;  // jQuery will get JSON: { "sent": true/false }
    }

    @PostMapping("agentLogin/verifyAgentOtp")
    public Map<String, Object> verifyAgentOtp(@RequestParam String email, @RequestParam String otp, HttpSession session) {
        Map<String, Object> m = new HashMap<>();
        boolean verified = agentService.verifyOtp(email, otp);
        if (verified) {
            AgentEntity entity= agentService.findByEmail(email);

            log.info("Agent found: {}", entity);

            AgentDTO agentDTO=new AgentDTO();


            BeanUtils.copyProperties(entity, agentDTO);

            agentDTO.setProfilePicture(entity.getProfilePicture());
            agentDTO.setProfilePictureContentType(entity.getProfilePictureContentType());

            session.setAttribute("loggedInAgent", agentDTO);

            log.info("Agent logged in successfully: {}", agentDTO);


        }

        m.put("verified", verified);
        log.info("Otp Verified: {}", verified);
        return m;
    }











}
