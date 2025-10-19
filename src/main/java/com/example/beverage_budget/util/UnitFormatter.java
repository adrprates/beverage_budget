package com.example.beverage_budget.util;

import com.example.beverage_budget.model.UnitOfMeasure;

public class UnitFormatter {

    public static String formatQuantity(Double quantity, UnitOfMeasure unit) {
        if (unit == null || quantity == null) {
            return "";
        }

        if (unit.getCode().equalsIgnoreCase("KG")) {
            if (quantity < 1) {
                double grams = quantity * 1000;
                return String.format("%.1f %s", grams, formatUnitLabel("G", "Gramas"));
            }
        } else if (unit.getCode().equalsIgnoreCase("G")) {
            if (quantity >= 1000) {
                double kg = quantity / 1000;
                return String.format("%.1f %s", kg, formatUnitLabel("KG", "Quilos"));
            }
        }

        if (unit.getCode().equalsIgnoreCase("L")) {
            if (quantity < 1) {
                double ml = quantity * 1000;
                return String.format("%.1f %s", ml, formatUnitLabel("ML", "Mililitros"));
            }
        } else if (unit.getCode().equalsIgnoreCase("ML")) {
            if (quantity >= 1000) {
                double l = quantity / 1000;
                return String.format("%.1f %s", l, formatUnitLabel("L", "Litros"));
            }
        }

        return String.format("%.3f %s", quantity, formatUnitLabel(unit.getCode(), unit.getDescription()));
    }

    public static String formatUnitLabel(String code, String description) {
        return String.format("(%s) %s", code.toLowerCase(), description.toLowerCase());
    }
}