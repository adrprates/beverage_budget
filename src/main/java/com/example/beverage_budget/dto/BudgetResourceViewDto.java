package com.example.beverage_budget.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetResourceViewDto {
    private Long resourceId;
    private String resourceName;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
