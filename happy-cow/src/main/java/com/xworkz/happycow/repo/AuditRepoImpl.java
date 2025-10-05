package com.xworkz.happycow.repo;

import com.xworkz.happycow.entity.AdminAuditEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.Optional;


@Slf4j
@Repository
public class AuditRepoImpl implements AuditRepo {

    @Autowired
    private EntityManagerFactory emf;


    @Override
    public void updateAdminAudit(AdminAuditEntity audit) {


        EntityManager manager = null;
        EntityTransaction transaction = null;

        try {
            manager = emf.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();


            manager.merge(audit);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Failed to update AdminAuditEntity: {}", audit, e);
        } finally {
            if (manager != null) {
                manager.close();
            }
        }


    }

    @Override
    public void save(AdminAuditEntity audit) {

        EntityManager manager = null;
        EntityTransaction transaction = null;

        try {
            manager = emf.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();
            manager.persist(audit);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Failed to save AdminAuditEntity: {}", audit, e);
        } finally {
            if (manager != null) {
                manager.close();
            }
        }

    }

    public Optional<AdminAuditEntity> findByAdminId(Integer adminId) {
        EntityManager manager = emf.createEntityManager();
        try {
            return manager.createQuery(
                            "SELECT a FROM AdminAuditEntity a WHERE a.admin.adminId = :adminId",
                            AdminAuditEntity.class)
                    .setParameter("adminId", adminId)
                    .getResultStream()
                    .findFirst();
        } finally {
            manager.close();
        }
    }

    public void saveOrUpdate(AdminAuditEntity audit) {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = emf.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();


            manager.merge(audit);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            throw e;
        } finally {
            if (manager != null) manager.close();
        }
    }

}
