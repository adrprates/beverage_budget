package com.example.beverage_budget.repository;

import com.example.beverage_budget.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByNameContainingIgnoreCase(String name);
}
