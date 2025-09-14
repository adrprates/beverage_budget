package com.example.beverage_budget.service;

import com.example.beverage_budget.model.Ingredient;

import java.util.List;

public interface IngredientService {
    List<Ingredient> getAll();
    void save(Ingredient ingredient);
    Ingredient getById(Long id);
    void deleteById(Long id);
}
