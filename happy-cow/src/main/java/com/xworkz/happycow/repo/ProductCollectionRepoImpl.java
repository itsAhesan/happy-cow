package com.xworkz.happycow.repo;

import com.xworkz.happycow.entity.ProductCollectionAuditEntity;
import com.xworkz.happycow.entity.ProductCollectionEntity;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ProductCollectionRepoImpl implements ProductCollectionRepo {



    @Autowired
    private EntityManagerFactory emf;




    @Override
    public List<ProductCollectionEntity> findForAgentBetweenDates(Integer agentId, LocalDate startDate, LocalDate endDate) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.createQuery(
                            "SELECT p FROM ProductCollectionEntity p " +
                                    "JOIN FETCH p.agent ag " +
                                    "WHERE ag.agentId = :agentId " +
                                    "AND p.collectedAt BETWEEN :startDate AND :endDate " +
                                    "ORDER BY p.collectedAt DESC", ProductCollectionEntity.class)
                    .setParameter("agentId", agentId)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        } finally { if (em != null) em.close(); }
    }

    @Override
    public Double sumTotalForAgent(Integer agentId) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            Number n = em.createQuery(
                            "select coalesce(sum(p.totalAmount), 0) " +
                                    "from ProductCollectionEntity p " +
                                    "where p.agent.agentId = :aid",
                            Number.class
                    ).setParameter("aid", agentId)
                    .getSingleResult();

            return n != null ? n.doubleValue() : 0.0;
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public Double sumQuantityForAgent(Integer agentId) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            Number n = em.createQuery(
                            "select coalesce(sum(p.quantity), 0) " +
                                    "from ProductCollectionEntity p " +
                                    "where p.agent.agentId = :aid",
                            Number.class
                    ).setParameter("aid", agentId)
                    .getSingleResult();

            return n != null ? n.doubleValue() : 0.0;
        } finally {
            if (em != null) em.close();
        }
    }



    @Override
    public void save(ProductCollectionEntity entity) {
        EntityManager em = null;
        EntityTransaction et = null;
        try {
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();
            em.persist(entity);
            et.commit();
        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Failed to save ProductCollectionEntity: {}", entity, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }

    @Override
    public void saveAudit(ProductCollectionAuditEntity audit) {
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
            log.error("Failed to save ProductCollectionAuditEntity: {}", audit, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }

   /* @Override
    public List<ProductCollectionEntity> findAllOrdered() {
        EntityManager em = null;
        EntityTransaction et = null;
        try {
            em = emf.createEntityManager();

            return em.createQuery(
                    "SELECT pc FROM ProductCollectionEntity pc ORDER BY pc.productCollectionId DESC",
                    ProductCollectionEntity.class
            ).getResultList();

        } catch (Exception e) {
           log.error("Failed to find details");
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return Collections.emptyList();
    }*/


    @Override
    public List<ProductCollectionEntity> findAllOrdered() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.createQuery(
                    "SELECT pc FROM ProductCollectionEntity pc " +
                            "JOIN FETCH pc.agent a " +
                            "JOIN FETCH pc.admin ad " +
                            "ORDER BY pc.productCollectionId DESC",
                    ProductCollectionEntity.class
            ).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

    public List<ProductCollectionEntity> findByCollectedDate(LocalDate d) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.createQuery(
                    "SELECT pc FROM ProductCollectionEntity pc " +
                            "JOIN FETCH pc.agent a " +
                            "JOIN FETCH pc.admin ad " +
                            "WHERE pc.collectedAt = :d " +
                            "ORDER BY pc.productCollectionId DESC",
                    ProductCollectionEntity.class
            ).setParameter("d", d).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public ProductCollectionEntity findById(Integer id) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.find(ProductCollectionEntity.class, id);
        } catch (Exception e) {
            log.error("Failed to find ProductCollectionEntity by id: {}", id, e);
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }


    }

    // ProductCollectionRepo.java
    public ProductCollectionEntity findByIdWithRelations(Integer id) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            EntityGraph<ProductCollectionEntity> graph =
                    em.createEntityGraph(ProductCollectionEntity.class);
            graph.addAttributeNodes("agent", "admin"); // load both

            Map<String, Object> hints = new HashMap<>();
            hints.put("javax.persistence.fetchgraph", graph);

            return em.find(ProductCollectionEntity.class, id, hints);
        } finally {
            if (em != null) em.close();
        }
    }

    public List<ProductCollectionEntity> findByAgentIdWithRelations(Integer agentId) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            EntityGraph<ProductCollectionEntity> graph =
                    em.createEntityGraph(ProductCollectionEntity.class);
            graph.addAttributeNodes("agent", "admin");

            return em.createQuery(
                            "SELECT p FROM ProductCollectionEntity p WHERE p.agent.agentId = :agentId",
                            ProductCollectionEntity.class)
                    .setParameter("agentId", agentId)
                    .setHint("javax.persistence.fetchgraph", graph)
                    .getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

  /*  public List<ProductCollectionEntity> findForAgentBetweenDates(Integer agentId, LocalDate startDate, LocalDate endDate) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            log.info("findForAgentBetweenDates agentId={}, startDate={}, endDate={}", agentId, startDate, endDate);

           List<ProductCollectionEntity> resultList = em.createQuery(
                            "SELECT p FROM ProductCollectionEntity p " +
                                    "JOIN FETCH p.agent ag " +
                                    "WHERE ag.agentId = :agentId " +
                                    "AND p.collectedAt BETWEEN :startDate AND :endDate " +
                                    "ORDER BY p.collectedAt DESC", ProductCollectionEntity.class)
                    .setParameter("agentId", agentId)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();

        *//*    List<ProductCollectionEntity> resultList = em.createQuery(
                            "SELECT p FROM ProductCollectionEntity p " +
                                    "JOIN FETCH p.agent ag " +
                                    "WHERE ag.agentId = :agentId " +
                                //    "AND p.collectedAt BETWEEN :startDate AND :endDate " +
                                    "ORDER BY p.collectedAt DESC", ProductCollectionEntity.class)
                    .setParameter("agentId", agentId)
                //    .setParameter("startDate", startDate)
                //    .setParameter("endDate", endDate)
                    .getResultList();*//*

            log.info("resultList: {}", resultList);

            log.info("resultList size: {}", (resultList != null ? resultList.size() : 0));
            return resultList;
        } finally {
            if (em != null) em.close();
        }
    }*/


