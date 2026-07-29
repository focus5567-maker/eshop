package com.example.eshop.controller;

import com.example.eshop.entity.User;
import com.example.eshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    /** 顯示登入表單 */
    @GetMapping("/login")
    public String showForm() {
        return "auth/login";
    }

    /** 處理登入送出 */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                         @RequestParam String password,
                         HttpSession session,
                         Model model) {

        User user = userService.login(username, password);

        if (user == null) {
            model.addAttribute("error", "帳號或密碼錯誤");
            return "auth/login";
        }

        // 登入成功，把使用者資訊存進 Session
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        return "redirect:/products";
    }

    /** 登出：清空 Session */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}