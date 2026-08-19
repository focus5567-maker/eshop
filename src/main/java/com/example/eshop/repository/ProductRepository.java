package com.example.eshop.repository;

// Product 是這個 Repository 要操作的目標 Entity，
// 泛型 <Product, Long> 裡的 Product，就是靠這個 import 進來的。
import com.example.eshop.entity.Product;

// Page、Pageable 是 Spring Data JPA 提供的分頁機制。
// Pageable：代表「要查第幾頁、每頁幾筆、要不要排序」這些分頁條件，由呼叫端傳進來。
// Page：查詢結果的容器，除了資料本身，還附帶「總頁數」「總筆數」這些分頁資訊。
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// JpaRepository：Spring Data JPA 提供的核心介面，
// 這個專案所有 Repository（不只商品，分類、會員、購物車、訂單都一樣）
// 都靠繼承這個介面，自動獲得 findAll()、save()、deleteById() 這些現成方法，
// 不用自己寫任何一行 SQL 或實作邏輯。
import org.springframework.data.jpa.repository.JpaRepository;

// @Repository 這個標記本身也是從這裡 import 進來的，
// 效果類似 @Component，讓 Spring 啟動時把這個介面納入管理範圍。
import org.springframework.stereotype.Repository;

// 商品的資料存取層，只負責「怎麼跟資料庫溝通」，完全沒有業務邏輯。
//
// 誰會用到這個介面：
// → ProductService.java 注入這個介面，呼叫裡面的方法去查/存/刪商品資料，
//   Service 自己不會直接組 SQL 或碰資料庫連線，全部委託給這裡處理。
//
// 這是「介面」，不是類別——繼承 JpaRepository 之後，
// findAll()、save()、deleteById() 這些方法完全不用自己寫，
// Spring 在應用程式啟動時，會在背後動態產生一個「隱形的實作類別」，
// 你完全看不到這個實作的原始碼，但它確實存在、確實能被呼叫、確實會執行對應的 SQL。

//<Entity名稱, 主鍵型態>
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 方法名稱本身，就是 JPA 用來解析出查詢規則的依據，這叫「衍生查詢」（Derived Query）：
    // findBy Name Containing IgnoreCase
    // → WHERE LOWER(name) LIKE LOWER('%關鍵字%')
    // JPA 光看方法名稱，就自動組出對應的 SQL 語句，你完全不用自己寫。
    //
    // Pageable 參數：支援分頁查詢，用在後台商品搜尋功能，
    // 呼叫端（ProductService）會傳進「要第幾頁、每頁幾筆」，
    // 這個方法會自動處理成 SQL 裡的 LIMIT/OFFSET。
    //
    // 誰會用到這個方法：
    // → ProductService.searchProducts()，這個方法是後台商品搜尋功能唯一用到的自訂查詢，
    //   其餘的商品 CRUD（新增、單筆查詢、刪除），都是靠 JpaRepository 內建的方法完成，
    //   不需要在這裡另外宣告。


    // 依 id 排序，確保順序穩定不會因為編輯商品而跳動
    Page<Product> findByNameContainingIgnoreCaseOrderById(String name, Pageable pageable);

    // 查全部時（沒輸入關鍵字）也要有固定排序
    Page<Product> findAllByOrderById(Pageable pageable);
}