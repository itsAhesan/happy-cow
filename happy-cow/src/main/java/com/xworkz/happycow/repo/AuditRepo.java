package com.xworkz.happycow.repo;

import com.xworkz.happycow.entity.AdminAuditEntity;

import java.util.Optional;

public interface AuditRepo {

    void updateAdminAudit(AdminAuditEntity audit);

    void save(AdminAuditEntity audit);

    Optional<AdminAuditEntity> findByAdminId(Integer adminId);

    void saveOrUpdate(AdminAuditEntity audit);
}
