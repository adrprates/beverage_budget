package com.example.beverage_budget.service.impl;

import com.example.beverage_budget.dto.DrinkDto;
import com.example.beverage_budget.model.*;
import com.example.beverage_budget.repository.BudgetDrinkRepository;
import com.example.beverage_budget.repository.BudgetAutoIngredientRepository;
import com.example.beverage_budget.repository.BudgetRepository;
import com.example.beverage_budget.repository.BudgetResourceRepository;
import com.example.beverage_budget.service.BudgetService;
import com.example.beverage_budget.service.DrinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetDrinkRepository budgetDrinkRepository;
    private final BudgetAutoIngredientRepository budgetAutoIngredientRepository;
    private final BudgetResourceRepository budgetResourceRepository;

    @Autowired
    private DrinkService drinkService;

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
    @Transactional
    public void save(Budget budget) {

        if (budget.getDrinks() != null) {
            for (BudgetDrink bd : budget.getDrinks()) {
                bd.setBudget(budget);
            }
        }

        if (budget.getAutoIngredients() != null) {
            for (BudgetAutoIngredient ba : budget.getAutoIngredients()) {
                ba.setBudget(budget);
            }
        }

        if (budget.getManualIngredients() != null) {
            for (BudgetManualIngredient bm : budget.getManualIngredients()) {
                bm.setBudget(budget);
            }
        }

        if(budget.getResources() != null) {
            for (BudgetResource br : budget.getResources()) {
                br.setBudget(budget);
            }
        }
        
        budgetRepository.save(budget);
    }

    @Override
    public void deleteById(Long id) {
        budgetRepository.deleteById(id);
    }

    @Override
    public void calculateTotals(Budget budget) {

        BigDecimal totalAuto = BigDecimal.ZERO;
        BigDecimal totalManual = BigDecimal.ZERO;
        BigDecimal totalResources = BigDecimal.ZERO;

        if (budget.getAutoIngredients() != null) {
            totalAuto = budget.getAutoIngredients().stream()
                    .map(BudgetAutoIngredient::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (budget.getManualIngredients() != null) {
            totalManual = budget.getManualIngredients().stream()
                    .map(BudgetManualIngredient::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (budget.getResources() != null) {
            totalResources = budget.getResources().stream()
                    .map(BudgetResource::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        budget.setTotalAutoIngredientCost(totalAuto);
        budget.setTotalManualIngredientCost(totalManual);
        budget.setTotalResourceCost(totalResources);

        BigDecimal totalCost = totalAuto.add(totalManual).add(totalResources);
        budget.setTotalCost(totalCost);

        BigDecimal markup = (budget.getMarkupPercentage() != null)
                ? budget.getMarkupPercentage().divide(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        BigDecimal suggestedFinalPrice = totalCost.add(totalCost.multiply(markup));

        if (budget.getCustomFinalPrice() != null &&
                budget.getCustomFinalPrice().compareTo(BigDecimal.ZERO) > 0) {
            budget.setFinalPrice(budget.getCustomFinalPrice());
        } else {
            budget.setFinalPrice(suggestedFinalPrice);
        }
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

        if (budget.getAutoIngredients() != null) {
            for (BudgetAutoIngredient bi : budget.getAutoIngredients()) {
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

    }

    @Override
    public List<BudgetAutoIngredient> calculateIngredientsFromDrinks(List<DrinkDto> drinks) {
        if (drinks == null || drinks.isEmpty()) return Collections.emptyList();

        Map<Long, BudgetAutoIngredient> merged = new HashMap<>();

        for (DrinkDto dto : drinks) {
            Drink drink = drinkService.getByIdWithIngredients(dto.getDrinkId());
            int drinkQty = dto.getQuantity();

            for (DrinkIngredient di : drink.getIngredients()) {
                Ingredient ing = di.getIngredient();
                double qtyPerDrink = di.getQuantity().doubleValue();
                String unit = ing.getUnitMeasure().getCode();

                double qtyBase = convertToBase(qtyPerDrink, unit);
                double totalBaseQty = qtyBase * drinkQty;

                BudgetAutoIngredient bi = merged.computeIfAbsent(
                        ing.getId(),
                        id -> {
                            BudgetAutoIngredient x = new BudgetAutoIngredient();
                            x.setIngredient(ing);
                            x.setQuantity(BigDecimal.ZERO);
                            x.setUnitsNeeded(0);
                            x.setUnitPrice(BigDecimal.ZERO);
                            x.setTotalPrice(BigDecimal.ZERO);
                            return x;
                        }
                );

                bi.setQuantity(bi.getQuantity().add(BigDecimal.valueOf(totalBaseQty)));
            }
        }

        for (BudgetAutoIngredient bi : merged.values()) {
            Ingredient ing = bi.getIngredient();
            String unit = ing.getUnitMeasure().getCode();
            double totalBaseNeeded = bi.getQuantity().doubleValue();

            double packageBase = convertToBase(ing.getVolume().doubleValue(), unit);
            int unitsNeeded = (int) Math.ceil(totalBaseNeeded / packageBase);
            bi.setUnitsNeeded(unitsNeeded);

            BigDecimal displayQuantity = BigDecimal.valueOf(totalBaseNeeded);
            if ("l".equalsIgnoreCase(unit) || "kg".equalsIgnoreCase(unit)) {
                displayQuantity = BigDecimal.valueOf(totalBaseNeeded / 1000.0);
            }
            bi.setQuantity(displayQuantity);

            if (bi.getUnitPrice() != null) {
                bi.setTotalPrice(bi.getUnitPrice().multiply(BigDecimal.valueOf(unitsNeeded)));
            }
        }

        return merged.values().stream().toList();
    }

    public double convertToBase(double qty, String unit) {
        unit = unit.toLowerCase();
        return switch (unit) {
            case "l", "kg" -> qty * 1000;
            case "ml", "g" -> qty;
            default -> qty;
        };
    }


    public int calculateUnitsFromQuantity(double quantity, double volumePerUnit) {
        return (int) Math.ceil(quantity / volumePerUnit);
    }

    public double calculateQuantityFromUnits(int units, double volumePerUnit) {
        return units * volumePerUnit;
    }

    @Override
    public BudgetAutoIngredient getIngredientById(Long ingredientId, List<DrinkDto> drinks) {
        List<BudgetAutoIngredient> list = calculateIngredientsFromDrinks(drinks);
        return list.stream()
                .filter(bi -> bi.getIngredient().getId().equals(ingredientId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void applyManualIngredientProportion(Budget budget) {
        if (budget == null || budget.getManualIngredients() == null) return;

        for (BudgetManualIngredient mi : budget.getManualIngredients()) {

            Ingredient ing = mi.getIngredient();
            if (ing == null || ing.getVolume() == null || ing.getVolume().doubleValue() == 0)
                continue;

            double volumePerUnit = ing.getVolume().doubleValue();

            if (mi.getUnitsNeeded() != null && mi.getUnitsNeeded() > 0) {

                BigDecimal adjustedQuantity =
                        BigDecimal.valueOf(calculateQuantityFromUnits(mi.getUnitsNeeded(), volumePerUnit));

                mi.setQuantity(adjustedQuantity);

            } else {
                BigDecimal totalQuantity = mi.getQuantity();

                int unitsNeeded = calculateUnitsFromQuantity(
                        totalQuantity.doubleValue(),
                        volumePerUnit
                );
                mi.setUnitsNeeded(unitsNeeded);

                BigDecimal adjustedQuantity =
                        BigDecimal.valueOf(calculateQuantityFromUnits(unitsNeeded, volumePerUnit));
                mi.setQuantity(adjustedQuantity);
            }

            if (mi.getUnitPrice() != null) {
                mi.setTotalPrice(
                        mi.getUnitPrice().multiply(BigDecimal.valueOf(mi.getUnitsNeeded()))
                );
            }
        }

        calculateTotals(budget);
    }


}