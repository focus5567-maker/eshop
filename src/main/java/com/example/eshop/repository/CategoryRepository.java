package com.example.eshop.repository;

// Category：這個 Repository 要操作的目標 Entity。
import com.example.eshop.entity.Category;

// JpaRepository：Spring Data JPA 核心介面，繼承後自動獲得
// findAll()、save()、deleteById() 這些現成方法，不用自己寫實作。
import org.springframework.data.jpa.repository.JpaRepository;

// @Repository：標記這是資料存取層，讓 Spring 啟動時把它收進容器管理。
import org.springframework.stereotype.Repository;

// 分類的資料存取層，只負責「怎麼跟資料庫溝通」。
//
// 誰會用到這個介面：
// → CategoryService.java 注入這個介面，呼叫裡面的方法查/存/刪分類資料
//
// 這是「介面」，純繼承 JpaRepository，完全沒有自訂任何查詢方法——
// 分類模組的需求很單純（查全部、新增、刪除），JpaRepository 內建的方法就已經夠用，
// 不需要像 ProductRepository 那樣額外宣告 findByNameContaining 這種衍生查詢。

//<Entity名稱, 主鍵型態>

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}