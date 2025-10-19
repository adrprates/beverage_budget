package com.example.beverage_budget.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "unit_conversions")
public class UnitConversion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_unit_id", nullable = false)
    private UnitOfMeasure fromUnit;

    @ManyToOne
    @JoinColumn(name = "to_unit_id", nullable = false)
    private UnitOfMeasure toUnit;

    private Double factor;

    public UnitConversion(UnitOfMeasure fromUnit, UnitOfMeasure toUnit, Double factor) {
        this.fromUnit = fromUnit;
        this.toUnit = toUnit;
        this.factor = factor;
    }

    public UnitConversion() {

    }
}