package com.xworkz.happycow.repo;

import com.xworkz.happycow.dto.PaymentWindowDTO;
import com.xworkz.happycow.entity.AgentPaymentWindowEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class AgentPaymentWindowRepo {

  @Autowired private EntityManagerFactory emf;

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
    } finally {
      if (em != null) em.close();
    }
  }

  public boolean existsForWindow(Integer agentId, LocalDate start, LocalDate end) {
    EntityManager em = null;
    try {
      em = emf.createEntityManager();
      Long cnt =
          em.createQuery(
                  "SELECT COUNT(p) FROM AgentPaymentWindowEntity p "
                      + "WHERE p.agent.agentId=:aid AND p.windowStartDate=:s AND p.windowEndDate=:e "
                      + "AND p.status='SUCCESS'",
                  Long.class)
              .setParameter("aid", agentId)
              .setParameter("s", start)
              .setParameter("e", end)
              .getSingleResult();
      return cnt != null && cnt > 0;
    } finally {
      if (em != null) em.close();
    }
  }

  /** Optional: any overlap with a paid window (safety) */
  public boolean hasPaidOverlap(Integer agentId, LocalDate start, LocalDate end) {
    EntityManager em = null;
    try {
      em = emf.createEntityManager();
      Long cnt =
          em.createQuery(
                  "SELECT COUNT(p) FROM AgentPaymentWindowEntity p "
                      + "WHERE p.agent.agentId=:aid AND p.status='SUCCESS' AND "
                      + "      NOT (p.windowEndDate < :s OR p.windowStartDate > :e)",
                  Long.class)
              .setParameter("aid", agentId)
              .setParameter("s", start)
              .setParameter("e", end)
              .getSingleResult();
      return cnt != null && cnt > 0;
    } finally {
      if (em != null) em.close();
    }
  }

  public List<AgentPaymentWindowEntity> findByAgent(Integer agentId) {
    EntityManager em = null;
    try {
      em = emf.createEntityManager();
      return em.createQuery(
              "SELECT p FROM AgentPaymentWindowEntity p "
                  + "JOIN FETCH p.agent a "
                  + "LEFT JOIN FETCH p.settledByAdmin ad "
                  + "WHERE a.agentId=:aid ORDER BY p.windowStartDate DESC",
              AgentPaymentWindowEntity.class)
          .setParameter("aid", agentId)
          .getResultList();
    } finally {
      if (em != null) em.close();
    }
  }

  public AgentPaymentWindowEntity findOne(Integer agentId, LocalDate start, LocalDate end) {
    EntityManager em = null;
    try {
      em = emf.createEntityManager();
      List<AgentPaymentWindowEntity> list =
          em.createQuery(
                  "SELECT p FROM AgentPaymentWindowEntity p "
                      + "JOIN FETCH p.agent a "
                      + "LEFT JOIN FETCH p.settledByAdmin ad "
                      + "WHERE a.agentId=:aid AND p.windowStartDate=:s AND p.windowEndDate=:e",
                  AgentPaymentWindowEntity.class)
              .setParameter("aid", agentId)
              .setParameter("s", start)
              .setParameter("e", end)
              .setMaxResults(1)
              .getResultList();
      return list.isEmpty() ? null : list.get(0);
    } finally {
      if (em != null) em.close();
    }
  }

  public Double sumPaidForAgent(Integer agentId) {
    EntityManager em = null;
    try {
      em = emf.createEntityManager();
      Number n =
          em.createQuery(
                  "select coalesce(sum(p.grossAmount), 0) "
                      + // grossAmount is BigDecimal
                      "from AgentPaymentWindowEntity p "
                      + "where p.agent.agentId = :aid "
                      + "and p.status = 'SUCCESS'",
                  Number.class)
              .setParameter("aid", agentId)
              .getSingleResult();

      return n != null ? n.doubleValue() : 0.0;
    } finally {
      if (em != null) em.close();
    }
  }

  public List<AgentPaymentWindowEntity> findAll() {
    EntityManager em = emf.createEntityManager();
    try {
      return em.createQuery(
              "SELECT p FROM AgentPaymentWindowEntity p ORDER BY p.settledAt DESC NULLS LAST, p.windowStartDate DESC",
              AgentPaymentWindowEntity.class)
          .getResultList();
    } finally {
      em.close();
    }
  }

  public List<PaymentWindowDTO> findAllAsDto() {
    EntityManager em = emf.createEntityManager();
    try {
      String q =
          "SELECT new com.xworkz.happycow.dto.PaymentWindowDTO("
              + "p.paymentId, "
              + "p.agent.agentId, "
              + "CONCAT(COALESCE(p.agent.firstName, ''), ' ', COALESCE(p.agent.lastName, '')), "
              + "p.agent.email, "
              + "p.agent.phoneNumber, "
              + "p.windowStartDate, "
              + "p.windowEndDate, "
              + "p.grossAmount, "
              + "p.status, "
              + "p.settledAt, "
              + "p.referenceNo, "
              + "p.createdOn"
              + ") FROM AgentPaymentWindowEntity p "
              + "ORDER BY CASE WHEN p.settledAt IS NULL THEN 1 ELSE 0 END, p.settledAt DESC, p.windowStartDate DESC";

      return em.createQuery(q, PaymentWindowDTO.class).getResultList();
    } finally {
      em.close();
    }
  }

  public List<PaymentWindowDTO> findByAgentAsDto(Integer agentId) {
    EntityManager em = emf.createEntityManager();
    try {
      String q =
          "SELECT new com.xworkz.happycow.dto.PaymentWindowDTO("
              + "p.paymentId, p.agent.agentId, CONCAT(COALESCE(p.agent.firstName, ''), ' ', COALESCE(p.agent.lastName, '')), "
              + "p.agent.email, p.agent.phoneNumber, p.windowStartDate, p.windowEndDate, p.grossAmount, p.status, p.settledAt, p.referenceNo, p.createdOn"
              + ") FROM AgentPaymentWindowEntity p "
              + "WHERE p.agent.agentId = :aid "
              + "ORDER BY CASE WHEN p.settledAt IS NULL THEN 1 ELSE 0 END, p.settledAt DESC, p.windowStartDate DESC";
      return em.createQuery(q, PaymentWindowDTO.class).setParameter("aid", agentId).getResultList();
    } finally {
      em.close();
    }
  }

  public List<AgentPaymentWindowEntity> findPaymentsByAgentId(Integer agentId) {

    EntityManager em = emf.createEntityManager();

    try {
      String q =
          "SELECT p FROM AgentPaymentWindowEntity p WHERE p.agent.agentId = :aid ORDER BY p.windowStartDate DESC";

      List<AgentPaymentWindowEntity> aid = em.createQuery(q, AgentPaymentWindowEntity.class).setParameter("aid", agentId).getResultList();


     // log.info("aid: " + aid);

     // TypedQuery<AgentPaymentWindowEntity> tq = em.createQuery(q, AgentPaymentWindowEntity.class);
    //  log.info("Typed Query: " + tq.getResultList());
     // tq.setParameter("aid", agentId);

      return aid;
    } finally {
      em.close();
    }
  }

  public List<AgentPaymentWindowEntity> findByPaymentIdAndAgentId(
      Long paymentId, Integer agentId) {
    EntityManager em = emf.createEntityManager();
    try {
      String q =
          "SELECT p FROM AgentPaymentWindowEntity p WHERE p.paymentId = :pid AND p.agent.agentId = :aid";

      List<AgentPaymentWindowEntity> resultList = em.createQuery(q, AgentPaymentWindowEntity.class).setParameter("pid", paymentId).setParameter("aid", agentId).getResultList();


    /*  TypedQuery<AgentPaymentWindowEntity> tq = em.createQuery(q, AgentPaymentWindowEntity.class);
      tq.setParameter("pid", paymentId);
      tq.setParameter("aid", agentId);
      List<AgentPaymentWindowEntity> list = tq.getResultList();*/
      if (resultList.isEmpty()) return null;
      return resultList;
    } finally {
      em.close();
    }
  }


  public Double getPendingPayments(Integer agentId) {
    EntityManager em = null;
    try {
      em = emf.createEntityManager();
      Number n = em.createQuery(
                      "SELECT COALESCE(SUM(p.grossAmount), 0) " +
                              "FROM AgentPaymentWindowEntity p " +
                              "WHERE p.agent.agentId = :aid " +
                              "AND p.status <> 'SUCCESS'",
                      Number.class
              ).setParameter("aid", agentId)
              .getSingleResult();

      return n != null ? n.doubleValue() : 0.0;
    } finally {
      if (em != null) em.close();
    }
  }

  public Double getMonthlySettledPayments(Integer agentId) {
    LocalDate now = LocalDate.now();
    int year = now.getYear();
    int month = now.getMonthValue();
    EntityManager em = null;
    try {
      em = emf.createEntityManager();
      Number n = em.createQuery(
                      "SELECT COALESCE(SUM(p.grossAmount), 0) " +
                              "FROM AgentPaymentWindowEntity p " +
                              "WHERE p.agent.agentId = :aid " +
                              "AND p.status = 'SUCCESS' " +
                              "AND YEAR(p.windowEndDate) = :y " +
                              "AND MONTH(p.windowEndDate) = :m",
                      Number.class
              ).setParameter("aid", agentId)
              .setParameter("y", year)
              .setParameter("m", month)
              .getSingleResult();

      return n != null ? n.doubleValue() : 0.0;
    } finally {
      if (em != null) em.close();
    }
  }

  @Autowired
  private ProductCollectionRepoImpl productCollectionRepoImpl;

  public Double getUnsettledAmount(Integer agentId) {
    Double totalCollected = productCollectionRepoImpl.sumTotalForAgent(agentId);
    Double totalPaid = sumPaidForAgent(agentId);
    return totalCollected - totalPaid;



  }
}
