package com.example.eshop.controller;

import com.example.eshop.mapper.ProductSearchMapper;
import com.example.eshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 消費者前台商品瀏覽/搜尋 Controller
 * 跟 ProductController（後台管理 CRUD）分開，職責不同：
 * 這裡只負責「查詢與顯示」，不提供新增/編輯/刪除。
 */
@Controller
@RequiredArgsConstructor
public class ShopController {

    private final ProductSearchMapper productSearchMapper;
    private final CategoryService categoryService;

    @GetMapping("/shop")
    public String shop(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Long categoryId,
                        @RequestParam(required = false) String sort,
                        Model model) {

        model.addAttribute("results", productSearchMapper.search(keyword, categoryId, sort));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sort", sort);

        return "shop/index";
    }
}
