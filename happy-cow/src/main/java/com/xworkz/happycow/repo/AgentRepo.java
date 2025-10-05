package com.xworkz.happycow.repo;

import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.entity.AgentEntity;

import java.util.List;

public interface AgentRepo {

    List<AgentEntity> findAll(int pageNumber, int pageSize);

    void save(AgentEntity agent);

    AgentEntity findById(Integer id);

    boolean update(AgentEntity agentEntity);

    boolean delete(Integer id);

    AgentEntity findByEmail(String email);

    AgentEntity findByPhoneNumber(String phoneNumber);

    long countAgents();

    List<AgentEntity> searchAgents(String keyword, int offset, int size);

    long countAgentsBySearch(String keyword);

    List<String> getAllMilkTypes();
}
