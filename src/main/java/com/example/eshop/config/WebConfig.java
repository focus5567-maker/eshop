package com.example.eshop.config;

import com.example.eshop.entity.Category;
import com.example.eshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 全域設定類別
 *
 * 實作 WebMvcConfigurer 介面，用來客製化 Spring MVC 的預設行為。
 * 這裡只覆寫 addFormatters() 方法，註冊一個自訂的型別轉換器 (Converter)。
 *
 * 使用情境：
 * 前端表單（例如 products/form.html）的下拉選單，選擇分類時，
 * 瀏覽器送出的其實只是分類的 ID 字串（例如 "1"），
 * 但 Product Entity 裡的 category 欄位型別是 Category 物件，
 * Spring 預設不知道怎麼把字串轉成物件，所以需要這個 Converter 幫忙轉換。
 */
@Component // 交給 Spring 容器管理，啟動時會自動被掃描並套用設定
@RequiredArgsConstructor // Lombok 自動產生建構子，注入 final 欄位 (categoryRepository)
public class WebConfig implements WebMvcConfigurer {

    // 注入資料庫存取層，轉換時需要靠它查出實際的 Category 物件
    private final CategoryRepository categoryRepository;

    /**
     * 註冊自訂的型別轉換器
     *
     * @param registry Spring 提供的格式化/轉換器註冊器，
     *                 呼叫 addConverter() 把自訂邏輯掛進去，
     *                 之後只要是 String -> Category 的轉換需求，都會套用這段邏輯。
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, Category>() {

            /**
             * 實際轉換邏輯：
             * 把表單傳來的分類 ID 字串，轉成對應的 Category 實體。
             *
             * @param id 表單送出的分類 ID（字串型態，例如 "1"）
             * @return 對應的 Category 物件；若 id 為空或查無資料則回傳 null
             */
            @Override
            public Category convert(String id) {
                // 防呆：下拉選單選「-- 請選擇分類 --」時，id 會是空字串
                if (id == null || id.isBlank()) {
                    return null;
                }
                // 依 ID 查詢資料庫，找不到就回傳 null（避免拋出例外中斷流程）
                return categoryRepository.findById(Long.parseLong(id)).orElse(null);
            }
        });
    }
}