package com.example.beverage_budget.controller;

import com.example.beverage_budget.dto.*;
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
            @RequestParam(required = false) List<Long> autoIngredientIds,
            @RequestParam(required = false) List<BigDecimal> autoIngredientQuantities,
            @RequestParam(required = false) List<BigDecimal> autoIngredientUnitPrices,
            @RequestParam(required = false) List<Integer> autoIngredientUnits,


            @RequestParam(required = false) List<Long> manualIngredientIds,
            @RequestParam(required = false) List<BigDecimal> manualIngredientQuantities,
            @RequestParam(required = false) List<BigDecimal> manualIngredientUnitPrices,
            @RequestParam(required = false) List<Integer> manualIngredientUnits,
            @RequestParam(required = false)  List<Long> resourceIds,
            @RequestParam(required = false)  List<BigDecimal> resourceQuantities,
            @RequestParam(required = false)  List<BigDecimal> resourceUnitPrices,
            @RequestParam(required = false)  List<BigDecimal> resourceTotals,
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

        budget.getAutoIngredients().clear();
        if (autoIngredientIds != null) {
            for (int i = 0; i < autoIngredientIds.size(); i++) {
                Ingredient ing = ingredientService.getById(autoIngredientIds.get(i));
                BigDecimal qty = autoIngredientQuantities.get(i);
                BigDecimal price = autoIngredientUnitPrices.get(i);
                Integer unitsNeeded = autoIngredientUnits.get(i);

                BudgetAutoIngredient bi = new BudgetAutoIngredient();
                bi.setBudget(budget);
                bi.setIngredient(ing);
                bi.setQuantity(qty);
                bi.setUnitPrice(price != null ? price : BigDecimal.ZERO);
                bi.setTotalPrice(bi.getUnitPrice().multiply(BigDecimal.valueOf(unitsNeeded)));
                bi.setUnitsNeeded(unitsNeeded);

                budget.getAutoIngredients().add(bi);
            }
        }

        budget.getManualIngredients().clear();
        if (manualIngredientIds != null) {
            for (int i = 0; i < manualIngredientIds.size(); i++) {

                Ingredient ing = ingredientService.getById(manualIngredientIds.get(i));

                BigDecimal qty = manualIngredientQuantities.get(i);
                BigDecimal price = manualIngredientUnitPrices.get(i) != null
                        ? manualIngredientUnitPrices.get(i)
                        : BigDecimal.ZERO;

                Integer unitsNeeded = null;
                if (manualIngredientUnits != null && manualIngredientUnits.size() > i) {
                    unitsNeeded = manualIngredientUnits.get(i);
                }

                BigDecimal totalPrice = price.multiply(
                        BigDecimal.valueOf(unitsNeeded != null ? unitsNeeded : 0)
                );

                BudgetManualIngredient bm = new BudgetManualIngredient();
                bm.setBudget(budget);
                bm.setIngredient(ing);
                bm.setQuantity(qty);
                bm.setUnitsNeeded(unitsNeeded != null ? unitsNeeded : 0);
                bm.setUnitPrice(price);
                bm.setTotalPrice(totalPrice);

                budget.getManualIngredients().add(bm);
            }
        }


        budget.getResources().clear();

        if (resourceIds != null) {
            for (int i = 0; i < resourceIds.size(); i++) {

                Resource r = resourceService.getById(resourceIds.get(i));
                BigDecimal qty = resourceQuantities.get(i);
                BigDecimal price = resourceUnitPrices.get(i);
                BigDecimal total = resourceTotals.get(i);

                BudgetResource br = new BudgetResource();
                br.setBudget(budget);
                br.setResource(r);
                br.setQuantity(qty != null ? qty : BigDecimal.ZERO);
                br.setUnitPrice(price != null ? price : BigDecimal.ZERO);
                br.setTotalPrice(total != null ? total : BigDecimal.ZERO);

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

        budgetService.calculateTotals(budget);

        budgetService.save(budget);

        return "redirect:/budget";
    }

    @GetMapping("/edit/{id}")
    public String editBudget(@PathVariable Long id, Model model) {
        Budget budget = budgetService.getById(id);
        model.addAttribute("budget", budget);

        model.addAttribute("allDrinks", drinkService.getAll());
        model.addAttribute("allIngredients", ingredientService.getAll());
        model.addAttribute("allResources", resourceService.getAll());

        List<BudgetDrinkViewDto> selectedDrinks = budget.getDrinks().stream()
                .map(d -> {
                    BudgetDrinkViewDto dto = new BudgetDrinkViewDto();
                    dto.setDrinkId(d.getDrink().getId());
                    dto.setDrinkName(d.getDrink().getName());
                    dto.setQuantity(d.getQuantity());
                    return dto;
                }).toList();
        model.addAttribute("selectedDrinks", selectedDrinks);

        List<BudgetAutoIngredientViewDto> autoIngredients = budget.getAutoIngredients().stream()
                .map(ai -> {
                    BudgetAutoIngredientViewDto dto = new BudgetAutoIngredientViewDto();
                    dto.setIngredientId(ai.getIngredient().getId());
                    dto.setIngredientName(ai.getIngredient().getName());
                    dto.setUnitMeasure(ai.getIngredient().getUnitMeasure().getCode());
                    dto.setQuantity(ai.getQuantity());
                    dto.setUnitsNeeded(ai.getUnitsNeeded());
                    dto.setUnitPrice(ai.getUnitPrice());
                    dto.setTotalPrice(ai.getTotalPrice());
                    return dto;
                }).toList();
        model.addAttribute("autoIngredients", autoIngredients);

        List<BudgetManualIngredientViewDto> manualIngredients = budget.getManualIngredients().stream()
                .map(mi -> {
                    BudgetManualIngredientViewDto dto = new BudgetManualIngredientViewDto();
                    dto.setIngredientId(mi.getIngredient().getId());
                    dto.setIngredientName(mi.getIngredient().getName());
                    dto.setUnitMeasure(mi.getIngredient().getUnitMeasure().getCode());
                    dto.setQuantity(mi.getQuantity());
                    dto.setUnitsNeeded(mi.getUnitsNeeded());
                    dto.setUnitPrice(mi.getUnitPrice());
                    dto.setTotalPrice(mi.getTotalPrice());
                    return dto;
                }).toList();
        model.addAttribute("manualIngredients", manualIngredients);

        List<BudgetResourceViewDto> resources = budget.getResources().stream()
                .map(r -> {
                    BudgetResourceViewDto dto = new BudgetResourceViewDto();
                    dto.setResourceId(r.getResource().getId());
                    dto.setResourceName(r.getResource().getName());
                    dto.setQuantity(r.getQuantity());
                    dto.setUnitPrice(r.getUnitPrice());
                    dto.setTotalPrice(r.getTotalPrice());
                    return dto;
                }).toList();
        model.addAttribute("resources", resources);

        return "budget/edit";
    }

    @GetMapping("/detail/{id}")
    public String detailBudget(@PathVariable Long id, Model model) {
        Budget budget = budgetService.getById(id);
        if (budget == null) {
            return "redirect:/budget";
        }

        model.addAttribute("budget", budget);
        model.addAttribute("allDrinks", drinkService.getAll());
        model.addAttribute("allIngredients", ingredientService.getAll());
        model.addAttribute("allResources", resourceService.getAll());

        List<BudgetDrinkViewDto> selectedDrinks = budget.getDrinks().stream()
                .map(d -> {
                    BudgetDrinkViewDto dto = new BudgetDrinkViewDto();
                    dto.setDrinkId(d.getDrink().getId());
                    dto.setDrinkName(d.getDrink().getName());
                    dto.setQuantity(d.getQuantity());
                    return dto;
                }).toList();
        model.addAttribute("selectedDrinks", selectedDrinks);

        List<BudgetAutoIngredientViewDto> autoIngredients = budget.getAutoIngredients().stream()
                .map(ai -> {
                    BudgetAutoIngredientViewDto dto = new BudgetAutoIngredientViewDto();
                    dto.setIngredientId(ai.getIngredient().getId());
                    dto.setIngredientName(ai.getIngredient().getName());
                    dto.setUnitMeasure(ai.getIngredient().getUnitMeasure().getCode());
                    dto.setQuantity(ai.getQuantity());
                    dto.setUnitsNeeded(ai.getUnitsNeeded());
                    dto.setUnitPrice(ai.getUnitPrice());
                    dto.setTotalPrice(ai.getTotalPrice());
                    return dto;
                }).toList();
        model.addAttribute("autoIngredients", autoIngredients);

        List<BudgetManualIngredientViewDto> manualIngredients = budget.getManualIngredients().stream()
                .map(mi -> {
                    BudgetManualIngredientViewDto dto = new BudgetManualIngredientViewDto();
                    dto.setIngredientId(mi.getIngredient().getId());
                    dto.setIngredientName(mi.getIngredient().getName());
                    dto.setUnitMeasure(mi.getIngredient().getUnitMeasure().getCode());
                    dto.setQuantity(mi.getQuantity());
                    dto.setUnitsNeeded(mi.getUnitsNeeded());
                    dto.setUnitPrice(mi.getUnitPrice());
                    dto.setTotalPrice(mi.getTotalPrice());
                    return dto;
                }).toList();
        model.addAttribute("manualIngredients", manualIngredients);

        List<BudgetResourceViewDto> resources = budget.getResources().stream()
                .map(r -> {
                    BudgetResourceViewDto dto = new BudgetResourceViewDto();
                    dto.setResourceId(r.getResource().getId());
                    dto.setResourceName(r.getResource().getName());
                    dto.setQuantity(r.getQuantity());
                    dto.setUnitPrice(r.getUnitPrice());
                    dto.setTotalPrice(r.getTotalPrice());
                    return dto;
                }).toList();
        model.addAttribute("resources", resources);

        BigDecimal autoIngredientsSubtotal = autoIngredients.stream()
                .map(BudgetAutoIngredientViewDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("autoIngredientsSubtotal", autoIngredientsSubtotal);

        BigDecimal manualIngredientsSubtotal = manualIngredients.stream()
                .map(BudgetManualIngredientViewDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("manualIngredientsSubtotal", manualIngredientsSubtotal);

        BigDecimal resourceSubtotal = resources.stream()
                .map(BudgetResourceViewDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("resourceSubtotal", resourceSubtotal);

        BigDecimal totalCost = autoIngredientsSubtotal
                .add(manualIngredientsSubtotal)
                .add(resourceSubtotal);
        model.addAttribute("totalCost", totalCost);

        BigDecimal finalPrice = budget.getCustomFinalPrice() != null
                ? budget.getCustomFinalPrice()
                : totalCost.multiply(BigDecimal.ONE.add(budget.getMarkupPercentage().divide(BigDecimal.valueOf(100))));
        model.addAttribute("finalPrice", finalPrice);

        return "budget/detail";
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
    
    @PostMapping("/manual-ingredient/calculate")
    @ResponseBody
    public ManualIngredientResponseDto calculateManualIngredient(@RequestBody ManualIngredientDto dto) {
        Ingredient ing = ingredientService.getById(dto.getIngredientId());
        double volumePorUnidade = ing.getVolume().doubleValue();

        double quantity = 0;
        int unitsNeeded = 0;

        if (dto.getQuantity() != null && dto.getQuantity() > 0) {
            quantity = dto.getQuantity();
            unitsNeeded = budgetService.calculateUnitsFromQuantity(quantity, volumePorUnidade);
            quantity = budgetService.calculateQuantityFromUnits(unitsNeeded, volumePorUnidade);
        }
        else if (dto.getUnits() != null && dto.getUnits() > 0) {
            unitsNeeded = dto.getUnits();
            quantity = budgetService.calculateQuantityFromUnits(unitsNeeded, volumePorUnidade);
        }

        BigDecimal totalPrice = dto.getUnitPrice() != null
                ? dto.getUnitPrice().multiply(BigDecimal.valueOf(unitsNeeded))
                : BigDecimal.ZERO;

        ManualIngredientResponseDto response = new ManualIngredientResponseDto();
        response.setIngredientId(dto.getIngredientId());
        response.setIngredientName(ing.getName());
        response.setQuantity(quantity);
        response.setUnitsNeeded(unitsNeeded);
        response.setUnitPrice(dto.getUnitPrice() != null ? dto.getUnitPrice() : BigDecimal.ZERO);
        response.setTotalPrice(totalPrice);
        response.setUnitMeasure(ing.getUnitMeasure().getCode());

        return response;
    }
}