package com.example.eshop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConsumerAppController {

    @GetMapping("/shop")
    public String shop() {
        return "app/index";
    }

    @GetMapping({"/cart", "/checkout", "/orders"})
    public String memberOnlyPages(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        return "app/index";
    }
}