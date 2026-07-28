package com.example.eshop.service;

import com.example.eshop.entity.Product;
import com.example.eshop.service.ProductService; // 註：同 package 內可省略此 import
import com.example.eshop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 商品模組業務邏輯層 (Business Logic / Service Layer)
 * 負責處理商業邏輯，並作為 Controller 與 Repository (DAO) 之間的溝通橋樑
 */
@Service // 標示此類別為 Spring 商業邏輯層元件，交由 Spring IoC 容器託管
public class ProductService {

    // 注入 Repository 專門處理資料庫存取
    private final ProductRepository productRepository;

    // ✅ 手動建構子注入（Constructor Injection）
    // Spring Boot 官方強烈推薦的注入方式，方便做單元測試 (Unit Test) 與保證屬性不可變 (final)
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 1. 分頁取得所有商品
     * 
     * @param pageable 分頁與排序條件 (包含當前頁碼、每頁數量、排序欄位)
     * @return 包含分頁資訊的商品列表
     */
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * 2. 根據 ID 查詢單一商品
     * 
     * @param id 商品主鍵 ID
     * @return 找到了就回傳 Product 實體；若沒找到則拋出 RuntimeException 異常
     */
    public Product getProductById(Long id) {
        // findById 回傳 Optional<Product>，搭配 .orElseThrow() 可以很優雅地處理找不到資料的情況
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    /**
     * 3. 儲存或更新商品
     * 
     * @param product 要儲存的商品物件
     * @return 儲存成功後的 Product 實體（若是新建商品，會包含資料庫自動產生的 id）
     */
    public Product saveProduct(Product product) {
        // save() 會自動判斷：若 product.id 為空則執行 INSERT，若有 id 則執行 UPDATE
        return productRepository.save(product);
    }

    /**
     * 4. 根據 ID 刪除商品
     * 
     * @param id 要刪除的商品主鍵 ID
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * 5. 搜尋商品（支援關鍵字與分頁）
     * 包含防護機制：當關鍵字為空時，自動切換為查詢全部商品
     * 
     * @param keyword  使用者輸入的搜尋關鍵字
     * @param pageable 分頁資訊
     * @return 分頁後的搜尋結果
     */
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        // 防護邏輯：若關鍵字為 null、空字串或全為空白字元，直接回傳所有商品列表
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll(pageable);
        }
        
        // 呼叫 Repository 衍生查詢：忽略大小寫 + 模糊搜尋 + 去除關鍵字前後空格
        return productRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
    }
}