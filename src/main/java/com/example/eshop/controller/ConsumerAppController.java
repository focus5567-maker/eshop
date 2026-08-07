package com.example.eshop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 消費者前台（商品搜尋、購物車、結帳）統一入口 Controller。
 * 這三個網址都回傳同一份空殼樣板，實際顯示哪個畫面交給前端 Vue Router 判斷。
 *
 * 依規格書第五節「每個 Controller 在處理請求前檢查 Session 是否存在有效使用者」，
 * /cart、/checkout 這兩個網址（模組4、5規定會員限定）在後端也要檢查登入狀態，
 * 不能只靠前端路由守衛（那樣使用者能繞過，也不符合規格書要求）。
 * /shop（模組3商品瀏覽）維持公開，不需要登入。
 */
@Controller
public class ConsumerAppController {

    @GetMapping("/shop")
    public String shop() {
        return "app/index";
    }

    @GetMapping({"/cart", "/checkout"})
    public String memberOnlyPages(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        return "app/index";
    }
}