package com.example.beverage_budget.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "units_of_measure")
public class UnitOfMeasure {

    public UnitOfMeasure() {
    }

    public UnitOfMeasure(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false)
    @NotBlank
    @Size(max = 30)
    private String code;

    @Column(name = "description", nullable = false)
    @NotBlank
    @Size(max = 100)
    private String description;
}