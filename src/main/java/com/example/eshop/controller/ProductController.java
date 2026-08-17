package com.example.eshop.controller;

// Product：接收表單資料時，Spring 會自動組裝成這個物件的實例。
import com.example.eshop.entity.Product;

// CategoryService：新增/編輯表單需要「分類清單」給下拉選單用，
// 所以除了 ProductService，還額外注入這個 Service。
import com.example.eshop.service.CategoryService;

// ProductService：這個 Controller 唯一負責處理商品業務邏輯的地方，
// 所有商品相關的動作（查詢、搜尋、新增、修改、刪除），全部委託給這個 Service 執行，
// Controller 自己不寫任何業務邏輯，只負責接請求、轉資料、決定畫面。
import com.example.eshop.service.ProductService;

// Page：分頁查詢的結果容器，除了資料本身，還附帶「總頁數」「總筆數」這些分頁資訊。
import org.springframework.data.domain.Page;

// PageRequest：用來建立 Pageable 的具體實作，
// 例如 PageRequest.of(page, 10) 代表「查第 page 頁，每頁 10 筆」。
import org.springframework.data.domain.PageRequest;

// @Controller：標記這是傳統的頁面控制器（不是 REST API），
// 方法回傳的字串會被當成「Thymeleaf 樣板名稱」去尋找對應的 HTML 檔案。
import org.springframework.stereotype.Controller;

// Model：Controller 跟 Thymeleaf 樣板之間傳遞資料的容器，
// 用 model.addAttribute("products", ...) 把資料放進去，樣板那邊用 ${products} 就能讀取到。
import org.springframework.ui.Model;

// 用 * 一次引入這個套件底下所有常用的 HTTP 相關註解，
// 包含 @GetMapping、@PostMapping、@RequestMapping、@PathVariable、
// @RequestParam、@ModelAttribute 這些，這個 Controller 都有用到。
import org.springframework.web.bind.annotation.*;

// MultipartFile：接收表單上傳的圖片檔案時，用這個型態接住。
import org.springframework.web.multipart.MultipartFile;

// 後台商品管理的頁面控制器，負責接收 HTTP 請求、呼叫 Service 處理，
// 最後決定要回傳哪個 Thymeleaf 樣板。
//
// 這個 Controller 底下所有網址（/products/**），都會先被 AdminRoleInterceptor 攔截檢查，
// 只有 ADMIN 角色能進來，這件事完全不用寫在這個檔案裡，是攔截器統一處理的。
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // GET /products：分頁列表，同時支援關鍵字模糊搜尋
    //
    // 誰會用到這裡的資料：
    // → templates/products/list.html，用 th:each 把 products 畫成表格，
    //   同時讀取 keyword 這個值，讓搜尋框在送出後還能顯示剛才輸入的關鍵字，不會被清空
    //
    // keyword 是 required = false（沒寫預設就是 required = true），
    // 代表這個參數可以不用帶，第一次進入這頁（還沒搜尋過）時，
    // keyword 會是 null，ProductService.searchProducts() 內部會判斷成「查全部」
    @GetMapping
    public String listProducts(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) String keyword,
                                Model model) {
        Page<Product> productPage = productService.searchProducts(keyword, PageRequest.of(page, 10));

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("keyword", keyword);   // 讓畫面能保留使用者剛才輸入的關鍵字

        return "products/list";
    }

    // GET /products/new：顯示新增表單
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "products/form";
    }

    // POST /products：處理新增送出
    // imageFile 是表單裡 <input type="file" name="imageFile"> 送過來的檔案，
    // 交給 productService.saveProduct() 統一處理（包含圖片上傳邏輯）
    @PostMapping
    public String createProduct(@ModelAttribute Product product,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        productService.saveProduct(product, imageFile);
        return "redirect:/products";
    }

    // GET /products/{id}：查看單一商品
    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "products/view";
    }

    // GET /products/{id}/edit：顯示編輯表單（跟新增共用同一份 form.html）
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.findAll());
        return "products/form";
    }

    // POST /products/{id}：處理編輯送出
    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        product.setId(id);   // 確保更新的是這一筆，不是新增
        productService.saveProduct(product, imageFile);
        return "redirect:/products";
    }

    // GET /products/{id}/delete：刪除
    // 用 GET 而非標準 DELETE，因為傳統 HTML 表單/連結不容易送出 DELETE 請求
    @GetMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}