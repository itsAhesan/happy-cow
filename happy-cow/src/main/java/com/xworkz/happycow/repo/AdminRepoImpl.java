package com.xworkz.happycow.repo;

import com.xworkz.happycow.entity.AdminAuditEntity;
import com.xworkz.happycow.entity.AdminEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.Optional;

@Repository
@Slf4j
public class AdminRepoImpl implements AdminRepo {

    @Autowired
    private EntityManagerFactory emf;

    @Override
    public Boolean save(AdminEntity adminEntity) {
        EntityManager em = null;
        EntityTransaction et = null;

        try {
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();

            // 🔑 create audit record and link
            AdminAuditEntity audit = new AdminAuditEntity();
            audit.setAdmin(adminEntity);   // link audit → admin
            adminEntity.setAudit(audit);   // link admin → audit
            audit.setAdminName(adminEntity.getAdminName());
            audit.setAction("SAVE");

            log.info("Attempting to save AdminEntity with Audit: {}", adminEntity);

            // persist admin → cascade will persist audit too
            em.persist(adminEntity);

            et.commit();

            log.info("Successfully saved AdminEntity with id: {}", adminEntity.getAdminId());
            return true;

        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Failed to save AdminEntity: {} ", adminEntity, e);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


/*    @Override
    public Boolean save(AdminEntity adminEntity) {
        EntityManager em = null;
        EntityTransaction et = null;

        try {
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();

            log.info("Attempting to save AdminEntity: {}", adminEntity);
            em.persist(adminEntity);
            et.commit();

            log.info("Successfully saved AdminEntity with id: {}", adminEntity.getAdminId());
            return true;

        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Failed to save AdminEntity: {} ", adminEntity, e);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }*/

    @Override
    public AdminEntity findByEmail(String email) {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();
            log.info("Attempting to find AdminEntity by email: {}", email);
            return em.createNamedQuery("findByEmail", AdminEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("Failed to find AdminEntity by email: {}", email, e);
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public AdminEntity findById(Integer adminId) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            log.info("Attempting to find AdminEntity by id: {}", adminId);
            return em.find(AdminEntity.class, adminId);
        } catch (Exception e) {
            log.error("Failed to find AdminEntity by id: {}", adminId, e);
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public boolean updateAdmin(AdminEntity adminEntity) {
        EntityManager em = null;
        EntityTransaction et = null;

        try {
            em = emf.createEntityManager();
            et = em.getTransaction();
            et.begin();

            log.info("Attempting to update AdminEntity: {}", adminEntity);
            AdminEntity existingAdminEntity = em.find(AdminEntity.class, adminEntity.getAdminId());
          /*  if (existingAdminEntity != null) {
                existingAdminEntity.setProfilePicture(adminEntity.getProfilePicture());
                em.merge(existingAdminEntity);
            }*/
            em.merge(adminEntity);
            et.commit();

            log.info("Successfully updated AdminEntity with id: {}", adminEntity.getAdminId());
            return true;
        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            log.error("Failed to update AdminEntity: {}", adminEntity, e);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public AdminEntity findByUnlockToken(String token) {

        EntityManager em = null;


        try {
            em = emf.createEntityManager();


            AdminEntity singleResult = em.createNamedQuery("findByUnlockToken", AdminEntity.class)
                    .setParameter("token", token)
                    .getSingleResult();

            return singleResult;

        } catch (Exception e) {

            log.error("Failed to find AdminEntity by unlock token: {}", token, e);
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }


    }


}
