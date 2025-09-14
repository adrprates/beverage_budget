package com.example.beverage_budget.controller;

import com.example.beverage_budget.model.Resource;
import com.example.beverage_budget.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping({"","/"})
    public String list(Model model) {
        model.addAttribute("list", resourceService.getAll());
        return "resource/list";
    }

    @GetMapping("create")
    public String create(Model model) {
        model.addAttribute("resource", new Resource());
        return "resource/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute @Valid Resource resource, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "resource/create";
        }
        resourceService.save(resource);
        return "redirect:/resource";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Resource resource = resourceService.getById(id);
        model.addAttribute("resource", resource);
        return "resource/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute @Valid Resource resource, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "resource/edit";
        }
        resourceService.save(resource);
        return "redirect:/resource";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        resourceService.deleteById(id);
        return "redirect:/resource";
    }
}