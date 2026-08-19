package com.example.eshop.controller;

import com.example.eshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 會員管理（規格書模組8子功能之一）
 * 受 AdminRoleInterceptor 保護，僅 ADMIN 可存取。
 */
@Controller
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/admin/users")
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/admin/users/{id}/role")
    public String updateRole(@PathVariable Long id,
                              @RequestParam String role,
                              HttpSession session,
                              Model model) {
        Long currentAdminId = (Long) session.getAttribute("userId");

        try {
            userService.updateRole(id, currentAdminId, role);
        } catch (IllegalStateException | IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("users", userService.findAll());
            return "admin/users";
        }

        //redirect重新導向
        return "redirect:/admin/users";
    }
}
