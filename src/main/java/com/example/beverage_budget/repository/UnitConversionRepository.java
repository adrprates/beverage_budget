package com.example.beverage_budget.repository;


import com.example.beverage_budget.model.UnitConversion;
import com.example.beverage_budget.model.UnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitConversionRepository extends JpaRepository<UnitConversion, Long> {
    Optional<UnitConversion> findByFromUnitAndToUnit(UnitOfMeasure from, UnitOfMeasure to);
    List<UnitConversion> findByFromUnit(UnitOfMeasure fromUnit);
    List<UnitConversion> findByToUnit(UnitOfMeasure toUnit);
}
