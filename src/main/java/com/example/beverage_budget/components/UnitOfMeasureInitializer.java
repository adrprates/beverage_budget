package com.example.beverage_budget.components;

import com.example.beverage_budget.model.UnitOfMeasure;
import com.example.beverage_budget.repository.UnitOfMeasureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UnitOfMeasureInitializer implements CommandLineRunner {

    private final UnitOfMeasureRepository unitRepository;

    public UnitOfMeasureInitializer(UnitOfMeasureRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(unitRepository.count() == 0) {
            unitRepository.save(new UnitOfMeasure("ML", "Mililitros"));
            unitRepository.save(new UnitOfMeasure("L", "Litros"));
            unitRepository.save(new UnitOfMeasure("G", "Gramas"));
            unitRepository.save(new UnitOfMeasure("KG", "Quilos"));
            unitRepository.save(new UnitOfMeasure("UN", "Unidade"));
            unitRepository.save(new UnitOfMeasure("FAT", "Fatia"));
            unitRepository.save(new UnitOfMeasure("GOTA", "Gota / Pitada"));
            unitRepository.save(new UnitOfMeasure("COP", "Copo"));
            unitRepository.save(new UnitOfMeasure("COLS", "Colher de sopa"));
            unitRepository.save(new UnitOfMeasure("COLC", "Colher de chá"));
        }
    }
}