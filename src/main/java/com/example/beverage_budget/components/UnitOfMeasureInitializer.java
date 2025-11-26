package com.example.beverage_budget.components;

import com.example.beverage_budget.model.UnitOfMeasure;
import com.example.beverage_budget.repository.UnitOfMeasureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UnitOfMeasureInitializer implements CommandLineRunner {

    private final UnitOfMeasureRepository unitRepository;

    public UnitOfMeasureInitializer(UnitOfMeasureRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public void run(String... args) {
        List<UnitOfMeasure> unitsToEnsure = List.of(
                new UnitOfMeasure("ML", "Mililitros"),
                new UnitOfMeasure("L", "Litros"),
                new UnitOfMeasure("G", "Gramas"),
                new UnitOfMeasure("KG", "Quilos"),
                new UnitOfMeasure("UN", "Unidade"),
                new UnitOfMeasure("FAT", "Fatia"),
                new UnitOfMeasure("GOTA", "Gota / Pitada"),
                new UnitOfMeasure("COP", "Copo"),
                new UnitOfMeasure("COLS", "Colher de sopa"),
                new UnitOfMeasure("COLC", "Colher de chá")
        );

        for (UnitOfMeasure u : unitsToEnsure) {
            unitRepository.findByCode(u.getCode())
                    .orElseGet(() -> unitRepository.save(u));
        }
    }
}