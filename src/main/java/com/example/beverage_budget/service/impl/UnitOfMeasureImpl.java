package com.example.beverage_budget.service.impl;

import com.example.beverage_budget.model.UnitOfMeasure;
import com.example.beverage_budget.repository.UnitOfMeasureRepository;
import com.example.beverage_budget.service.UnitOfMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitOfMeasureImpl implements UnitOfMeasureService {

    @Autowired
    private UnitOfMeasureRepository unitOfMeasureRepository;

    @Override
    public List<UnitOfMeasure> getAll() {
        return unitOfMeasureRepository.findAll();
    }

    @Override
    public UnitOfMeasure getById(Long id) {
        return unitOfMeasureRepository.findById(id).orElse(null);
    }
}
