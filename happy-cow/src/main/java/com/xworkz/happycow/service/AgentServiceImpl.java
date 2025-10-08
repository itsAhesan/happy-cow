package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.entity.AgentAuditEntity;
import com.xworkz.happycow.entity.AgentEntity;
import com.xworkz.happycow.repo.AgentAuditRepo;
import com.xworkz.happycow.repo.AgentRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentRepo agentRepo;


    @Autowired
    private JavaMailSender mailSender;




   /* @Override
    public List<AgentDTO> getAllAgents() {

        List<AgentEntity> agentEntities = agentRepo.findAll();

        List<AgentDTO> agentDTOs = new ArrayList<>();

        if(agentEntities.isEmpty()) {
            return Collections.emptyList();
        }

        for (AgentEntity agentEntity : agentEntities) {
            AgentDTO agentDTO = new AgentDTO();
            BeanUtils.copyProperties(agentEntity, agentDTO);
            agentDTOs.add(agentDTO);

        }
        return agentDTOs;
    }*/


    @Override
    public List<AgentDTO> getAllAgents(int pageNumber, int pageSize) {
        List<AgentEntity> agentEntities = agentRepo.findAll(pageNumber, pageSize);
        List<AgentDTO> agentDTOs = new ArrayList<>();

        for (AgentEntity agentEntity : agentEntities) {
            AgentDTO agentDTO = new AgentDTO();
            BeanUtils.copyProperties(agentEntity, agentDTO);
            agentDTOs.add(agentDTO);
        }
        return agentDTOs;
    }


    @Autowired
    private AgentAuditRepo agentAuditRepo;


    @Transactional
    public void registerAgent(AgentDTO agent, String adminName) {

        AgentEntity agentEntity = new AgentEntity();
        BeanUtils.copyProperties(agent, agentEntity);
        // ✅ 1. Save Agent
        agentRepo.save(agentEntity);

        // ✅ 2. Save Audit Log
        AgentAuditEntity audit = new AgentAuditEntity();
        audit.setAgent(agentEntity);
        audit.setAgentName(agent.getFirstName() + " " + agent.getLastName());
        audit.setCreatedBy(adminName);
        //  audit.setUpdatedBy(adminName); // first time same as created
        audit.setUpdatedBy(adminName);
        audit.setCreatedOn(LocalDateTime.now());
        // audit.setUpdatedOn(LocalDateTime.now());

        agentAuditRepo.save(audit);

        // ✅ 3. Send Success Email
        String subject = "Welcome to HappyCow Dairy!";
        String body = "Dear " + agent.getFirstName() + ",\n\n"
                + "Congratulations! Your registration as an agent with HappyCow Dairy was successful.\n"
                + "You can now log in and manage your profile.\n\n"
                + "Best regards,\nHappyCow Dairy Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(agent.getEmail());
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        //  emailService.sendEmail(savedAgent.getEmail(), subject, body);
    }

    @Override
    public AgentDTO findById(Integer id) {

        AgentEntity agentEntity = agentRepo.findById(id);
        if (agentEntity == null) {
            return null;
        }
        AgentDTO agentDTO = new AgentDTO();
        BeanUtils.copyProperties(agentEntity, agentDTO);


        return agentDTO;
    }

    @Override
    public boolean updateAgent(AgentDTO agentDTO,String adminName) {

        AgentEntity agentEntity = agentRepo.findById(agentDTO.getAgentId());
        if (agentEntity == null) {
            return false;

        }
        BeanUtils.copyProperties(agentDTO, agentEntity);

        agentRepo.update(agentEntity);

        AgentAuditEntity agentAuditEntity = agentAuditRepo.findByAgent(agentEntity);


        //    AgentAuditEntity agentAuditEntity = new AgentAuditEntity();
        agentAuditEntity.setAgent(agentEntity);  // Set the agent relationship
        agentAuditEntity.setAgentName(agentDTO.getFirstName() + " " + agentDTO.getLastName());
        agentAuditEntity.setUpdatedOn(LocalDateTime.now());
        //  agentAuditEntity.setUpdatedBy(agentDTO.getFirstName() + " " + agentDTO.getLastName());
        agentAuditEntity.setUpdatedBy(adminName);
        agentAuditRepo.update(agentAuditEntity);

        return true;


    }

    @Override
    public boolean deleteAgent(Integer id) {
        AgentEntity agentEntity = agentRepo.findById(id);
        if (agentEntity == null) {
            return false;
        }
        agentRepo.delete(id);
        return true;
    }

    @Override
    public boolean existsByEmail(String email) {

        AgentEntity agentEntity = agentRepo.findByEmail(email);

        if (agentEntity != null) {
            return true;
        }

        return false;
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {

        AgentEntity agentEntity = agentRepo.findByPhoneNumber(phoneNumber);

        if (agentEntity != null) {
            return true;
        }

        return false;
    }

    @Override
    public long getAgentCount() {
        return agentRepo.countAgents();
    }

    public List<AgentDTO> searchAgents(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<AgentEntity> agentEntities = agentRepo.searchAgents(keyword, offset, size);

        List<AgentDTO> agentDTOs = new ArrayList<>();
        for (AgentEntity agentEntity : agentEntities) {
            AgentDTO agentDTO = new AgentDTO();
            BeanUtils.copyProperties(agentEntity, agentDTO);
            agentDTOs.add(agentDTO);

        }
        return agentDTOs;

    }

    public long getAgentSearchCount(String keyword) {
        return agentRepo.countAgentsBySearch(keyword);
    }

    @Override
    public List<String> getAllMilkTypes() {
        return agentRepo.getAllMilkTypes();
    }

    @Override
    public AgentDTO getAgentByPhoneNumber(String phoneNumber) {

        AgentEntity agentEntity = agentRepo.findByPhoneNumber(phoneNumber);

        AgentDTO agentDTO = new AgentDTO();
        BeanUtils.copyProperties(agentEntity, agentDTO);

        return agentDTO;
    }


}
