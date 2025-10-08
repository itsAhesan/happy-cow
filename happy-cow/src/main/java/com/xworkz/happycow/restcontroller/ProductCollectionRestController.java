package com.xworkz.happycow.restcontroller;


import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.entity.AgentEntity;
import com.xworkz.happycow.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/")
public class ProductCollectionRestController {


    @Autowired
    private AgentService agentService;



    @GetMapping("productCollection/getAgentByPhoneNumber")
    public ResponseEntity<AgentDTO> getAgentByPhoneNumber(@RequestParam String phoneNumber, Model model){

        log.info("getAgentByPhoneNumber method started");

        AgentDTO agentDTO = agentService.getAgentByPhoneNumber(phoneNumber);

       return ResponseEntity.ok(agentDTO);

    }


}
