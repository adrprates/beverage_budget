package com.example.beverage_budget.enums;

public enum UnitOfMeasure {
    ML("Mililitros"),
    L("Litros"),
    G("Gramas"),
    KG("Quilos"),
    UNIT("Unidade"),
    SLICE("Fatia"),
    DASH("Gota / Pitada"),
    CUP("Copo"),
    TBSP("Colher de sopa"),
    TSP("Colher de chá");

    private final String description;

    UnitOfMeasure(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}