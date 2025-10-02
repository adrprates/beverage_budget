package com.example.beverage_budget.repository;

import com.example.beverage_budget.enums.IngredientType;
import com.example.beverage_budget.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByNameContainingIgnoreCase(String name);
    List<Ingredient> findByIngredientType(IngredientType ingredientType);
}
