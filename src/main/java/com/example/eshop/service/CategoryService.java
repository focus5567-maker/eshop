package com.example.eshop.service;

// Category：這個 Service 所有方法經手的核心物件。
import com.example.eshop.entity.Category;

// CategoryRepository：這個 Service 唯一依賴的資料存取層。
import com.example.eshop.repository.CategoryRepository;

// Lombok 提供，自動幫這個類別產生「建構子注入」的程式碼，
// 效果等同你在 ProductService 裡自己手寫的建構子，只是用註解自動生成，不用自己寫。
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

// 分類模組的業務邏輯層，負責「怎麼處理分類相關的規則」。
// 目前這個類別的內容非常單純，幾乎是「直接轉呼叫」Repository，
// 沒有像 ProductService 那樣有圖片上傳這種額外的業務邏輯。
//
// 誰會用到這個類別：
// → CategoryController.java 注入這個 Service，呼叫裡面的方法完成後台分類管理的每個動作

// @RequiredArgsConstructor：Lombok 提供的註解，
// 自動幫這個類別產生一個「建構子」，
// 建構子的參數，會是這個類別裡所有標記 final 的欄位。

@Service
@RequiredArgsConstructor
public class CategoryService {

    //宣告
    private final CategoryRepository categoryRepository;

    // 誰會呼叫這個方法、結果傳去哪：
    // → CategoryController.list()：查完塞進 Model，給 categories/list.html 畫成表格
    // → ProductController.showCreateForm()、showEditForm()：
    //   商品新增/編輯表單的分類下拉選單，選項清單就是這裡查出來的
    // → ProductApiController.getCategories()：前台商品搜尋頁的分類篩選選單，
    //   也是呼叫這個方法拿到全部分類，再包裝成 JSON 給 Vue 用
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    // 誰會呼叫這個方法：
    // → CategoryController.create()：新增分類表單送出時呼叫
    // !目前分類模組沒有「編輯」功能（跟商品模組不對稱），
    // 這個方法雖然叫 save，理論上也能處理更新，但實際上只有新增流程會用到它
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    // 誰會呼叫這個方法：
    // → CategoryController.delete()：點擊列表上的「刪除」連結時呼叫
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}