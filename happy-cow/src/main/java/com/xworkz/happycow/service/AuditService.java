package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.AdminDTO;
import com.xworkz.happycow.entity.AdminEntity;

public interface AuditService {

    public void logAdminLogin(AdminEntity adminEntity);
    public void logAdminLogout(AdminEntity adminEntity);

    void logAdminAuditSave(AdminDTO adminDTO);
}
