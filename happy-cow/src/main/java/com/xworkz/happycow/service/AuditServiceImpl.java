package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.entity.AdminAuditEntity;
import com.xworkz.happycow.entity.AdminEntity;
import com.xworkz.happycow.repo.AuditRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AuditServiceImpl implements AuditService{

    @Autowired
    private AuditRepo auditRepo;

    @Autowired
    private AdminService adminService;



    @Override
    public void logAdminAuditSave(AdminDTO adminDTO) {

        AdminEntity admin = adminService.findByEmailEntity(adminDTO.getEmailId());


      /*  AdminEntity admin= new AdminEntity();
        BeanUtils.copyProperties(adminDTO, admin);*/


        AdminAuditEntity audit = new AdminAuditEntity();
        audit.setAdmin(admin);
        audit.setAdminName(admin.getAdminName());
        audit.setAction("SAVE");
        auditRepo.save(audit);

    }



  /*  @Override
    public void logAdminLogin(AdminEntity admin) {
        AdminAuditEntity audit = new AdminAuditEntity();
        audit.setAdmin(admin);
        audit.setAdminName(admin.getAdminName());
        audit.setLoginTime(LocalDateTime.now());
        audit.setAction("LOGIN");
        auditRepo.updateAdminAudit(audit);
        log.info("Audit: Login recorded for {}", admin.getEmailId());




    }*/

 /*   @Autowired
    private EntityManagerFactory emf;*/

    @Override
    public void logAdminLogin(AdminEntity admin) {
        AdminAuditEntity audit = auditRepo.findByAdminId(admin.getAdminId())
                .orElse(new AdminAuditEntity());

        if (audit.getAuditId() == null) {
            audit.setAdmin(admin);
            audit.setAdminName(admin.getAdminName());
        }

        audit.setLoginTime(LocalDateTime.now());
        audit.setLogoutTime(null);
        audit.setAction("LOGIN");

        auditRepo.saveOrUpdate(audit);
        log.info("Audit: Login recorded for {}", admin.getEmailId());
    }
    @Override
    public void logAdminLogout(AdminEntity admin) {
        AdminAuditEntity audit = auditRepo.findByAdminId(admin.getAdminId())
                .orElse(new AdminAuditEntity());

        if (audit.getAuditId() == null) {
            // If somehow logout comes before login (edge case)
            audit.setAdmin(admin);
            audit.setAdminName(admin.getAdminName());
            audit.setLoginTime(null);
        }

        audit.setLogoutTime(LocalDateTime.now());
        audit.setAction("LOGOUT");

        auditRepo.saveOrUpdate(audit);
        log.info("Audit: Logout recorded for {}", admin.getEmailId());
    }




  /*  @Override
    public void logAdminLogin(AdminEntity admin) {
        EntityManager em = emf.createEntityManager();
        try {
            // fetch existing audit
            AdminAuditEntity audit = em.createQuery(
                            "SELECT a FROM AdminAuditEntity a WHERE a.admin.adminId = :adminId",
                            AdminAuditEntity.class)
                    .setParameter("adminId", admin.getAdminId())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (audit == null) {
                // first login → create new audit
                audit = new AdminAuditEntity();
                audit.setAdmin(admin);
                audit.setAdminName(admin.getAdminName());
            }

            // update fields
            audit.setLoginTime(LocalDateTime.now());
            audit.setLogoutTime(null);
            audit.setAction("LOGIN");

            // delegate to repo
            auditRepo.updateAdminAudit(audit);
            log.info("Audit: Login recorded for {}", admin.getEmailId());

        } finally {
            em.close();
        }
    }*/

/*    @Override
    public void logAdminLogout(AdminEntity admin) {
        EntityManager em = emf.createEntityManager();
        try {
            // fetch existing audit for this admin
            AdminAuditEntity audit = em.createQuery(
                            "SELECT a FROM AdminAuditEntity a WHERE a.admin.adminId = :adminId",
                            AdminAuditEntity.class)
                    .setParameter("adminId", admin.getAdminId())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (audit == null) {
                // 🚨 Ideally this should not happen,
                // but if no audit exists, create a new one to avoid null issues
                audit = new AdminAuditEntity();
                audit.setAdmin(admin);
                audit.setAdminName(admin.getAdminName());
                audit.setLoginTime(null);
            }

            // update logout details
            audit.setLogoutTime(LocalDateTime.now());
            audit.setAction("LOGOUT");

            // save changes
            auditRepo.updateAdminAudit(audit);
            log.info("Audit: Logout recorded for {}", admin.getEmailId());

        } finally {
            em.close();
        }
    }*/


 /*   @Override
    public void logAdminLogout(AdminEntity admin) {
        AdminAuditEntity audit = new AdminAuditEntity();
        audit.setAdmin(admin);
        audit.setAdminName(admin.getAdminName());
        audit.setLogoutTime(LocalDateTime.now());
        audit.setAction("LOGOUT");
        auditRepo.updateAdminAudit(audit);
        log.info("Audit: Logout recorded for {}", admin.getEmailId());

    }*/
}
