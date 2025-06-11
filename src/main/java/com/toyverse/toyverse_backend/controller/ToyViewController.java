package com.toyverse.toyverse_backend.controller;

import com.toyverse.toyverse_backend.service.ToyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ToyViewController {

    @Autowired
    private ToyService toyService;

    @GetMapping("/toy-detail")
    public String toyDetail(@RequestParam Long id, Model model) {
        model.addAttribute("toy", toyService.getToyById(id));
        return "toy-detail";
    }
} 