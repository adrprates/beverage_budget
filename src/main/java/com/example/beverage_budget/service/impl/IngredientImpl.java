package com.example.beverage_budget.service.impl;

import com.example.beverage_budget.enums.IngredientType;
import com.example.beverage_budget.model.Ingredient;
import com.example.beverage_budget.repository.IngredientRepository;
import com.example.beverage_budget.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class IngredientImpl implements IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    public List<Ingredient> getAll() {
        return ingredientRepository.findAll();
    }

    @Override
    public void save(Ingredient ingredient) {
        ingredientRepository.save(ingredient);
    }

    @Override
    public Ingredient getById(Long id) {
        Optional<Ingredient> optional = ingredientRepository.findById(id);
        Ingredient ingredient = null;
        if(optional.isPresent()){
            ingredient = optional.get();
        } else {
            throw new RuntimeException("Ingredient not found by id: " + id);
        }
        return ingredient;
    }

    @Override
    public void deleteById(Long id) {
        ingredientRepository.deleteById(id);
    }

    @Override
    public List<Ingredient> searchByName(String name) {
        return ingredientRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Ingredient> searchByType(IngredientType type) {
        if (type == null) return Collections.emptyList();
        return ingredientRepository.findByIngredientType(type);
    }
}
