package com.example.beverage_budget.controller;

import com.example.beverage_budget.model.Drink;
import com.example.beverage_budget.model.DrinkIngredient;
import com.example.beverage_budget.model.Ingredient;
import com.example.beverage_budget.model.UnitOfMeasure;
import com.example.beverage_budget.service.DrinkService;
import com.example.beverage_budget.service.IngredientService;
import com.example.beverage_budget.service.UnitOfMeasureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/drink")
public class DrinkController {

    @Autowired
    private DrinkService drinkService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private UnitOfMeasureService unitService;

    @GetMapping({"", "/"})
    public String list(Model model) {
        model.addAttribute("list", drinkService.getAll());
        return "drink/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("drink", new Drink());
        model.addAttribute("allIngredients", ingredientService.getAll());
        model.addAttribute("allUnits", unitService.getAll());
        return "drink/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute @Valid Drink drink,
                       BindingResult bindingResult,
                       @RequestParam(value = "ingredientIds", required = false) List<Long> ingredientIds,
                       @RequestParam(value = "quantities", required = false) List<Double> quantities,
                       Model model) {

        if (drink.getUnitMeasure() != null && drink.getUnitMeasure().getId() != null) {
            UnitOfMeasure um = unitService.getById(drink.getUnitMeasure().getId());
            drink.setUnitMeasure(um);
        } else {
            bindingResult.rejectValue("unitMeasure", "NotNull", "Unidade de medida é obrigatória");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("allIngredients", ingredientService.getAll());
            model.addAttribute("allUnits", unitService.getAll());
            return "drink/create";
        }

        if (ingredientIds != null && quantities != null) {
            List<DrinkIngredient> drinkIngredients = new ArrayList<>();
            for (int i = 0; i < ingredientIds.size(); i++) {
                Ingredient ing = ingredientService.getById(ingredientIds.get(i));
                Double qty = quantities.get(i);

                DrinkIngredient di = new DrinkIngredient();
                di.setDrink(drink);
                di.setIngredient(ing);
                di.setQuantity(qty);

                drinkIngredients.add(di);
            }
            drink.setIngredients(drinkIngredients);
        }

        drinkService.save(drink);
        return "redirect:/drink";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Drink drink = drinkService.getById(id);
        model.addAttribute("drink", drink);
        model.addAttribute("allIngredients", ingredientService.getAll());
        model.addAttribute("allUnits", unitService.getAll());
        return "drink/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute @Valid Drink drink,
                         BindingResult bindingResult,
                         @RequestParam(value = "ingredientIds", required = false) List<Long> ingredientIds,
                         @RequestParam(value = "quantities", required = false) List<Double> quantities,
                         Model model) {

        if (drink.getUnitMeasure() != null && drink.getUnitMeasure().getId() != null) {
            UnitOfMeasure um = unitService.getById(drink.getUnitMeasure().getId());
            drink.setUnitMeasure(um);
        } else {
            bindingResult.rejectValue("unitMeasure", "NotNull", "Unidade de medida é obrigatória");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("allIngredients", ingredientService.getAll());
            model.addAttribute("allUnits", unitService.getAll());
            return "drink/edit";
        }

        drink.getIngredients().clear();
        if (ingredientIds != null && quantities != null) {
            List<DrinkIngredient> drinkIngredients = new ArrayList<>();
            for (int i = 0; i < ingredientIds.size(); i++) {
                Ingredient ing = ingredientService.getById(ingredientIds.get(i));
                Double qty = quantities.get(i);

                DrinkIngredient di = new DrinkIngredient();
                di.setDrink(drink);
                di.setIngredient(ing);
                di.setQuantity(qty);

                drinkIngredients.add(di);
            }
            drink.setIngredients(drinkIngredients);
        }

        drinkService.save(drink);
        return "redirect:/drink";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        drinkService.deleteById(id);
        return "redirect:/drink";
    }

    @GetMapping("/{drinkId}/remove-ingredient/{ingredientId}")
    public String removeIngredient(@PathVariable Long drinkId,
                                   @PathVariable Long ingredientId) {
        drinkService.removeIngredient(drinkId, ingredientId);
        return "redirect:/drink/edit/" + drinkId;
    }
}