package com.example.beverage_budget.service.impl;

import com.example.beverage_budget.enums.BudgetStatus;
import com.example.beverage_budget.model.*;
import com.example.beverage_budget.repository.BudgetDrinkRepository;
import com.example.beverage_budget.repository.BudgetIngredientRepository;
import com.example.beverage_budget.repository.BudgetRepository;
import com.example.beverage_budget.repository.BudgetResourceRepository;
import com.example.beverage_budget.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetDrinkRepository budgetDrinkRepository;
    private final BudgetIngredientRepository budgetIngredientRepository;
    private final BudgetResourceRepository budgetResourceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Budget> getAll() {
        return budgetRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Budget getById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found by id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Budget> searchByName(String name) {
        return budgetRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public void save(Budget budget) {
        if (budget.getStatus() == null) {
            budget.setStatus(BudgetStatus.PENDING);
        }

        budgetRepository.save(budget);

        if (budget.getId() != null) {
            budgetDrinkRepository.deleteByBudgetId(budget.getId());
            budgetIngredientRepository.deleteByBudgetId(budget.getId());
            budgetResourceRepository.deleteByBudgetId(budget.getId());
        }

        if (budget.getDrinks() != null) {
            for (BudgetDrink bd : budget.getDrinks()) {
                bd.setBudget(budget);
                budgetDrinkRepository.save(bd);
            }
        }

        if (budget.getIngredients() != null) {
            for (BudgetIngredient bi : budget.getIngredients()) {
                bi.setBudget(budget);
                if (bi.getQuantity() != null && bi.getUnitPrice() != null) {
                    bi.setTotalPrice(bi.getQuantity().multiply(bi.getUnitPrice()));
                } else {
                    bi.setTotalPrice(BigDecimal.ZERO);
                }
                budgetIngredientRepository.save(bi);
            }
        }

        if (budget.getResources() != null) {
            for (BudgetResource br : budget.getResources()) {
                br.setBudget(budget);
                if (br.getQuantity() != null && br.getUnitPrice() != null) {
                    br.setTotalPrice(br.getQuantity().multiply(br.getUnitPrice()));
                } else {
                    br.setTotalPrice(BigDecimal.ZERO);
                }
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
    public void calculateTotals(Budget budget) {
        BigDecimal totalIngredients = BigDecimal.ZERO;
        BigDecimal totalResources = BigDecimal.ZERO;

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

        BigDecimal totalCost = totalIngredients.add(totalResources);
        budget.setTotalCost(totalCost);

        BigDecimal markup = (budget.getMarkupPercentage() != null)
                ? budget.getMarkupPercentage().divide(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        BigDecimal suggestedFinalPrice = totalCost.add(totalCost.multiply(markup));

        if (budget.getCustomFinalPrice() != null && budget.getCustomFinalPrice().compareTo(BigDecimal.ZERO) > 0) {
            budget.setFinalPrice(budget.getCustomFinalPrice());
        } else {
            budget.setFinalPrice(suggestedFinalPrice);
        }

        budgetRepository.save(budget);
    }

    @Override
    public void applyDrinkProportion(Budget budget, int targetServings) {
        if (budget == null || budget.getDrinks() == null) return;

        for (BudgetDrink bd : budget.getDrinks()) {
            Drink drink = bd.getDrink();
            if (drink == null || drink.getServings() == null || drink.getServings() == 0)
                continue;

            BigDecimal factor = BigDecimal.valueOf(targetServings)
                    .divide(BigDecimal.valueOf(drink.getServings()), 6, RoundingMode.HALF_UP);

            bd.setQuantity(BigDecimal.valueOf(targetServings));
        }

        if (budget.getIngredients() != null) {
            for (BudgetIngredient bi : budget.getIngredients()) {
                if (bi.getQuantity() != null) {
                    BigDecimal factor = BigDecimal.valueOf(targetServings);
                    BigDecimal newQuantity = bi.getQuantity().multiply(factor);
                    bi.setQuantity(newQuantity);

                    if (bi.getUnitPrice() != null) {
                        bi.setTotalPrice(newQuantity.multiply(bi.getUnitPrice()));
                    }
                }
            }
        }

        calculateTotals(budget);

        budgetRepository.save(budget);
    }
}