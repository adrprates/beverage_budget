package com.example.beverage_budget.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "drink_ingredients")
public class DrinkIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "drink_id")
    private Drink drink;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantity;
}