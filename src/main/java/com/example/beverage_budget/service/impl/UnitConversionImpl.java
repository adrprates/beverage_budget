package com.example.beverage_budget.service.impl;

import com.example.beverage_budget.model.UnitConversion;
import com.example.beverage_budget.model.UnitOfMeasure;
import com.example.beverage_budget.repository.UnitConversionRepository;
import com.example.beverage_budget.repository.UnitOfMeasureRepository;
import com.example.beverage_budget.service.UnitConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UnitConversionImpl implements UnitConversionService {

    @Autowired
    private UnitConversionRepository conversionRepository;

    @Override
    public Optional<Double> getConversionFactor(UnitOfMeasure from, UnitOfMeasure to) {
        if (from.equals(to)) {
            return Optional.of(1.0);
        }

        Optional<UnitConversion> direct = conversionRepository.findByFromUnitAndToUnit(from, to);
        if (direct.isPresent()) {
            return Optional.of(direct.get().getFactor());
        }

        Optional<UnitConversion> inverse = conversionRepository.findByFromUnitAndToUnit(to, from);
        if (inverse.isPresent()) {
            return Optional.of(1 / inverse.get().getFactor());
        }

        return findConversionPath(from, to);
    }

    @Override
    public Optional<Double> convert(Double value, UnitOfMeasure from, UnitOfMeasure to) {
        return getConversionFactor(from, to).map(factor -> value * factor);
    }

    private Optional<Double> findConversionPath(UnitOfMeasure from, UnitOfMeasure to) {
        Map<UnitOfMeasure, Double> factors = new HashMap<>();
        Set<UnitOfMeasure> visited = new HashSet<>();
        Queue<UnitOfMeasure> queue = new LinkedList<>();

        factors.put(from, 1.0);
        queue.add(from);

        while (!queue.isEmpty()) {
            UnitOfMeasure current = queue.poll();
            double currentFactor = factors.get(current);

            if (current.equals(to)) {
                return Optional.of(currentFactor);
            }

            if (!visited.add(current)) continue;

            List<UnitConversion> outgoing = conversionRepository.findByFromUnit(current);
            for (UnitConversion c : outgoing) {
                if (!visited.contains(c.getToUnit())) {
                    factors.put(c.getToUnit(), currentFactor * c.getFactor());
                    queue.add(c.getToUnit());
                }
            }

            List<UnitConversion> incoming = conversionRepository.findByToUnit(current);
            for (UnitConversion c : incoming) {
                if (!visited.contains(c.getFromUnit())) {
                    factors.put(c.getFromUnit(), currentFactor / c.getFactor());
                    queue.add(c.getFromUnit());
                }
            }
        }

        return Optional.empty();
    }
}