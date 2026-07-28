package com.example.eshop.repository;

import com.example.eshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 商品資料存取層 (DAO / Repository)
 * 繼承 JpaRepository 後，自動擁有 basic CRUD (新增、讀取、更新、刪除) 與分頁排序能力
 * 
 * 泛型說明：
 * - Product: 對應的 Entity 類別
 * - Long: Product 主鍵 (@Id) 的資料型態
 */
@Repository // 宣告此介面為 Spring 商業邏輯中的 Repository 元件（可省略，JpaRepository 預設已包含）
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 關鍵字模糊搜尋商品名稱（包含忽略大小寫與分頁功能）
     * 
     * Spring Data JPA 會根據「方法名稱 (Derived Query)」自動解析生成 SQL 語法：
     * - findBy: 查詢指令 (SELECT ... FROM products)
     * - Name: 針對 Product 的 name 欄位
     * - Containing: 包含關鍵字 (對應 SQL 的 LIKE %keyword%)
     * - IgnoreCase: 忽略英文大小寫 (對應 SQL 的 LOWER() 或 UPPER())
     * 
     * 自動生成的 SQL 類似：
     * SELECT * FROM products 
     * WHERE LOWER(name) LIKE LOWER('%' || ? || '%') 
     * LIMIT ? OFFSET ?;
     * 
     * @param name     搜尋的商品名稱關鍵字
     * @param pageable 分頁與排序條件 (包含頁碼、每頁筆數、排序欄位)
     * @return 包含分頁資訊與查詢結果的 Page 物件
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}