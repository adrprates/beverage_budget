package com.example.beverage_budget.service;

import com.example.beverage_budget.model.Drink;

import java.util.List;

public interface DrinkService {
    List<Drink> getAll();
    void save(Drink drink);
    Drink getById(Long id);
    void deleteById(Long id);
    List<Drink> searchByName(String name);

    void removeIngredient(Long drinkId, Long ingredientId);
    Drink getByIdWithIngredients(Long id);
}