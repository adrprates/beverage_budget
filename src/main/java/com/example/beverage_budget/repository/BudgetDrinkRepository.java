package com.example.beverage_budget.repository;

import com.example.beverage_budget.model.BudgetDrink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetDrinkRepository extends JpaRepository<BudgetDrink, Long> {
    @Modifying
    @Query("DELETE FROM BudgetDrink bd WHERE bd.budget.id = :budgetId")
    void deleteByBudgetId(@Param("budgetId") Long budgetId);

}
