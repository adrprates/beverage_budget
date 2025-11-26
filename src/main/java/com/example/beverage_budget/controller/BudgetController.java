package com.example.beverage_budget.controller;

import com.example.beverage_budget.model.*;
import com.example.beverage_budget.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    @Autowired private BudgetService budgetService;
    @Autowired private DrinkService drinkService;
    @Autowired private IngredientService ingredientService;
    @Autowired private ResourceService resourceService;
    @Autowired private UnitOfMeasureService unitService;

    @GetMapping({"", "/"})
    public String list(@RequestParam(required = false) String search, Model model) {
        List<Budget> budgets = (search != null && !search.isBlank())
                ? budgetService.searchByName(search)
                : budgetService.getAll();
        model.addAttribute("list", budgets);
        model.addAttribute("search", search);
        return "budget/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("budget", new Budget());
        prepareForm(model);
        return "budget/create";
    }

    @PostMapping("/save")
    @Transactional
    public String save(
            @ModelAttribute @Valid Budget budget,
            BindingResult bindingResult,
            @RequestParam(required = false) List<Long> drinkIds,
            @RequestParam(required = false) List<BigDecimal> drinkQuantities,
            @RequestParam(required = false) List<Long> ingredientIds,
            @RequestParam(required = false) List<BigDecimal> ingredientQuantities,
            @RequestParam(required = false) List<BigDecimal> ingredientUnitPrices,
            @RequestParam(required = false) List<Long> resourceIds,
            @RequestParam(required = false) List<BigDecimal> resourceQuantities,
            @RequestParam(required = false) List<BigDecimal> resourceUnitPrices,
            @RequestParam(defaultValue = "false") boolean autoProportion,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            prepareForm(model);
            return "budget/create";
        }

        budget.getDrinks().clear();

        if (drinkIds != null && drinkQuantities != null) {
            for (int i = 0; i < drinkIds.size(); i++) {

                Drink drink = drinkService.getByIdWithIngredients(drinkIds.get(i));

                BudgetDrink bd = new BudgetDrink();
                bd.setBudget(budget);
                bd.setDrink(drink);
                bd.setQuantity(drinkQuantities.get(i));

                budget.getDrinks().add(bd);
            }
        }

        budget.getIngredients().clear();

        if (ingredientIds != null) {
            for (int i = 0; i < ingredientIds.size(); i++) {

                Ingredient ing = ingredientService.getById(ingredientIds.get(i));
                BigDecimal qty = ingredientQuantities.get(i);
                BigDecimal price = ingredientUnitPrices.get(i);

                BudgetIngredient bi = new BudgetIngredient();
                bi.setBudget(budget);
                bi.setIngredient(ing);
                bi.setQuantity(qty);
                bi.setUnitPrice(price != null ? price : BigDecimal.ZERO);
                bi.setTotalPrice(bi.getQuantity().multiply(bi.getUnitPrice()));
                bi.setUnitsNeeded(0);

                budget.getIngredients().add(bi);
            }
        }

        budget.getResources().clear();

        if (resourceIds != null) {
            for (int i = 0; i < resourceIds.size(); i++) {

                Resource r = resourceService.getById(resourceIds.get(i));
                BigDecimal qty = resourceQuantities.get(i);
                BigDecimal price = resourceUnitPrices.get(i);

                BudgetResource br = new BudgetResource();
                br.setBudget(budget);
                br.setResource(r);
                br.setQuantity(qty);
                br.setUnitPrice(price != null ? price : BigDecimal.ZERO);
                br.setTotalPrice(br.getQuantity().multiply(br.getUnitPrice()));

                budget.getResources().add(br);
            }
        }

        if (autoProportion) {
            budgetService.applyDrinkProportion(budget, budget.getPeopleCount());
        }

        if (budget.getCustomFinalPrice() != null &&
                budget.getCustomFinalPrice().compareTo(BigDecimal.ZERO) > 0) {

            budget.setFinalPrice(budget.getCustomFinalPrice());
        }

        budgetService.save(budget);

        return "redirect:/budget";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Budget budget = budgetService.getById(id);
        model.addAttribute("budget", budget);
        prepareForm(model);
        return "budget/edit";
    }

    @PostMapping("/update")
    @Transactional
    public String update(@ModelAttribute @Valid Budget budget,
                         BindingResult bindingResult,
                         @RequestParam(required = false) List<Long> drinkIds,
                         @RequestParam(required = false) List<BigDecimal> drinkQuantities,
                         @RequestParam(required = false) List<Long> ingredientIds,
                         @RequestParam(required = false) List<BigDecimal> ingredientQuantities,
                         @RequestParam(required = false) List<BigDecimal> ingredientUnitPrices,
                         @RequestParam(required = false) List<Long> resourceIds,
                         @RequestParam(required = false) List<BigDecimal> resourceQuantities,
                         @RequestParam(required = false) List<BigDecimal> resourceUnitPrices,
                         @RequestParam(defaultValue = "false") boolean autoProportion,
                         Model model) {

        if (bindingResult.hasErrors()) {
            prepareForm(model);
            return "budget/edit";
        }

        List<BudgetDrink> budgetDrinks = new ArrayList<>();
        if (drinkIds != null && drinkQuantities != null) {
            for (int i = 0; i < drinkIds.size(); i++) {
                Drink drink = drinkService.getByIdWithIngredients(drinkIds.get(i));
                BudgetDrink bd = new BudgetDrink();
                bd.setBudget(budget);
                bd.setDrink(drink);
                bd.setQuantity(drinkQuantities.get(i));
                budgetDrinks.add(bd);
            }
        }
        budget.setDrinks(budgetDrinks);

        List<BudgetIngredient> budgetIngredients = mergeIngredientsFromDrinksAndManual(
                budget, drinkIds, ingredientIds, ingredientQuantities, ingredientUnitPrices
        );
        budget.setIngredients(budgetIngredients);

        List<BudgetResource> budgetResources = new ArrayList<>();
        if (resourceIds != null && resourceQuantities != null) {
            for (int i = 0; i < resourceIds.size(); i++) {
                Resource r = resourceService.getById(resourceIds.get(i));
                BigDecimal unitPrice = (resourceUnitPrices != null && resourceUnitPrices.size() > i)
                        ? resourceUnitPrices.get(i) : BigDecimal.ZERO;
                BigDecimal qty = resourceQuantities.get(i);
                BudgetResource br = new BudgetResource();
                br.setBudget(budget);
                br.setResource(r);
                br.setQuantity(qty);
                br.setUnitPrice(unitPrice);
                br.setTotalPrice(unitPrice.multiply(qty));
                budgetResources.add(br);
            }
        }
        budget.setResources(budgetResources);

        if (autoProportion)
            budgetService.applyDrinkProportion(budget, budget.getPeopleCount());

        if (budget.getCustomFinalPrice() != null && budget.getCustomFinalPrice().compareTo(BigDecimal.ZERO) > 0) {
            budget.setFinalPrice(budget.getCustomFinalPrice());
        }

        budgetService.save(budget);
        return "redirect:/budget";
    }

    @PostMapping("/{id}/apply-proportion")
    public String applyProportion(@PathVariable Long id, @RequestParam int targetServings) {
        Budget budget = budgetService.getById(id);
        budgetService.applyDrinkProportion(budget, targetServings);
        return "redirect:/budget/edit/" + id;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        budgetService.deleteById(id);
        return "redirect:/budget";
    }

    private void prepareForm(Model model) {
        model.addAttribute("allDrinks", drinkService.getAll());
        model.addAttribute("allIngredients", ingredientService.getAll());
        model.addAttribute("allResources", resourceService.getAll());
        model.addAttribute("allUnits", unitService.getAll());
    }

    private List<BudgetIngredient> mergeIngredientsFromDrinksAndManual(
            Budget budget,
            List<Long> drinkIds,
            List<Long> ingredientIds,
            List<BigDecimal> ingredientQuantities,
            List<BigDecimal> ingredientUnitPrices
    ) {
        Map<Long, BudgetIngredient> merged = new HashMap<>();

        if (drinkIds != null) {
            for (Long drinkId : drinkIds) {
                Drink drink = drinkService.getByIdWithIngredients(drinkId);
                for (DrinkIngredient di : drink.getIngredients()) {
                    Long ingId = di.getIngredient().getId();
                    merged.compute(ingId, (id, existing) -> {
                        if (existing == null) {
                            BudgetIngredient bi = new BudgetIngredient();
                            bi.setBudget(budget);
                            bi.setIngredient(di.getIngredient());
                            bi.setQuantity(BigDecimal.valueOf(di.getQuantity()));
                            bi.setUnitPrice(BigDecimal.ZERO);
                            bi.setTotalPrice(BigDecimal.ZERO);
                            return bi;
                        } else {
                            existing.setQuantity(existing.getQuantity()
                                    .add(BigDecimal.valueOf(di.getQuantity())));
                            return existing;
                        }
                    });
                }
            }
        }

        if (ingredientIds != null && ingredientQuantities != null) {
            for (int i = 0; i < ingredientIds.size(); i++) {
                Long ingId = ingredientIds.get(i);
                BigDecimal qty = ingredientQuantities.get(i);
                BigDecimal unit = (ingredientUnitPrices != null && ingredientUnitPrices.size() > i)
                        ? ingredientUnitPrices.get(i) : BigDecimal.ZERO;

                merged.compute(ingId, (id, existing) -> {
                    if (existing == null) {
                        BudgetIngredient bi = new BudgetIngredient();
                        bi.setBudget(budget);
                        bi.setIngredient(ingredientService.getById(ingId));
                        bi.setQuantity(qty);
                        bi.setUnitPrice(unit);
                        bi.setTotalPrice(unit.multiply(qty));
                        return bi;
                    } else {
                        existing.setQuantity(existing.getQuantity().add(qty));
                        existing.setUnitPrice(unit);
                        existing.setTotalPrice(existing.getUnitPrice().multiply(existing.getQuantity()));
                        return existing;
                    }
                });
            }
        }

        return new ArrayList<>(merged.values());
    }
}