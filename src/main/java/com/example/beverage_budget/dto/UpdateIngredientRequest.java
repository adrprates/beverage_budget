package com.example.beverage_budget.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateIngredientRequest {
    private Long ingredientId;
    private Integer units;
    private Double quantity;
    private List<DrinkDto> drinks;
}


