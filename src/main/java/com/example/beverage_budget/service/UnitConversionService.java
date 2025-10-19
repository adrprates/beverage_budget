package com.example.beverage_budget.service;

import com.example.beverage_budget.model.UnitOfMeasure;

import java.util.Optional;

public interface UnitConversionService {
    Optional<Double> getConversionFactor(UnitOfMeasure from, UnitOfMeasure to);
    Optional<Double> convert(Double value, UnitOfMeasure from, UnitOfMeasure to);
}
