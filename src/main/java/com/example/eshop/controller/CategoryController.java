package com.example.eshop.controller;

// Category：新增表單送出的資料，會被 Spring 自動組裝成這個物件。
import com.example.eshop.entity.Category;

// CategoryService：這個 Controller 唯一依賴的業務邏輯層，
// 所有分類相關的動作，全部委託給這個 Service 執行。
import com.example.eshop.service.CategoryService;

// Lombok 提供，自動幫這個類別產生「建構子注入」的程式碼，不用自己手寫建構子。
import lombok.RequiredArgsConstructor;

// @Controller：標記這是傳統頁面控制器，回傳字串是 Thymeleaf 樣板名稱。
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

/**
 * 分類模組控制器
 * 處理所有與商品分類相關的 HTTP 請求，並回傳對應的 Thymeleaf 頁面。
 *
 * 這個 Controller 底下所有網址（/categories/**），
 * 都會先被 AdminRoleInterceptor 攔截檢查，只有 ADMIN 角色能進來，
 * 這件事完全不用寫在這個檔案裡，是攔截器統一處理的。
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 顯示分類列表頁
     *
     * 誰會用到這裡的資料：
     * → categories/list.html，用 th:each 把 categories 畫成表格
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "categories/list";
    }

    /**
     * 顯示新增分類的表單頁
     *
     * 誰會用到這裡的資料：
     * → categories/form.html，帶一個空的 Category 物件給表單做欄位綁定
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/form";
    }

    /**
     * 處理新增分類的提交動作
     *
     * 誰會呼叫這裡：
     * → categories/form.html 的表單送出時觸發
     * 呼叫 CategoryService.save() 存進資料庫，完成後導回列表頁
     */
    @PostMapping
    public String create(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/categories";
    }

    /**
     * 刪除分類
     * (與 ProductController 風格一致，用 GET 連結而非 POST 表單，
     * 因為傳統 HTML 連結不容易送出標準的 DELETE 請求)
     *
     * 誰會呼叫這裡：
     * → categories/list.html 表格裡每一列的「刪除」連結
     */
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return "redirect:/categories";
    }
}