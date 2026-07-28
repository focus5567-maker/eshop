package com.example.eshop.controller;

import com.example.eshop.entity.Category;
import com.example.eshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 分類模組控制器
 * 處理所有與商品分類相關的 HTTP 請求，並回傳對應的 Thymeleaf 頁面。
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 顯示分類列表頁
     * HTTP 方法：GET /categories
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "categories/list";
    }

    /**
     * 顯示新增分類的表單頁
     * HTTP 方法：GET /categories/new
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/form";
    }

    /**
     * 處理新增分類的提交動作
     * HTTP 方法：POST /categories
     */
    @PostMapping
    public String create(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/categories";
    }

    /**
     * 刪除分類
     * HTTP 方法：GET /categories/{id}/delete
     * (與 ProductController 風格一致，用 GET 連結而非 POST 表單)
     */
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return "redirect:/categories";
    }
}