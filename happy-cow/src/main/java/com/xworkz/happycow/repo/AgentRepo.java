package com.xworkz.happycow.repo;

import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.dto.BankForm;
import com.xworkz.happycow.dto.PhotoDTO;
import com.xworkz.happycow.entity.AgentAuditEntity;
import com.xworkz.happycow.entity.AgentBankAuditEntity;
import com.xworkz.happycow.entity.AgentBankEntity;
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



    AgentEntity saveOrUpdate(AgentEntity entity);

    PhotoDTO findPhotoDTOById(Integer id);

    void clearPhoto(Integer id);

    boolean saveBankDetails(AgentBankEntity bankEntity);

    boolean existsByAgentId(Integer agentId);

    AgentBankEntity findByAgentId(Integer agentId);

    void saveBankAudit(AgentBankAuditEntity bankAuditEntity);

    void updateAgentAudit(AgentAuditEntity agentAuditEntity);

    AgentAuditEntity findByAgentIdFromAgentAudit(Integer agentId);
}
