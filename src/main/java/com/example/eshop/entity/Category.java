package com.example.eshop.entity;

// jakarta.persistence.* 是 JPA 的核心套件，
// @Entity、@Id、@Column 這些註解都來自這裡，讓這個類別知道要對應資料庫哪張表、哪些欄位。
import jakarta.persistence.*;

// Lombok 提供，自動幫你產生 getter/setter 方法，不用自己手寫。
import lombok.Getter;
import lombok.Setter;

// Hibernate 提供的註解，讓 createdAt、updatedAt 自動被填值，不用自己手動設定時間。
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

// LocalDateTime：Java 標準的日期時間型態，用來存 createdAt、updatedAt。
import java.time.LocalDateTime;

// 這是「商品分類」對應到資料庫 categories 表的物件，純粹是資料的容器。
//
// 誰會用到這個類別：
// → CategoryRepository.java（靠 @Table、@Id 這些註解知道要對應哪張表）
// → CategoryService.java（新增/查詢/刪除分類時，經手的就是這個物件，
//   判斷完之後轉交給 CategoryRepository 真正存進資料庫）
// → CategoryController.java（表單送出的資料，被 Spring 自動組裝成這個物件）
// → categories/list.html、form.html（Thymeleaf 直接讀取欄位顯示在畫面上）
// → Product.java（@ManyToOne 關聯這個類別，商品透過這裡拿到自己所屬的分類名稱）
// → WebConfig.java（自訂的 Converter，把表單送來的分類 id 字串轉成這個物件）
@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 上層分類 id，預留給「階層式分類」用（例如電器 → 冰箱 → 單門冰箱這種樹狀結構），
    // 目前整個專案還沒有任何地方真正使用這個欄位。
    private Long parentId;

    // 排序用，數字越小排越前面，
    // !!! 依照規格書建立，目前有這個欄位但沒有任何地方真正拿它來排序（CategoryController 沒有 ORDER BY 邏輯），
    // 填了數字目前不會影響畫面顯示順序。
    private Integer sortOrder;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // updatedAt：這筆資料「最後一次被修改」的時間。
    //
    // @UpdateTimestamp：Hibernate 提供的註解，
    // 只要這筆資料被 UPDATE（不管改哪個欄位），
    // Hibernate 會在存檔前自動把目前時間填進這個欄位，你自己完全不用手動設定。
    //
    // 跟 createdAt 的差異：
    // createdAt 只在第一次新增時被填值，之後永遠不會變；
    // updatedAt 每次被修改都會更新，變動的時間點會反映在這裡。
    //
    // !! 這個自動更新機制，只有在透過 JPA/Hibernate 存檔（呼叫 repository.save(...)）
    // 才會生效——如果哪天改用 MyBatis 直接寫 SQL 的 UPDATE 語句，
    // 這個自動填值的機制不會發生，要自己在 SQL 裡手動寫 updated_at = NOW()
    // （這是你之前嘗試「全改 MyBatis」時，一起確認過的重要細節）
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}