package com.example.beverage_budget.repository;

import com.example.beverage_budget.model.BudgetManualIngredient;
import com.example.beverage_budget.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BudgetManualIngredientRepository extends JpaRepository<BudgetManualIngredient, Long> {

    List<BudgetManualIngredient> findByBudgetId(Long budgetId);

    @Modifying
    @Query("DELETE FROM BudgetManualIngredient bi WHERE bi.budget.id = :budgetId")
    void deleteByBudgetId(@Param("budgetId") Long budgetId);

    boolean existsByIngredient(Ingredient ingredient);
}