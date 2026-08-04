package com.example.eshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 購物車項目的顯示資料 (DTO)
 * 把 CartItem + Product 的資訊攤平成一個扁平物件,方便顯示或轉成 JSON。
 */
@Getter
@Setter
public class CartItemView {
    private Long id;              // CartItem 的 id（調整數量/刪除時要用）
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;  // price * quantity，後端先算好
    private Integer stock;        // 讓前端判斷還能不能繼續加數量
}
