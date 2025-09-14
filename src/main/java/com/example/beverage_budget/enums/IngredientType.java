package com.example.beverage_budget.enums;

public enum IngredientType {
    BEVERAGE_ALCOHOLIC("Bebida Alcoólica"),
    BEVERAGE_NON_ALCOHOLIC("Bebida Não Alcoólica"),
    FRUIT("Fruta"),
    SPICE("Especiaria"),
    HERB("Erva"),
    SWEETENER("Adoçante"),
    CREAM("Creme"),
    GARNISH("Guarnição"),
    ICE("Gelo");

    private final String descricao;

    IngredientType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}