package com.example.beverage_budget.repository;

import com.example.beverage_budget.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByNameContainingIgnoreCase(String name);
}
