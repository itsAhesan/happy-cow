package com.xworkz.happycow.repo;

import com.xworkz.happycow.entity.ProductCollectionAuditEntity;
import com.xworkz.happycow.entity.ProductCollectionEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductCollectionRepo {
    void save(ProductCollectionEntity entity);

    void saveAudit(ProductCollectionAuditEntity audit);

    List<ProductCollectionEntity> findAllOrdered();

    List<ProductCollectionEntity> findByCollectedDate(LocalDate d);


    ProductCollectionEntity findById(Integer id);

    ProductCollectionEntity findByIdWithRelations(Integer id);

    List<ProductCollectionEntity> findByAgentIdWithRelations(Integer agentId);

    List<ProductCollectionEntity> findForAgentBetweenDates(Integer agentId, LocalDate startDate, LocalDate endDate);

    // In ProductCollectionRepo interface
    List<ProductCollectionRepoImpl.AgentPeriodAggregate> aggregateForAgentsBetweenDates(LocalDate start, LocalDate end);

    Double sumTotalForAgent(Integer agentId);

    // Optional: if you want total quantity as well
    Double sumQuantityForAgent(Integer agentId);


    Double getTodayCollectionLiters(Integer agentId);

    Double getTodayEarnings(Integer agentId);
}
