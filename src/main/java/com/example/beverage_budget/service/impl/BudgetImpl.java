package com.example.beverage_budget.service.impl;

import com.example.beverage_budget.model.*;
import com.example.beverage_budget.repository.BudgetDrinkRepository;
import com.example.beverage_budget.repository.BudgetIngredientRepository;
import com.example.beverage_budget.repository.BudgetRepository;
import com.example.beverage_budget.repository.BudgetResourceRepository;
import com.example.beverage_budget.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetImpl implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private BudgetDrinkRepository budgetDrinkRepository;

    @Autowired
    private BudgetIngredientRepository budgetIngredientRepository;

    @Autowired
    private BudgetResourceRepository budgetResourceRepository;

    @Override
    public List<Budget> getAll() {
        return budgetRepository.findAll();
    }

    @Override
    public Budget getById(Long id) {
        Optional<Budget> optional = budgetRepository.findById(id);
        return optional.orElseThrow(() -> new RuntimeException("Budget not found by id: " + id));
    }

    @Override
    public void save(Budget budget) {
        budgetRepository.save(budget);

        if (budget.getId() != null) {
            budgetDrinkRepository.deleteByBudgetId(budget.getId());
            budgetIngredientRepository.deleteByBudgetId(budget.getId());
            budgetResourceRepository.deleteByBudgetId(budget.getId());
        }

        if (budget.getDrinks() != null) {
            for (BudgetDrink bd : budget.getDrinks()) {
                bd.setBudget(budget);
                bd.setTotalPrice(bd.getUnitPrice().multiply(bd.getQuantity()));
                budgetDrinkRepository.save(bd);
            }
        }

        if (budget.getIngredients() != null) {
            for (BudgetIngredient bi : budget.getIngredients()) {
                bi.setBudget(budget);
                bi.setTotalPrice(bi.getUnitPrice().multiply(bi.getQuantity()));
                budgetIngredientRepository.save(bi);
            }
        }

        if (budget.getResources() != null) {
            for (BudgetResource br : budget.getResources()) {
                br.setBudget(budget);
                br.setTotalPrice(br.getUnitPrice().multiply(br.getQuantity()));
                budgetResourceRepository.save(br);
            }
        }

        calculateTotals(budget);
    }

    @Override
    public void deleteById(Long id) {
        budgetRepository.deleteById(id);
    }

    @Override
    public List<Budget> searchByName(String name) {
        return budgetRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public void calculateTotals(Budget budget) {
        BigDecimal totalDrinks = BigDecimal.ZERO;
        BigDecimal totalIngredients = BigDecimal.ZERO;
        BigDecimal totalResources = BigDecimal.ZERO;

        if (budget.getDrinks() != null) {
            totalDrinks = budget.getDrinks().stream()
                    .map(BudgetDrink::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (budget.getIngredients() != null) {
            totalIngredients = budget.getIngredients().stream()
                    .map(BudgetIngredient::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (budget.getResources() != null) {
            totalResources = budget.getResources().stream()
                    .map(BudgetResource::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        budget.setTotalValue(totalDrinks.add(totalIngredients).add(totalResources));
        budgetRepository.save(budget);
    }

    @Override
    public void applyDrinkProportion(Budget budget, int targetServings) {
        if (budget == null || budget.getDrinks() == null) return;

        for (BudgetDrink bd : budget.getDrinks()) {
            Drink drink = bd.getDrink();

            if (drink == null || drink.getServings() == null || drink.getServings() == 0) continue;

            BigDecimal factor = BigDecimal.valueOf((double) targetServings / drink.getServings());

            bd.setQuantity(bd.getQuantity().multiply(factor));
            if (bd.getUnitPrice() != null) {
                bd.setTotalPrice(bd.getUnitPrice().multiply(bd.getQuantity()));
            }

            if (budget.getIngredients() != null) {
                for (BudgetIngredient bi : budget.getIngredients()) {
                    if (bi.getIngredient() != null && drink.getIngredients() != null) {
                        bi.setQuantity(bi.getQuantity().multiply(factor));
                        if (bi.getUnitPrice() != null) {
                            bi.setTotalPrice(bi.getUnitPrice().multiply(bi.getQuantity()));
                        }
                    }
                }
            }
        }

        calculateTotals(budget);
    }
}