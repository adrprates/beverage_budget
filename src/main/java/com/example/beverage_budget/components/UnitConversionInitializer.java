package com.example.beverage_budget.components;

import com.example.beverage_budget.model.UnitConversion;
import com.example.beverage_budget.model.UnitOfMeasure;
import com.example.beverage_budget.repository.UnitConversionRepository;
import com.example.beverage_budget.repository.UnitOfMeasureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UnitConversionInitializer implements CommandLineRunner {
    private final UnitOfMeasureRepository unitRepository;
    private final UnitConversionRepository conversionRepository;

    public UnitConversionInitializer(UnitOfMeasureRepository unitRepository,
                                     UnitConversionRepository conversionRepository) {
        this.unitRepository = unitRepository;
        this.conversionRepository = conversionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (conversionRepository.count() == 0) {
            UnitOfMeasure ml = unitRepository.findByCode("ML").orElseThrow();
            UnitOfMeasure l = unitRepository.findByCode("L").orElseThrow();
            UnitOfMeasure g = unitRepository.findByCode("G").orElseThrow();
            UnitOfMeasure kg = unitRepository.findByCode("KG").orElseThrow();

            conversionRepository.save(new UnitConversion(ml, l, 0.001));
            conversionRepository.save(new UnitConversion(l, ml, 1000.0));
            conversionRepository.save(new UnitConversion(g, kg, 0.001));
            conversionRepository.save(new UnitConversion(kg, g, 1000.0));
        }
    }
}