package com.xworkz.happycow.repo;

import com.xworkz.happycow.entity.AgentAuditEntity;
import com.xworkz.happycow.entity.AgentEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface AgentAuditRepo {
    void save(AgentAuditEntity audit);

    AgentAuditEntity findByAgent(AgentEntity agent);
    void update(AgentAuditEntity audit);

   // List<AgentAuditEntity> findAuditsBetween13And15Days();

    // void updateAgentByTime(AgentAuditEntity agentAuditEntity);

    List<AgentAuditEntity> findCreatedBetweenWithAgent(LocalDateTime start, LocalDateTime end);
    List<AgentAuditEntity> findByAgentAndCreatedBetween(Integer agentId, LocalDateTime start, LocalDateTime end);

    AgentAuditEntity findLatestByAgentId(Integer agentId);

    AgentAuditEntity findById(Long auditId);
}