/*

    public List<ProductCollectionEntity> findForAgentBetweenDates(Integer agentId, LocalDate startDate, LocalDate endDate) {        //for notifications
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            List<ProductCollectionEntity> resultList = em.createQuery(
                            "SELECT p FROM ProductCollectionEntity p " +
                                    "JOIN FETCH p.agent ag " +
                                    "WHERE ag.agentId = :agentId " +
                                    "AND p.collectedAt BETWEEN :startDate AND :endDate " +
                                    "ORDER BY p.collectedAt DESC", ProductCollectionEntity.class)
                    .setParameter("agentId", agentId)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();

            */
/*List<ProductCollectionEntity> resultList = em.createQuery(
                            "SELECT p FROM ProductCollectionEntity p " +
                                    "JOIN FETCH p.agent ag " +
                                    "WHERE ag.agentId = :agentId " +
                                    "ORDER BY p.collectedAt DESC", ProductCollectionEntity.class)
                    .setParameter("agentId", agentId)
                    .getResultList();*//*


            log.info("resultList: {}", resultList);
            return resultList;


        } finally { if (em != null) em.close(); }
    }

*/


    // In ProductCollectionRepoImpl
    @Override
    public List<AgentPeriodAggregate> aggregateForAgentsBetweenDates(LocalDate start, LocalDate end) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return em.createQuery(
                            "SELECT new com.xworkz.happycow.repo.ProductCollectionRepoImpl$AgentPeriodAggregate(" +
                                    "    ag.agentId, ag.firstName, ag.lastName, ag.email, ag.phoneNumber, " +
                                    "    SUM(p.quantity), SUM(p.totalAmount), COUNT(p) ) " +
                                    "FROM ProductCollectionEntity p " +
                                    "JOIN p.agent ag " +
                                    "WHERE p.collectedAt BETWEEN :start AND :end " +
                                    "GROUP BY ag.agentId, ag.firstName, ag.lastName, ag.email, ag.phoneNumber " +
                                    "ORDER BY ag.firstName, ag.lastName", AgentPeriodAggregate.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
        } finally { if (em != null) em.close(); }
    }

    @Value
    @ToString
    @AllArgsConstructor
    public static class AgentPeriodAggregate {
        public final Integer agentId;
        public final String firstName;
        public final String lastName;
        public final String email;
        public final String phoneNumber;
        public final Double sumQuantity;
        public final Double sumTotalAmount;
        public final Long countRows;

        /*public AgentPeriodAggregate(Integer agentId, String firstName, String lastName,
                                    String email, String phoneNumber,
                                    Double sumQuantity, Double sumTotalAmount, Long countRows) {
            this.agentId = agentId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.sumQuantity = sumQuantity;
            this.sumTotalAmount = sumTotalAmount;
            this.countRows = countRows;
        }*/
    }





}
