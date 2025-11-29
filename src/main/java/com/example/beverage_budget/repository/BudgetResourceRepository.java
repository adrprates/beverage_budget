package com.example.beverage_budget.repository;

import com.example.beverage_budget.model.BudgetResource;
import com.example.beverage_budget.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetResourceRepository extends JpaRepository<BudgetResource, Long> {
    List<BudgetResource> findByBudgetId(Long budgetId);

    @Modifying
    @Query("DELETE FROM BudgetResource br WHERE br.budget.id = :budgetId")
    void deleteByBudgetId(@Param("budgetId") Long budgetId);

    boolean existsByResource(Resource resource);
}