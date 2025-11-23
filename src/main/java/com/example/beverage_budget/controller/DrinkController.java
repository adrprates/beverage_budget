package com.example.beverage_budget.controller;

import com.example.beverage_budget.model.Drink;
import com.example.beverage_budget.model.DrinkIngredient;
import com.example.beverage_budget.model.Ingredient;
import com.example.beverage_budget.model.UnitOfMeasure;
import com.example.beverage_budget.service.DrinkService;
import com.example.beverage_budget.service.IngredientService;
import com.example.beverage_budget.service.UnitConversionService;
import com.example.beverage_budget.service.UnitOfMeasureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/drink")
public class DrinkController {

    @Autowired
    private DrinkService drinkService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private UnitOfMeasureService unitService;

    @Autowired
    private UnitConversionService conversionService;

    @GetMapping({"", "/"})
    public String list(@RequestParam(required = false) String search, Model model) {
        List<Drink> drinks = (search != null && !search.isEmpty()) ?
                drinkService.searchByName(search) : drinkService.getAll();
        model.addAttribute("list", drinks);
        model.addAttribute("search", search);
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
                       @RequestParam(value = "ingredientUnits", required = false) List<String> ingredientUnitCodes,
                       Model model,
                       RedirectAttributes redirectAttributes) {

        if (ingredientIds == null || ingredientIds.isEmpty() || quantities == null || quantities.isEmpty()) {
            redirectAttributes.addFlashAttribute("ingredientError", "O drink deve ter pelo menos um ingrediente.");
            return "redirect:/drink/create";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.drink", bindingResult);
            redirectAttributes.addFlashAttribute("drink", drink);
            return "redirect:/drink/create";
        }

        List<DrinkIngredient> drinkIngredients = new ArrayList<>();
        for (int i = 0; i < ingredientIds.size(); i++) {
            Ingredient ing = ingredientService.getById(ingredientIds.get(i));
            Double qty = quantities.get(i);

            final String unitCode = (ingredientUnitCodes != null && ingredientUnitCodes.size() > i)
                    ? ingredientUnitCodes.get(i)
                    : ing.getUnitMeasure().getCode();

            UnitOfMeasure ingUnit = unitService.getAll().stream()
                    .filter(u -> u.getCode().equalsIgnoreCase(unitCode))
                    .findFirst()
                    .orElse(ing.getUnitMeasure());

            Double convertedQty = qty;
            if (!ingUnit.getCode().equals(ing.getUnitMeasure().getCode())) {
                try {
                    convertedQty = conversionService
                            .convert(qty, ingUnit, ing.getUnitMeasure())
                            .orElseThrow();
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("ingredientError",
                            "Não é possível converter " + ing.getName() + " para " + ingUnit.getDescription()+ ".");
                    return "redirect:/drink/create";
                }
            }

            DrinkIngredient di = new DrinkIngredient();
            di.setDrink(drink);
            di.setIngredient(ing);
            di.setQuantity(convertedQty);
            di.setUnitMeasure(ing.getUnitMeasure());
            drinkIngredients.add(di);
        }

        drink.setIngredients(drinkIngredients);
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
                         @RequestParam(value = "ingredientUnits", required = false) List<String> ingredientUnitCodes,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        Long drinkId = drink.getId();

        if (ingredientIds == null || ingredientIds.isEmpty() || quantities == null || quantities.isEmpty()) {
            redirectAttributes.addFlashAttribute("ingredientError", "O drink deve ter pelo menos um ingrediente.");
            return "redirect:/drink/edit/" + drinkId;
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.drink", bindingResult);
            redirectAttributes.addFlashAttribute("drink", drink);
            return "redirect:/drink/edit/" + drinkId;
        }

        drink.getIngredients().clear();
        List<DrinkIngredient> drinkIngredients = new ArrayList<>();
        for (int i = 0; i < ingredientIds.size(); i++) {
            Ingredient ing = ingredientService.getById(ingredientIds.get(i));
            Double qty = quantities.get(i);

            final String unitCode = (ingredientUnitCodes != null && ingredientUnitCodes.size() > i)
                    ? ingredientUnitCodes.get(i)
                    : ing.getUnitMeasure().getCode();

            UnitOfMeasure ingUnit = unitService.getAll().stream()
                    .filter(u -> u.getCode().equalsIgnoreCase(unitCode))
                    .findFirst()
                    .orElse(ing.getUnitMeasure());

            Double convertedQty = qty;
            if (!ingUnit.getCode().equals(ing.getUnitMeasure().getCode())) {
                try {
                    convertedQty = conversionService
                            .convert(qty, ingUnit, ing.getUnitMeasure())
                            .orElseThrow();
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("ingredientError",
                            "Não é possível converter " + ing.getName() + " para " + ingUnit.getDescription()+ ".");
                    return "redirect:/drink/edit/" + drinkId;
                }
            }

            DrinkIngredient di = new DrinkIngredient();
            di.setDrink(drink);
            di.setIngredient(ing);
            di.setQuantity(convertedQty);
            di.setUnitMeasure(ing.getUnitMeasure());
            drinkIngredients.add(di);
        }

        drink.setIngredients(drinkIngredients);
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

    @GetMapping("/{id}/ingredients")
    @ResponseBody
    public List<Map<String, Object>> getIngredients(@PathVariable Long id) {
        Drink drink = drinkService.getById(id);
        List<Map<String, Object>> list = new ArrayList<>();

        if (drink.getIngredients() != null) {
            for (DrinkIngredient di : drink.getIngredients()) {
                Map<String, Object> item = new HashMap<>();
                item.put("ingredientId", di.getIngredient().getId());
                item.put("ingredientName", di.getIngredient().getName());
                item.put("quantity", di.getQuantity());
                item.put("unitMeasure", di.getUnitMeasure() != null ? di.getUnitMeasure().getDescription() : "unidade");
                list.add(item);
            }
        }
        return list;
    }

    @GetMapping("/{id}/budget-ingredients")
    @ResponseBody
    public List<Map<String, Object>> getBudgetIngredients(@PathVariable Long id) {

        Drink drink = drinkService.getByIdWithIngredients(id);

        return drink.getIngredients().stream().map(di -> {

            Ingredient ing = di.getIngredient();

            Map<String, Object> m = new HashMap<>();
            m.put("ingredientId", ing.getId());
            m.put("ingredientName", ing.getName());
            m.put("unitMeasure", ing.getUnitMeasure().getCode());
            m.put("quantity", di.getQuantity());
            m.put("ingredientVolume", ing.getVolume());

            return m;

        }).collect(Collectors.toList());
    }
}