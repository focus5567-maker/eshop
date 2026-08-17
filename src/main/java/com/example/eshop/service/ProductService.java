package com.example.eshop.service;

// Product：這個 Service 所有方法，經手的核心物件，
// 業務邏輯判斷完之後，會把這個物件交給 ProductRepository 真正存進資料庫。
import com.example.eshop.entity.Product;

// ProductRepository：這個 Service 唯一依賴的資料存取層，
// 所有跟資料庫的溝通（查/存/刪），都透過呼叫這個介面的方法完成，
// Service 自己完全不會直接寫 SQL 或碰資料庫連線。
import com.example.eshop.repository.ProductRepository;

// Page、Pageable：Spring Data JPA 的分頁機制，
// Pageable 從 Controller 那邊傳進來（使用者要看第幾頁），
// Page 是查完之後回傳給 Controller 的結果容器（含資料+分頁資訊）。
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// @Service：標記這個類別是業務邏輯層，讓 Spring 啟動時自動把它收進容器管理，
// 之後 ProductController 才能直接注入這個類別使用。
import org.springframework.stereotype.Service;

// MultipartFile：Spring 提供的介面，代表「使用者透過表單上傳的檔案」，
// 這裡專門用來接收商品圖片。
import org.springframework.web.multipart.MultipartFile;

// IOException：讀寫檔案時可能發生的例外（例如硬碟空間不足、路徑錯誤），
// 這是 Checked Exception，Java 強制要求處理，這裡選擇用 try-catch 接住。
import java.io.IOException;

// Files、Path、Paths：Java NIO（New I/O）提供的檔案操作工具，
// 比較新、比較好用的檔案處理方式，用來建立資料夾、複製檔案。
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// StandardCopyOption：複製檔案時的選項設定，
// 這裡用 REPLACE_EXISTING，代表「如果目標位置已經有同名檔案，直接覆蓋」。
import java.nio.file.StandardCopyOption;

// UUID：用來產生一串不會重複的隨機英數字，
// 這裡拿來當作上傳圖片的檔名前綴，避免不同商品上傳同名檔案互相覆蓋。
import java.util.UUID;

// 商品模組的業務邏輯層，負責「怎麼處理商品相關的規則」，
// 不直接碰資料庫細節（那是 ProductRepository 的責任），也不處理 HTTP 請求（那是 Controller 的責任）。
//
// 誰會用到這個類別：
// → ProductController.java 注入這個 Service，呼叫裡面的方法完成後台商品管理的每個動作
@Service
public class ProductService {

    private final ProductRepository productRepository;

    // 圖片實際存放的資料夾（專案內的 static/uploads），
    // 因為在 static 資料夾裡，瀏覽器可以直接透過網址存取（例如 http://localhost:8080/eshop/uploads/xxx.jpg）
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    // 手動建構子注入：Spring 啟動時會自動把 ProductRepository 的實例塞進來
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 誰會呼叫這個方法、結果傳去哪：
    // → ProductController.viewProduct()：查完塞進 Model，給 products/view.html 顯示商品詳情
    // → ProductController.showEditForm()：查完塞進 Model，給 products/form.html 預填編輯表單
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // 誰會呼叫這個方法：
    // → ProductController.createProduct()：處理新增商品表單送出時呼叫
    // → ProductController.updateProduct()：處理編輯商品表單送出時呼叫
    // 兩處呼叫方式一模一樣，差別只在傳進來的 product 有沒有帶 id
    public Product saveProduct(Product product, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            // 有選新檔案：處理上傳，把回傳的網址存進 imageUrl
            String imageUrl = storeImage(imageFile);
            product.setImageUrl(imageUrl);
        } else if (product.getId() != null) {
            // 編輯模式但沒換圖：去資料庫撈出原本的 imageUrl，補回 product 物件，避免存檔時被清空
            Product existing = productRepository.findById(product.getId()).orElse(null);
            if (existing != null) {
                product.setImageUrl(existing.getImageUrl());
            }
        }

        // save() 會自動判斷：product.id 是 null 就新增，有值就更新
        return productRepository.save(product);
    }

    // 私有方法，只在這個類別內部被 saveProduct() 呼叫，
    // 不會被 Controller 或其他任何外部類別直接使用（private 這個關鍵字就是在限制這件事）
    private String storeImage(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 用 UUID 當檔名前綴，避免不同商品上傳同名檔案互相覆蓋
            String originalFilename = file.getOriginalFilename();
            String newFilename = UUID.randomUUID().toString() + "-" + originalFilename;

            Path targetPath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + newFilename;
        } catch (IOException e) {
            throw new RuntimeException("圖片上傳失敗", e);
        }
    }

    // 誰會呼叫這個方法：
    // → ProductController.deleteProduct()：點擊列表上的「刪除」連結時呼叫
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // 誰會呼叫這個方法、結果傳去哪：
    // → ProductController.listProducts()：目前後台商品列表唯一的查詢入口，
    //   查完的結果塞進 Model，給 products/list.html 畫成表格，
    //   同時也是「模糊搜尋」功能實際被觸發的地方
    //
    // 依 id 排序，確保商品列表順序穩定，不會因為編輯過某筆商品而跳動
    // （對應 ProductRepository 裡改名成 findAllByOrderById / findByNameContainingIgnoreCaseOrderById 的方法）
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAllByOrderById(pageable);
        }
        return productRepository.findByNameContainingIgnoreCaseOrderById(keyword.trim(), pageable);
    }
}