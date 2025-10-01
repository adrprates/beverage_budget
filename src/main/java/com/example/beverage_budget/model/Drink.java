package com.example.beverage_budget.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "drinks")
public class Drink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name" ,nullable = false)
    @NotBlank
    @Size(max = 150)
    private String name;

    @Column(name = "quantity", nullable = false)
    @NotNull
    private BigDecimal quantity;

    @Column(name = "servings", nullable = false)
    @NotNull
    private Integer servings;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "drink", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DrinkIngredient> ingredients = new ArrayList<>();

}