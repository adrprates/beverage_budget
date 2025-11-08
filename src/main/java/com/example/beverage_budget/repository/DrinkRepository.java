package com.example.beverage_budget.repository;

import com.example.beverage_budget.model.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Long> {
    List<Drink> findByNameContainingIgnoreCase(String name);

    @Query("SELECT d FROM Drink d LEFT JOIN FETCH d.ingredients di LEFT JOIN FETCH di.ingredient WHERE d.id = :id")
    Optional<Drink> findByIdWithIngredients(@Param("id") Long id);
}
