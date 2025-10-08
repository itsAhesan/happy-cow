package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.entity.AgentEntity;

import java.util.List;

public interface AgentService {

    List<AgentDTO> getAllAgents(int pageNumber, int pageSize);

    void registerAgent(AgentDTO agentDTO, String adminName);

    AgentDTO findById(Integer id);

    boolean updateAgent(AgentDTO agentDTO,String adminName);

    boolean deleteAgent(Integer id);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    long getAgentCount();

    List<AgentDTO> searchAgents(String trim, int page, int size);

    long getAgentSearchCount(String trim);

    List<String> getAllMilkTypes();

    AgentDTO getAgentByPhoneNumber(String phoneNumber);
}
