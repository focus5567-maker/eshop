package com.example.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 消費者前台商品搜尋頁面控制器
 * 只負責回傳一個「空殼」頁面，實際的搜尋邏輯由 Vue 透過 ProductApiController 非同步取得。
 */
@Controller
public class ShopController {

    @GetMapping("/shop")
    public String shop() {
        return "shop/index";
    }
}
