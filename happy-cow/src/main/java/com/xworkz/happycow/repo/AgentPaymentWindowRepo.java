package com.xworkz.happycow.repo;

import com.xworkz.happycow.entity.AgentPaymentWindowEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
public class AgentPaymentWindowRepo {

    @Autowired
    private EntityManagerFactory emf;

    public AgentPaymentWindowEntity save(AgentPaymentWindowEntity e) {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            em.persist(e);
            tx.commit();
            return e;
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error("Error saving payment record", ex);
            throw ex;
        } finally { if (em != null) em.close(); }
    }

    public boolean existsForWindow(Integer agentId, LocalDate start, LocalDate end) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            Long cnt = em.createQuery(
                            "SELECT COUNT(p) FROM AgentPaymentWindowEntity p " +
                                    "WHERE p.agent.agentId=:aid AND p.windowStartDate=:s AND p.windowEndDate=:e " +
                                    "AND p.status='SUCCESS'", Long.class)
                    .setParameter("aid", agentId)
                    .setParameter("s", start)
                    .setParameter("e", end)
                    .getSingleResult();
            return cnt != null && cnt > 0;
        } finally { if (em != null) em.close(); }
    }

    /** Optional: any overlap with a paid window (safety) */
    public boolean hasPaidOverlap(Integer agentId, LocalDate start, LocalDate end) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            Long cnt = em.createQuery(
                            "SELECT COUNT(p) FROM AgentPaymentWindowEntity p " +
                                    "WHERE p.agent.agentId=:aid AND p.status='SUCCESS' AND " +
                                    "      NOT (p.windowEndDate < :s OR p.windowStartDate > :e)", Long.class)
                    .setParameter("aid", agentId)
                    .setParameter("s", start)
                    .setParameter("e", end)
                    .getSingleResult();
            return cnt != null && cnt > 0;
        } finally { if (em != null) em.close(); }
    }

    public List<AgentPaymentWindowEntity> findByAgent(Integer agentId) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.createQuery(
                            "SELECT p FROM AgentPaymentWindowEntity p " +
                                    "JOIN FETCH p.agent a " +
                                    "LEFT JOIN FETCH p.settledByAdmin ad " +
                                    "WHERE a.agentId=:aid ORDER BY p.windowStartDate DESC", AgentPaymentWindowEntity.class)
                    .setParameter("aid", agentId)
                    .getResultList();
        } finally { if (em != null) em.close(); }
    }

    public AgentPaymentWindowEntity findOne(Integer agentId, LocalDate start, LocalDate end) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            List<AgentPaymentWindowEntity> list = em.createQuery(
                            "SELECT p FROM AgentPaymentWindowEntity p " +
                                    "JOIN FETCH p.agent a " +
                                    "LEFT JOIN FETCH p.settledByAdmin ad " +
                                    "WHERE a.agentId=:aid AND p.windowStartDate=:s AND p.windowEndDate=:e", AgentPaymentWindowEntity.class)
                    .setParameter("aid", agentId)
                    .setParameter("s", start)
                    .setParameter("e", end)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally { if (em != null) em.close(); }
    }





    public Double sumPaidForAgent(Integer agentId) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            Number n = em.createQuery(
                            "select coalesce(sum(p.grossAmount), 0) " +   // grossAmount is BigDecimal
                                    "from AgentPaymentWindowEntity p " +
                                    "where p.agent.agentId = :aid " +
                                    "and p.status = 'SUCCESS'",
                            Number.class
                    ).setParameter("aid", agentId)
                    .getSingleResult();

            return n != null ? n.doubleValue() : 0.0;
        } finally {
            if (em != null) em.close();
        }
    }

}
