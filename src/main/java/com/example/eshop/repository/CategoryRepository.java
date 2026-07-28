package com.example.eshop.repository;

import com.example.eshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 分類資料存取層 (DAO / Repository)
 * 繼承 JpaRepository 後，自動擁有基本 CRUD 能力，不需自己實作。
 *
 * 泛型說明：
 * - Category: 對應的 Entity 類別
 * - Long: Category 主鍵 (@Id) 的資料型態
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}