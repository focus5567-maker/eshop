package com.example.eshop.service;

import com.example.eshop.entity.Category;
import com.example.eshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分類模組業務邏輯層 (Service)
 * 目前僅包裝 Repository 的基本 CRUD 方法，
 * 之後若有分類相關的商業邏輯（例如排序規則、階層驗證），會集中寫在這裡，
 * 讓 Controller 不用直接碰資料庫存取層。
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /** 查詢所有分類，供列表頁與下拉選單使用 */
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    /** 新增或更新一筆分類（依 id 是否存在自動判斷） */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    /** 依 ID 刪除分類 */
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}