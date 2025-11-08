package com.example.beverage_budget.service;

import com.example.beverage_budget.model.Budget;

import java.util.List;

public interface BudgetService {
    List<Budget> getAll();
    Budget getById(Long id);
    void save(Budget budget);
    void deleteById(Long id);
    List<Budget> searchByName(String name);

    void calculateTotals(Budget budget);
    void applyDrinkProportion(Budget budget, int targetServings);
}