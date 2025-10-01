package com.example.beverage_budget.service.impl;

import com.example.beverage_budget.model.Drink;
import com.example.beverage_budget.repository.DrinkRepository;
import com.example.beverage_budget.service.DrinkService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

//public class DrinkImpl implements DrinkService {
//
//    @Autowired
//    private DrinkRepository drinkRepository;
//
//    @Override
//    public List<Drink> getAll() {
//        return drinkRepository.findAll();
//    }
//
//    @Override
//    public void save(Drink drink) {
//        // graças ao cascade = ALL e orphanRemoval = true,
//        // o JPA salva/atualiza/remover os vínculos automaticamente
//        drinkRepository.save(drink);
//    }
//
//    @Override
//    public Drink getById(Long id) {
//        Optional<Drink> optional = drinkRepository.findById(id);
//        if(optional.isPresent()){
//            return optional.get();
//        } else {
//            throw new RuntimeException("Drink not found by id: " + id);
//        }
//    }
//
//    @Override
//    public void deleteById(Long id) {
//        drinkRepository.deleteById(id);
//    }
//
//    @Override
//    public void removeIngredient(Long drinkId, Long ingredientId) {
//        Drink drink = getById(drinkId);
//
//        // remove apenas aquele ingrediente específico da lista
//        boolean removed = drink.getIngredients()
//                .removeIf(di -> di.getIngredient().getId().equals(ingredientId));
//
//        if (!removed) {
//            throw new RuntimeException("Ingredient " + ingredientId + " not found in Drink " + drinkId);
//        }
//
//        // salvar o drink novamente vai atualizar a tabela intermediária
//        drinkRepository.save(drink);
//    }
//}
