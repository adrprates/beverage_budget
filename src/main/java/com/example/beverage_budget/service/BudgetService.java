package com.example.beverage_budget.service;

import com.example.beverage_budget.dto.DrinkDto;
import com.example.beverage_budget.model.Budget;
import com.example.beverage_budget.model.BudgetAutoIngredient;

import java.util.List;

public interface BudgetService {
    List<Budget> getAll();
    Budget getById(Long id);
    void save(Budget budget);
    void deleteById(Long id);
    List<Budget> searchByName(String name);

    void calculateTotals(Budget budget);
    void applyDrinkProportion(Budget budget, int targetServings);
    void applyManualIngredientProportion(Budget budget);
    List<BudgetAutoIngredient> calculateIngredientsFromDrinks(List<DrinkDto> drinks);
    BudgetAutoIngredient getIngredientById(Long ingredientId, List<DrinkDto> drinks);
    double convertToBase(double qty, String unit);
    int calculateUnitsFromQuantity(double quantity, double volumePerUnit);
    double calculateQuantityFromUnits(int units, double volumePerUnit);
}