package com.xworkz.happycow.repo;

import com.xworkz.happycow.entity.AdminEntity;

import java.util.Optional;

public interface AdminRepo {
    Boolean save(AdminEntity adminEntity);

    AdminEntity findByEmail(String email);

    AdminEntity findById(Integer adminId);

    boolean updateAdmin(AdminEntity adminEntity);

    AdminEntity findByUnlockToken(String token);

}
