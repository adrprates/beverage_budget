package com.example.beverage_budget.repository;

import com.example.beverage_budget.model.BudgetIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetIngredientRepository extends JpaRepository<BudgetIngredient, Long> {
    List<BudgetIngredient> findByBudgetId(Long budgetId);

    @Modifying
    @Query("DELETE FROM BudgetIngredient bi WHERE bi.budget.id = :budgetId")
    void deleteByBudgetId(@Param("budgetId") Long budgetId);

}
