package com.example.beverage_budget.repository;

import com.example.beverage_budget.enums.BudgetStatus;
import com.example.beverage_budget.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByNameContainingIgnoreCase(String name);

    List<Budget> findByStatus(BudgetStatus status);

    List<Budget> findByEventDateBetween(LocalDate start, LocalDate end);
}