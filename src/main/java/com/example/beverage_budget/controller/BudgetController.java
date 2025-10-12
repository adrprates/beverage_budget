package com.example.beverage_budget.controller;

import com.example.beverage_budget.model.*;
import com.example.beverage_budget.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private DrinkService drinkService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private UnitOfMeasureService unitService;

    @GetMapping({"", "/"})
    public String list(@RequestParam(required = false) String search, Model model) {
        List<Budget> budgets;
        if (search != null && !search.isEmpty()) {
            budgets = budgetService.searchByName(search);
        } else {
            budgets = budgetService.getAll();
        }
        model.addAttribute("list", budgets);
        model.addAttribute("search", search);
        return "budget/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("budget", new Budget());
        model.addAttribute("allDrinks", drinkService.getAll());
        model.addAttribute("allIngredients", ingredientService.getAll());
        model.addAttribute("allResources", resourceService.getAll());
        model.addAttribute("allUnits", unitService.getAll());
        return "budget/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute @Valid Budget budget,
                       BindingResult bindingResult,
                       @RequestParam(value = "drinkIds", required = false) List<Long> drinkIds,
                       @RequestParam(value = "drinkQuantities", required = false) List<BigDecimal> drinkQuantities,
                       @RequestParam(value = "ingredientIds", required = false) List<Long> ingredientIds,
                       @RequestParam(value = "ingredientQuantities", required = false) List<BigDecimal> ingredientQuantities,
                       @RequestParam(value = "resourceIds", required = false) List<Long> resourceIds,
                       @RequestParam(value = "resourceQuantities", required = false) List<BigDecimal> resourceQuantities,
                       @RequestParam(value = "resourcePrices", required = false) List<BigDecimal> resourcePrices,
                       Model model) {

        if (budget.getName() == null || budget.getName().isBlank()) {
            bindingResult.rejectValue("name", "NotBlank", "O nome do orçamento é obrigatório.");
        }
        if (budget.getPeopleCount() == null || budget.getPeopleCount() <= 0) {
            bindingResult.rejectValue("peopleCount", "Positive", "Informe a quantidade de pessoas.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("allDrinks", drinkService.getAll());
            model.addAttribute("allIngredients", ingredientService.getAll());
            model.addAttribute("allResources", resourceService.getAll());
            model.addAttribute("allUnits", unitService.getAll());
            return "budget/create";
        }

        List<BudgetDrink> budgetDrinks = new ArrayList<>();
        if (drinkIds != null && drinkQuantities != null) {
            for (int i = 0; i < drinkIds.size(); i++) {
                Drink drink = drinkService.getById(drinkIds.get(i));
                BudgetDrink bd = new BudgetDrink();
                bd.setBudget(budget);
                bd.setDrink(drink);
                bd.setQuantity(drinkQuantities.get(i));
                bd.setUnitPrice(BigDecimal.ZERO);
                bd.setTotalPrice(BigDecimal.ZERO);
                budgetDrinks.add(bd);
            }
        }
        budget.setDrinks(budgetDrinks);

        List<BudgetIngredient> budgetIngredients = new ArrayList<>();
        if (ingredientIds != null && ingredientQuantities != null) {
            for (int i = 0; i < ingredientIds.size(); i++) {
                Ingredient ing = ingredientService.getById(ingredientIds.get(i));
                BudgetIngredient bi = new BudgetIngredient();
                bi.setBudget(budget);
                bi.setIngredient(ing);
                bi.setQuantity(ingredientQuantities.get(i));
                bi.setUnitPrice(BigDecimal.ZERO);
                bi.setTotalPrice(BigDecimal.ZERO);
                budgetIngredients.add(bi);
            }
        }
        budget.setIngredients(budgetIngredients);

        List<BudgetResource> budgetResources = new ArrayList<>();
        if (resourceIds != null && resourceQuantities != null) {
            for (int i = 0; i < resourceIds.size(); i++) {
                Resource resource = resourceService.getById(resourceIds.get(i));
                BudgetResource br = new BudgetResource();
                br.setBudget(budget);
                br.setResource(resource);
                br.setQuantity(resourceQuantities.get(i));
                br.setUnitPrice(resourcePrices != null && resourcePrices.size() > i ? resourcePrices.get(i) : BigDecimal.ZERO);
                br.setTotalPrice(br.getUnitPrice().multiply(br.getQuantity()));
                budgetResources.add(br);
            }
        }
        budget.setResources(budgetResources);

        budgetService.save(budget);
        return "redirect:/budget";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Budget budget = budgetService.getById(id);
        model.addAttribute("budget", budget);
        model.addAttribute("allDrinks", drinkService.getAll());
        model.addAttribute("allIngredients", ingredientService.getAll());
        model.addAttribute("allResources", resourceService.getAll());
        model.addAttribute("allUnits", unitService.getAll());
        return "budget/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute @Valid Budget budget,
                         BindingResult bindingResult,
                         @RequestParam(value = "drinkIds", required = false) List<Long> drinkIds,
                         @RequestParam(value = "drinkQuantities", required = false) List<BigDecimal> drinkQuantities,
                         @RequestParam(value = "ingredientIds", required = false) List<Long> ingredientIds,
                         @RequestParam(value = "ingredientQuantities", required = false) List<BigDecimal> ingredientQuantities,
                         @RequestParam(value = "resourceIds", required = false) List<Long> resourceIds,
                         @RequestParam(value = "resourceQuantities", required = false) List<BigDecimal> resourceQuantities,
                         @RequestParam(value = "resourcePrices", required = false) List<BigDecimal> resourcePrices,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allDrinks", drinkService.getAll());
            model.addAttribute("allIngredients", ingredientService.getAll());
            model.addAttribute("allResources", resourceService.getAll());
            model.addAttribute("allUnits", unitService.getAll());
            return "budget/edit";
        }

        List<BudgetDrink> budgetDrinks = new ArrayList<>();
        if (drinkIds != null && drinkQuantities != null) {
            for (int i = 0; i < drinkIds.size(); i++) {
                Drink drink = drinkService.getById(drinkIds.get(i));
                BudgetDrink bd = new BudgetDrink();
                bd.setBudget(budget);
                bd.setDrink(drink);
                bd.setQuantity(drinkQuantities.get(i));
                bd.setUnitPrice(BigDecimal.ZERO);
                bd.setTotalPrice(BigDecimal.ZERO);
                budgetDrinks.add(bd);
            }
        }
        budget.setDrinks(budgetDrinks);

        List<BudgetIngredient> budgetIngredients = new ArrayList<>();
        if (ingredientIds != null && ingredientQuantities != null) {
            for (int i = 0; i < ingredientIds.size(); i++) {
                Ingredient ing = ingredientService.getById(ingredientIds.get(i));
                BudgetIngredient bi = new BudgetIngredient();
                bi.setBudget(budget);
                bi.setIngredient(ing);
                bi.setQuantity(ingredientQuantities.get(i));
                bi.setUnitPrice(BigDecimal.ZERO);
                bi.setTotalPrice(BigDecimal.ZERO);
                budgetIngredients.add(bi);
            }
        }
        budget.setIngredients(budgetIngredients);

        List<BudgetResource> budgetResources = new ArrayList<>();
        if (resourceIds != null && resourceQuantities != null) {
            for (int i = 0; i < resourceIds.size(); i++) {
                Resource resource = resourceService.getById(resourceIds.get(i));
                BudgetResource br = new BudgetResource();
                br.setBudget(budget);
                br.setResource(resource);
                br.setQuantity(resourceQuantities.get(i));
                br.setUnitPrice(resourcePrices != null && resourcePrices.size() > i ? resourcePrices.get(i) : BigDecimal.ZERO);
                br.setTotalPrice(br.getUnitPrice().multiply(br.getQuantity()));
                budgetResources.add(br);
            }
        }
        budget.setResources(budgetResources);

        budgetService.save(budget);
        return "redirect:/budget";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        budgetService.deleteById(id);
        return "redirect:/budget";
    }

    @PostMapping("/{id}/apply-proportion")
    public String applyProportion(@PathVariable Long id,
                                  @RequestParam("targetServings") int targetServings) {
        Budget budget = budgetService.getById(id);
        budgetService.applyDrinkProportion(budget, targetServings);
        return "redirect:/budget/edit/" + id;
    }
}