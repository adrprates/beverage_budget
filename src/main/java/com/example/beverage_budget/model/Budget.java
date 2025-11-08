package com.example.beverage_budget.model;

import com.example.beverage_budget.enums.BudgetStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "people_count", nullable = false)
    private Integer peopleCount;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "event_hour")
    private LocalTime eventHour;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(name = "markup_percentage", precision = 5, scale = 2)
    private BigDecimal markupPercentage = BigDecimal.ZERO;

    @Column(name = "final_price", precision = 15, scale = 2)
    private BigDecimal finalPrice = BigDecimal.ZERO;

    @Column(name = "custom_final_price", precision = 15, scale = 2)
    private BigDecimal customFinalPrice;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetDrink> drinks = new ArrayList<>();

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetIngredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetResource> resources = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BudgetStatus status = BudgetStatus.PENDING;
}