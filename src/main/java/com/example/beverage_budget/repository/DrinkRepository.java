package com.example.beverage_budget.repository;

import com.example.beverage_budget.model.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Long> {
    List<Drink> findByNameContainingIgnoreCase(String name);
}
