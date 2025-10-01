package com.example.beverage_budget.controller;

import com.example.beverage_budget.model.Ingredient;
import com.example.beverage_budget.model.UnitOfMeasure;
import com.example.beverage_budget.service.IngredientService;
import com.example.beverage_budget.service.UnitOfMeasureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ingredient")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private UnitOfMeasureService unitService;

    @GetMapping({"", "/"})
    public String list(Model model) {
        model.addAttribute("list", ingredientService.getAll());
        return "ingredient/list";
    }

    @GetMapping("create")
    public String create(Model model) {
        model.addAttribute("ingredient", new Ingredient());
        model.addAttribute("allUnits", unitService.getAll());
        return "ingredient/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute @Valid Ingredient ingredient,
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allUnits", unitService.getAll());
            return "ingredient/create";
        }

        if (ingredient.getUnitMeasure() != null && ingredient.getUnitMeasure().getId() != null) {
            UnitOfMeasure um = unitService.getById(ingredient.getUnitMeasure().getId());
            ingredient.setUnitMeasure(um);
        } else {
            bindingResult.rejectValue("unitMeasure", "NotNull", "Unidade de medida é obrigatória");
            model.addAttribute("allUnits", unitService.getAll());
            return "ingredient/create";
        }

        ingredientService.save(ingredient);
        return "redirect:/ingredient";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Ingredient ingredient = ingredientService.getById(id);
        model.addAttribute("ingredient", ingredient);
        model.addAttribute("allUnits", unitService.getAll());
        return "ingredient/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute @Valid Ingredient ingredient,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allUnits", unitService.getAll());
            return "ingredient/edit";
        }

        if (ingredient.getUnitMeasure() != null && ingredient.getUnitMeasure().getId() != null) {
            UnitOfMeasure um = unitService.getById(ingredient.getUnitMeasure().getId());
            ingredient.setUnitMeasure(um);
        } else {
            bindingResult.rejectValue("unitMeasure", "NotNull", "Unidade de medida é obrigatória");
            model.addAttribute("allUnits", unitService.getAll());
            return "ingredient/edit";
        }

        ingredientService.save(ingredient);
        return "redirect:/ingredient";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        ingredientService.deleteById(id);
        return "redirect:/ingredient";
    }
}