package com.example.eshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 商品分類實體 (Entity)
 * 對應資料庫的 categories 資料表
 *
 * 原本商品分類是 Product 裡的一個字串欄位，
 * 為了避免打字不一致、方便統一管理分類名稱，拆成獨立的資料表。
 */
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

    // 上層分類 ID，目前先做單層分類結構，這個欄位保留但尚未使用
    private Long parentId;

    // 排序用，數字越小排越前面
    private Integer sortOrder;

    // 建立時間，由 Hibernate 自動填入，之後不可修改
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 更新時間，每次存檔 Hibernate 會自動更新
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}