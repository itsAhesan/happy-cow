package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.dto.PhotoDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class AgentServiceImpl implements AgentService {

    @Override
    public boolean verifyOtp(String email, String otp) {
        AgentEntity agent = agentRepo.findByEmail(email);
        if (agent == null) {
            return false;
        }
        if (!agent.getOtp().equals(otp)) {
            return false;
        }
        if (agent.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }
        agent.setOtp(null);
        agent.setOtpExpiry(null);
        agentRepo.update(agent);
        return true;
    }

    @Override
    public AgentEntity findByEmail(String email) {
        AgentEntity entity=agentRepo.findByEmail(email);

      //  AgentDTO dto=new AgentDTO();
      //  BeanUtils.copyProperties(entity, dto);

        return entity;
    }

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

    @Override
    public boolean sendOtp(String email) {
        AgentEntity agent = agentRepo.findByEmail(email);
        if (agent == null) {
            log.warn("sendOtp: email not found {}", email);
            return false;
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        agent.setOtp(otp);
        agent.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        agentRepo.update(agent);

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email); // or agent.getEmail() if that’s your field
            msg.setSubject("HappyCow Dairy - Agent Login OTP");
            msg.setText("Hi " + (agent.getFirstName() + " " + agent.getLastName()) + ",\n\n"
                    + "Your OTP for login is: " + otp + "\n"
                    + "It is valid for 5 minutes.\n\n"
                    + "— HappyCow Dairy");
            mailSender.send(msg);
            log.info("OTP sent to {}", email);
            return true;
        } catch (Exception e) {
            log.error("Failed to send OTP to {}", email, e);
            return false;
        }
    }






    @Override
    public AgentEntity findByEmailEntity(String email) {
        AgentEntity e = agentRepo.findByEmail(email);
        if (e == null) throw new IllegalArgumentException("Agent not found");
        return e;
    }



    @Override
    public void updateFromDto(String emailOfLoggedInUser, AgentDTO dto, MultipartFile imageFile) {
        AgentEntity entity = findByEmailEntity(emailOfLoggedInUser);

        // Copy editable fields
        entity.setFirstName(safeTrim(dto.getFirstName()));
        entity.setLastName(safeTrim(dto.getLastName()));
        entity.setAddress(safeTrim(dto.getAddress()));
        entity.setTypesOfMilk(safeTrim(dto.getTypesOfMilk()));
        // email/phone are read-only in UI; do not change here.

        // Image (optional)
        if (imageFile != null && !imageFile.isEmpty()) {
            validateImage(imageFile);
            try {
                entity.setProfilePicture(imageFile.getBytes());
                entity.setProfilePictureContentType(imageFile.getContentType());
            } catch (Exception ex) {
                throw new IllegalArgumentException("Failed to read uploaded image");
            }
        }

        agentRepo.saveOrUpdate(entity);
    }

    @Override
    public PhotoDTO findPhotoById(Integer id) {
        return agentRepo.findPhotoDTOById(id); // may be null
    }

    @Override
    public void clearPhoto(Integer id, String requesterEmail) {
        AgentEntity target = agentRepo.findById(id);
        if (target == null) {
            throw new IllegalArgumentException("Agent not found");
        }
        // Basic ownership check
        if (!target.getEmail().equalsIgnoreCase(requesterEmail)) {
            throw new SecurityException("Not allowed");
        }
        agentRepo.clearPhoto(id);
    }

    private void validateImage(MultipartFile file) {
        long maxBytes = 2L * 1024 * 1024; // 2 MB
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("Image too large (max 2 MB)");
        }
        String ct = file.getContentType();
        if (ct == null || !(ct.equalsIgnoreCase("image/jpeg")
                || ct.equalsIgnoreCase("image/png")
                || ct.equalsIgnoreCase("image/webp"))) {
            throw new IllegalArgumentException("Only JPG, PNG, or WEBP allowed");
        }
    }

    private String safeTrim(String s) {
        return s == null ? null : s.trim();
    }


}
