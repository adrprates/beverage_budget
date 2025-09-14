package com.example.beverage_budget.service;

import com.example.beverage_budget.model.Resource;

import java.util.List;

public interface ResourceService {
    List<Resource> getAll();
    void save(Resource resource);
    Resource getById(Long id);
    void deleteById(Long id);
}
