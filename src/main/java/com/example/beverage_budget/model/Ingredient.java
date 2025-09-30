package com.example.beverage_budget.model;

import com.example.beverage_budget.enums.IngredientType;
<<<<<<< HEAD
import com.example.beverage_budget.enums.UnitOfMeasure;
=======
>>>>>>> 3bd4d13 (Add UnitOfMeasure table)
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(max = 150)
    private String name;

    @Column(name = "ingrendiet_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private IngredientType ingredientType;

    @Column(name = "mark", nullable = false)
    @NotBlank
    @Size(max = 100)
    private String mark;

    @Column(name = "volume", nullable = false)
    @NotNull
    BigDecimal volume;

<<<<<<< HEAD
    @Column(name = "unit_measure", nullable = false)
    @Enumerated(EnumType.STRING)
=======
    @ManyToOne
    @JoinColumn(name = "unit_measure_id", nullable = false)
>>>>>>> 3bd4d13 (Add UnitOfMeasure table)
    private UnitOfMeasure unitMeasure;
}