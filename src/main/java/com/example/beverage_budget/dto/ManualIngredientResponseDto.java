package com.example.beverage_budget.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ManualIngredientResponseDto {
    private Long ingredientId;
    private String ingredientName;
    private Double quantity;
    private Integer unitsNeeded;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String unitMeasure;
}

