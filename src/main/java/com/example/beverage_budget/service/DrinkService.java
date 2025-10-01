package com.example.beverage_budget.service;

import com.example.beverage_budget.model.Drink;

import java.util.List;

public interface DrinkService {
    List<Drink> getAll();
    void save(Drink drink);
    Drink getById(Long id);
    void deleteById(Long id);

    void removeIngredient(Long drinkId, Long ingredientId);
}