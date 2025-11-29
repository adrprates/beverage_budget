package com.example.beverage_budget.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetManualIngredientViewDto {
    private Long ingredientId;
    private String ingredientName;
    private String unitMeasure;
    private BigDecimal quantity;
    private Integer unitsNeeded;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
