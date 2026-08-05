package com.example.eshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderView {
    private Long id;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private LocalDateTime orderDate;
    private List<OrderItemView> items;
}
