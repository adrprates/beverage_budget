package com.example.beverage_budget.service;

import com.example.beverage_budget.model.UnitOfMeasure;

import java.util.List;

public interface UnitOfMeasureService {
    List<UnitOfMeasure> getAll();
    UnitOfMeasure getById(Long id);
}
