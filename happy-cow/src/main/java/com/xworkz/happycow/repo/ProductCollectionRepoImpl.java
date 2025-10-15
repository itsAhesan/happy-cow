package com.xworkz.happycow.repo;

import com.xworkz.happycow.entity.ProductCollectionAuditEntity;
import com.xworkz.happycow.entity.ProductCollectionEntity;
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
public class ProductCollectionRepoImpl implements ProductCollectionRepo{

    @Autowired
    private EntityManagerFactory emf;


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
        EntityManager em=null;
        try {
            em=emf.createEntityManager();
            return em.find(ProductCollectionEntity.class, id);
        }catch (Exception e){
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



}
