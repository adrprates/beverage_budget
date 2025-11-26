package com.example.beverage_budget.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ManualIngredientDto {
    private Long ingredientId;
    private Double quantity;
    private Integer units;
    private BigDecimal unitPrice;
}
