package com.example.beverage_budget.service.impl;

import com.example.beverage_budget.model.Resource;
import com.example.beverage_budget.repository.ResourceRepository;
import com.example.beverage_budget.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public List<Resource> getAll() {
        return resourceRepository.findAll();
    }

    @Override
    public void save(Resource resource) {
        resourceRepository.save(resource);
    }

    @Override
    public Resource getById(Long id) {
        Optional<Resource> optional = resourceRepository.findById(id);
        Resource resource = null;
        if (optional.isPresent()) {
            resource = optional.get();
        } else {
            throw new RuntimeException("Resource not found by id: " + id);
        }
        return resource;
    }

    @Override
    public void deleteById(Long id) {
        resourceRepository.deleteById(id);
    }
}
