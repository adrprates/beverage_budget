package com.example.beverage_budget.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetDrinkViewDto {
    private Long drinkId;
    private String drinkName;
    private BigDecimal quantity;
}