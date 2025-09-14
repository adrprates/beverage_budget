package com.example.beverage_budget.controller;

import com.example.beverage_budget.model.Ingredient;
import com.example.beverage_budget.service.IngredientService;
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

    @GetMapping({"","/"})
    public String list(Model model) {
        model.addAttribute("list", ingredientService.getAll());
        return "ingredient/list";
    }

    @GetMapping("create")
    public String create(Model model) {
        model.addAttribute("ingredient", new Ingredient());
        return "ingredient/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute @Valid Ingredient ingredient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "ingredient/create";
        }
        ingredientService.save(ingredient);
        return "redirect:/ingredient";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Ingredient ingredient = ingredientService.getById(id);
        model.addAttribute("ingredient", ingredient);
        return "ingredient/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute @Valid Ingredient ingredient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
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