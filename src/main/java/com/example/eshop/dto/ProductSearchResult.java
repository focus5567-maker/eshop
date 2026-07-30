package com.example.eshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 商品搜尋結果的資料載體 (DTO)
 *
 * 為什麼不直接用 Product Entity：
 * Product.category 是關聯物件（@ManyToOne），那是 JPA/Hibernate 的機制。
 * MyBatis 沒有「自動關聯查詢」這回事，我們直接用 SQL JOIN 把分類名稱一起查出來，
 * 所以需要一個「扁平」的資料結構來裝結果，而不是套用原本的 Entity 結構。
 */
@Getter
@Setter
public class ProductSearchResult {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String imageUrl;
    private String categoryName;  // 直接是名稱字串，不是 Category 物件
}
