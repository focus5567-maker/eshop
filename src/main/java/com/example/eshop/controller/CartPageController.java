package com.example.eshop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 購物車頁面控制器
 * 只負責回傳一個「空殼」頁面，實際的購物車資料由 Vue 透過 CartApiController 非同步取得。
 */
@Controller
public class CartPageController {

    @GetMapping("/cart")
    public String cartPage(HttpSession session) {
        // 檢查登入狀態，跟你之前處理「需要登入才能用」的邏輯一致
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        return "cart/index";
    }
}

