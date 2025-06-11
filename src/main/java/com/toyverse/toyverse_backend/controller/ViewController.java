package com.toyverse.toyverse_backend.controller;

import com.toyverse.toyverse_backend.service.ToyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {
    private final ToyService toyService;

    public ViewController(ToyService toyService) {
        this.toyService = toyService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("toys", toyService.getFilteredToys(null, null));
        return "home";
    }

    @GetMapping("/toys/{id}")
    public String toyDetail(@PathVariable Long id, Model model) {
        model.addAttribute("toy", toyService.getToyById(id));
        return "toy-detail";
    }

    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }

    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("toys", toyService.getFilteredToys(null, null));
        return "admin";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
} 