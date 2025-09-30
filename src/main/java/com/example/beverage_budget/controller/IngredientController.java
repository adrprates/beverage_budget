package com.example.beverage_budget.controller;

import com.example.beverage_budget.model.Ingredient;
<<<<<<< HEAD
import com.example.beverage_budget.service.IngredientService;
=======
import com.example.beverage_budget.model.UnitOfMeasure;
import com.example.beverage_budget.service.IngredientService;
import com.example.beverage_budget.service.UnitOfMeasureService;
>>>>>>> 3bd4d13 (Add UnitOfMeasure table)
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

<<<<<<< HEAD
    @GetMapping({"","/"})
=======
    @Autowired
    private UnitOfMeasureService unitService;

    @GetMapping({"", "/"})
>>>>>>> 3bd4d13 (Add UnitOfMeasure table)
    public String list(Model model) {
        model.addAttribute("list", ingredientService.getAll());
        return "ingredient/list";
    }

    @GetMapping("create")
    public String create(Model model) {
        model.addAttribute("ingredient", new Ingredient());
<<<<<<< HEAD
=======
        model.addAttribute("allUnits", unitService.getAll());
>>>>>>> 3bd4d13 (Add UnitOfMeasure table)
        return "ingredient/create";
    }

    @PostMapping("/save")
<<<<<<< HEAD
    public String save(@ModelAttribute @Valid Ingredient ingredient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "ingredient/create";
        }
=======
    public String save(@ModelAttribute @Valid Ingredient ingredient,
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allUnits", unitService.getAll());
            return "ingredient/create";
        }

        UnitOfMeasure um = unitService.getById(ingredient.getUnitMeasure().getId());
        ingredient.setUnitMeasure(um);

>>>>>>> 3bd4d13 (Add UnitOfMeasure table)
        ingredientService.save(ingredient);
        return "redirect:/ingredient";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Ingredient ingredient = ingredientService.getById(id);
        model.addAttribute("ingredient", ingredient);
<<<<<<< HEAD
=======
        model.addAttribute("allUnits", unitService.getAll());
>>>>>>> 3bd4d13 (Add UnitOfMeasure table)
        return "ingredient/edit";
    }

    @PostMapping("/update")
<<<<<<< HEAD
    public String update(@ModelAttribute @Valid Ingredient ingredient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "ingredient/edit";
        }
=======
    public String update(@ModelAttribute @Valid Ingredient ingredient,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allUnits", unitService.getAll());
            return "ingredient/edit";
        }

        UnitOfMeasure um = unitService.getById(ingredient.getUnitMeasure().getId());
        ingredient.setUnitMeasure(um);

>>>>>>> 3bd4d13 (Add UnitOfMeasure table)
        ingredientService.save(ingredient);
        return "redirect:/ingredient";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        ingredientService.deleteById(id);
        return "redirect:/ingredient";
    }
}