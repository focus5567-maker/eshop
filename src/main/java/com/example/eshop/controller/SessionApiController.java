package com.example.eshop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 提供給 Vue 前台查詢目前登入狀態的 API。
 * 不需要登入才能呼叫，未登入時回傳 loggedIn: false，而不是 401，
 * 因為「查詢自己是不是登入」這件事本身不該被擋下來。
 */
@RestController
@RequestMapping("/api/session")
public class SessionApiController {

    @GetMapping("/me")
    public Map<String, Object> getCurrentSession(HttpSession session) {
        Object userId = session.getAttribute("userId");
        Object username = session.getAttribute("username");
        Object role = session.getAttribute("role");

        if (userId == null) {
            return Map.of("loggedIn", false);
        }

        return Map.of(
                "loggedIn", true,
                "userId", userId,
                "username", username,
                "role", role
        );
    }
}
