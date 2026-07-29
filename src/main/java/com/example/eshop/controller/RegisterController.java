package com.example.eshop.controller;

import com.example.eshop.entity.User;
import com.example.eshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    /** 顯示註冊表單 */
    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    /** 處理註冊送出 */
    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                            @RequestParam String confirmPassword,
                            Model model) {

        if (userService.isUsernameTaken(user.getUsername())) {
            model.addAttribute("error", "這個帳號已經被使用了");
            return "auth/register";
        }
        if (userService.isEmailTaken(user.getEmail())) {
            model.addAttribute("error", "這個 Email 已經被註冊過了");
            return "auth/register";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "兩次輸入的密碼不一致");
            return "auth/register";
        }

        userService.register(user);
        return "redirect:/login";
    }
}