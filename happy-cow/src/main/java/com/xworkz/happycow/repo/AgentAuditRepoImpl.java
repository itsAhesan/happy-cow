package com.xworkz.happycow.repo;


import com.xworkz.happycow.entity.AgentAuditEntity;
import com.xworkz.happycow.entity.AgentEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
public class AgentAuditRepoImpl implements AgentAuditRepo {

    @Override
    public AgentAuditEntity findByAgent(AgentEntity agent) {
        try {
            return emf.createEntityManager()
                    .createQuery("SELECT a FROM AgentAuditEntity a WHERE a.agent = :agent", AgentAuditEntity.class)
                    .setParameter("agent", agent)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            log.error("Error finding audit by agent", e);
            return null;
        }
    }

    @Override
    public void update(AgentAuditEntity audit) {
        EntityManager em = null;
        EntityTransaction et = null;
        try {
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();
            em.merge(audit);
            et.commit();
        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Error updating audit", e);
            throw new RuntimeException("Failed to update audit", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Autowired
    private EntityManagerFactory emf;


    @Override
    public void save(AgentAuditEntity audit) {
        EntityManager em = null;
        EntityTransaction et = null;
        try {
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();
            em.persist(audit);
            et.commit();
        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Failed to save AgentAuditEntity: {}", audit, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }


    }

   /* public List<AgentAuditEntity> findAuditsBetween13And15Days() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime thirteenDaysAgo = now.minusDays(13);
            LocalDateTime fifteenDaysAgo = now.minusDays(15);

            return em.createQuery(
                            "SELECT a FROM AgentAuditEntity a " +
                                    "WHERE a.createdOn BETWEEN :fifteenDaysAgo AND :thirteenDaysAgo",
                            AgentAuditEntity.class)
                    .setParameter("thirteenDaysAgo", thirteenDaysAgo)
                    .setParameter("fifteenDaysAgo", fifteenDaysAgo)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching audits for notification", e);
            return Collections.emptyList();
        } finally {
            if (em != null) em.close();
        }
    }*/

    @Override
    public List<AgentAuditEntity> findCreatedBetweenWithAgent(LocalDateTime start, LocalDateTime end) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.createQuery(
                            "SELECT a FROM AgentAuditEntity a " +
                                    "JOIN FETCH a.agent ag " +
                                    "WHERE a.createdOn BETWEEN :start AND :end " +
                                    "AND (ag.active = true OR ag.active IS NULL)",
                            AgentAuditEntity.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public List<AgentAuditEntity> findByAgentAndCreatedBetween(Integer agentId, LocalDateTime start, LocalDateTime end) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.createQuery(
                            "SELECT a FROM AgentAuditEntity a " +
                                    "JOIN FETCH a.agent ag " +
                                    "WHERE ag.agentId = :agentId AND a.createdOn BETWEEN :start AND :end",
                            AgentAuditEntity.class)
                    .setParameter("agentId", agentId)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
        } finally {
            if (em != null) em.close();
        }

    }

    // AgentAuditRepoImpl.java
    public AgentAuditEntity findLatestByAgentId(Integer agentId) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.createQuery(
                            "SELECT a FROM AgentAuditEntity a " +
                                    "WHERE a.agent.agentId = :agentId " +
                                    "ORDER BY a.createdOn DESC",
                            AgentAuditEntity.class)
                    .setParameter("agentId", agentId)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            if (em != null) em.close();
        }


    }

    @Override
    public AgentAuditEntity findById(Long auditId) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.find(AgentAuditEntity.class, auditId);
        } finally {
            if (em != null) em.close();
        }
    }


}
