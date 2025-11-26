package com.example.beverage_budget.repository;

import com.example.beverage_budget.model.BudgetAutoIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetAutoIngredientRepository extends JpaRepository<BudgetAutoIngredient, Long> {
    List<BudgetAutoIngredient> findByBudgetId(Long budgetId);

    @Modifying
    @Query("DELETE FROM BudgetAutoIngredient bi WHERE bi.budget.id = :budgetId")
    void deleteByBudgetId(@Param("budgetId") Long budgetId);

}
