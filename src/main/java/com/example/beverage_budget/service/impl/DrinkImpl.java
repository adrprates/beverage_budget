package com.example.beverage_budget.service.impl;

import com.example.beverage_budget.model.Drink;
import com.example.beverage_budget.model.DrinkIngredient;
import com.example.beverage_budget.repository.DrinkRepository;
import com.example.beverage_budget.service.DrinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class DrinkImpl implements DrinkService {

    @Autowired
    private DrinkRepository drinkRepository;

    @Override
    public List<Drink> getAll() {
        return drinkRepository.findAll();
    }

    @Override
    public void save(Drink drink) {
        if (drink.getIngredients() != null) {
            for (DrinkIngredient di : drink.getIngredients()) {
                di.setDrink(drink);
            }
        }
        drinkRepository.save(drink);
    }

    @Override
    public Drink getById(Long id) {
        Optional<Drink> optional = drinkRepository.findById(id);
        Drink drink = null;
        if (optional.isPresent()) {
            drink = optional.get();
        } else {
            throw new RuntimeException("Drink not found by id: " + id);
        }
        return drink;
    }

    @Override
    public void deleteById(Long id) {
        drinkRepository.deleteById(id);
    }

    @Override
    public void removeIngredient(Long drinkId, Long ingredientId) {
        Drink drink = getById(drinkId);
        if (drink.getIngredients() != null) {
            Iterator<DrinkIngredient> it = drink.getIngredients().iterator();
            while (it.hasNext()) {
                DrinkIngredient di = it.next();
                if (di.getIngredient().getId().equals(ingredientId)) {
                    it.remove();
                }
            }
            save(drink);
        }
    }

    @Override
    public List<Drink> searchByName(String name) {
        return drinkRepository.findByNameContainingIgnoreCase(name);
    }
}