package com.example.eshop.config;

import com.example.eshop.entity.Category;
import com.example.eshop.interceptor.AdminRoleInterceptor;
import com.example.eshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 這個類別是「全域設定中心」，裡面做了三件不相關的事：
// 1. 教 Spring 怎麼把表單送來的字串轉成 Category 物件
// 2. 註冊後台權限攔截器（擋非管理員進後台）
// 3. 提供一個密碼加密工具，給整個專案共用
@Component
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    // 這裡的 CategoryRepository，只有下面 addFormatters() 那個方法會用到，
    // 用來「依 id 查出真正的 Category 物件」。
    private final CategoryRepository categoryRepository;

    // ============================================================
    // 第一件事：把表單的「分類 ID 字串」自動轉成「Category 物件」
    // ============================================================
    //
    // 誰會用到這個轉換功能：
    // → ProductController.java 的 createProduct()、updateProduct()
    //   當使用者在 products/form.html 填完表單按下送出，
    //   表單裡分類欄位送過來的只是一個字串（例如 "1"），
    //   但 Product.java 的 category 欄位需要的是一個完整的 Category 物件，
    //   Spring 在把表單資料組裝成 Product 物件的過程中，
    //   會自動呼叫這裡寫好的轉換規則，把字串換成真正的 Category 物件，
    //   ProductController 完全不用自己處理這個轉換，Spring 背後就做好了。
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, Category>() {
            @Override
            public Category convert(String id) {
                // 下拉選單選到空白選項時，id 會是空字串，直接回傳 null，不要出錯
                if (id == null || id.isBlank()) {
                    return null;
                }
                // 拿字串 id 去資料庫查，查到就回傳真正的 Category 物件
                return categoryRepository.findById(Long.parseLong(id)).orElse(null);
            }
        });
    }

    // ============================================================
    // 第二件事：註冊「後台權限攔截器」，把它套用在指定的網址上
    // ============================================================
    //
    // 誰會被這個攔截器影響：
    // → ProductController.java（管的網址 /products/**）
    // → CategoryController.java（管的網址 /categories/**）
    // → AdminUserController.java（管的網址 /admin/**）
    //
    // 運作方式：
    // 使用者打上面這三個 Controller 管的任何網址時，
    // Spring 會先讓 AdminRoleInterceptor.java 裡的檢查邏輯跑一次，
    // 檢查沒過（沒登入 / 不是 ADMIN），就直接被攔截器導去 /login 或 /403，
    // 這三個 Controller 本身完全不用寫任何權限檢查的程式碼。
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminRoleInterceptor())
                .addPathPatterns("/products/**", "/categories/**", "/admin/**");
    }

    // ============================================================
    // 第三件事：做出一個密碼加密工具，讓其他 Service 可以直接拿去用
    // ============================================================
    //
    // 誰會用到這個加密工具：
    // → UserService.java
    //   register()：把使用者輸入的明文密碼加密後才存進資料庫
    //   login()：拿使用者輸入的密碼，跟資料庫存的加密結果比對是否相符
    //
    // UserService 不用自己 new 一個 BCryptPasswordEncoder，
    // 只要在自己的類別裡宣告一個 PasswordEncoder 欄位，
    // Spring 就會自動把這裡做好的這一份，塞給 UserService 使用。
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}