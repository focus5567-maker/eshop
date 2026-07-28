package com.example.eshop.controller;

import com.example.eshop.entity.Product;
import com.example.eshop.service.CategoryService;
import com.example.eshop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 商品模組控制器
 * 處理所有與商品相關的 HTTP 請求，並透過 HTML 模板（如 Thymeleaf）渲染畫面
 */
@Controller // 標示此類別為 Spring MVC Controller，會回傳 View (HTML 頁面名稱)
@RequestMapping("/products") // 統一設定此 Controller 下所有 API 的基礎路徑為 /products
public class ProductController {

    // 注入業務邏輯層 (Service)
    private final ProductService productService;
    private final CategoryService categoryService;

    // ✅ 手動建構子注入（Constructor Injection）
    // Spring Boot 官方推薦的依賴注入方式，能確保類別不可變性（Immutable）且方便單元測試
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * 1. 取得商品清單（支援分頁）
     * HTTP 方法：GET /products 或 /products?page=0
     *
     * @param page 當前頁碼（從 0 開始計算，預設為第 0 頁）
     * @param model 用於傳送資料給前端 HTML 頁面的容器
     * @return 視圖路徑 "products/list" -> 對應 templates/products/list.html
     */
    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        // 每頁顯示 10 筆資料，向 Service 請求分頁資料
        Page<Product> productPage = productService.getProducts(PageRequest.of(page, 10));

        // 將分頁資訊帶到前端畫面
        model.addAttribute("products", productPage.getContent());   // 當頁的商品列表
        model.addAttribute("currentPage", page);                    // 當前頁碼
        model.addAttribute("totalPages", productPage.getTotalPages()); // 總頁數
        model.addAttribute("totalItems", productPage.getTotalElements()); // 總資料筆數

        return "products/list";
    }

    /**
     * 2. 顯示建立新商品的表單頁面
     * HTTP 方法：GET /products/new
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // 帶入一個空的 Product 物件，供前端表單做資料綁定 (Form Binding)
        model.addAttribute("product", new Product());
        // 帶入分類清單，供表單的下拉選單使用
        model.addAttribute("categories", categoryService.findAll());
        return "products/form";
    }

    /**
     * 3. 處理新增商品的提交動作
     * HTTP 方法：POST /products
     *
     * @param product 自動將表單傳入的欄位資料綁定成 Product 物件
     */
    @PostMapping
    public String createProduct(@ModelAttribute Product product) {
        productService.saveProduct(product); // 呼叫 Service 儲存商品
        return "redirect:/products";        // 新增成功後，重導向（Redirect）回商品列表頁
    }

    /**
     * 4. 查看單一商品詳細資料
     * HTTP 方法：GET /products/{id} (例如: /products/5)
     */
    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        // 根據 URL 中的 id 取得商品，並帶給前端視圖
        model.addAttribute("product", productService.getProductById(id));
        return "products/view";
    }

    /**
     * 5. 顯示編輯商品的表單頁面
     * HTTP 方法：GET /products/{id}/edit (例如: /products/5/edit)
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        // 查出既有商品資料，填入表單供使用者修改（共用 form 頁面）
        model.addAttribute("product", productService.getProductById(id));
        // 帶入分類清單，供表單的下拉選單使用
        model.addAttribute("categories", categoryService.findAll());
        return "products/form";
    }

    /**
     * 6. 處理更新商品的提交動作
     * HTTP 方法：POST /products/{id}
     */
    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product) {
        product.setId(id);                   // 確保 Update 的對象 ID 正確
        productService.saveProduct(product); // 儲存更新後的商品
        return "redirect:/products";        // 更新成功後重導向回列表頁
    }

    /**
     * 7. 刪除商品
     * HTTP 方法：GET /products/{id}/delete
     * (註：傳統 HTML 表單不易發送 DELETE 請求，常以 GET 或 POST 帶 delete 路徑替代)
     */
    @GetMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id); // 呼叫 Service 執行刪除
        return "redirect:/products";      // 刪除後重導向回列表頁
    }
}