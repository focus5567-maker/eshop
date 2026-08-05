package com.example.eshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemView {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;      // 下單當下鎖定的單價
    private BigDecimal subtotal;
}
