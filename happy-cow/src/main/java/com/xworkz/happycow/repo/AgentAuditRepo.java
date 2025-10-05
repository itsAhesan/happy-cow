package com.xworkz.happycow.repo;

import com.xworkz.happycow.entity.AgentAuditEntity;
import com.xworkz.happycow.entity.AgentEntity;

public interface AgentAuditRepo {
    void save(AgentAuditEntity audit);

    AgentAuditEntity findByAgent(AgentEntity agent);
    void update(AgentAuditEntity audit);

   // void updateAgentByTime(AgentAuditEntity agentAuditEntity);
}
