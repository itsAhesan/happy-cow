package com.xworkz.happycow.repo;


import com.xworkz.happycow.entity.AgentAuditEntity;
import com.xworkz.happycow.entity.AgentEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

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


   /* @Override
    public void updateAgentByTime(AgentAuditEntity agentAuditEntity) {
        EntityManager em=null;
        EntityTransaction et=null;
        try {
            em=emf.createEntityManager();
            et=em.getTransaction();
            et.begin();

            em.find(AgentAuditEntity.class, agentAuditEntity.getAuditId());
            em.merge(agentAuditEntity);
            et.commit();
        }catch(Exception e) {
            if(et!=null && et.isActive()) {
                et.rollback();
            }
            log.error("Failed to update AgentAuditEntity: {}", agentAuditEntity, e);
        }finally {
            if(em!=null) {
                em.close();
            }
        }

    }*/

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
}
