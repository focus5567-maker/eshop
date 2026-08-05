package com.example.eshop.controller;

import com.example.eshop.dto.OrderView;
import com.example.eshop.entity.Order;
import com.example.eshop.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    /** 結帳：把購物車轉成訂單 */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(HttpSession session, @RequestBody Map<String, Object> body) {
        Long userId = getUserIdOrNull(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "尚未登入"));
        }

        String recipientName = (String) body.get("recipientName");
        String recipientPhone = (String) body.get("recipientPhone");
        String shippingAddress = (String) body.get("shippingAddress");
        String paymentMethodStr = (String) body.get("paymentMethod");

        if (recipientName == null || recipientName.isBlank()
                || recipientPhone == null || recipientPhone.isBlank()
                || shippingAddress == null || shippingAddress.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "收件資訊不完整"));
        }

        Order.PaymentMethod paymentMethod;
        try {
            paymentMethod = Order.PaymentMethod.valueOf(paymentMethodStr);
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "付款方式不正確"));
        }

        try {
            OrderView result = orderService.checkout(userId, recipientName, recipientPhone, shippingAddress, paymentMethod);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** 查詢自己的訂單清單 */
    @GetMapping
    public ResponseEntity<?> getMyOrders(HttpSession session) {
        Long userId = getUserIdOrNull(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "尚未登入"));
        }

        List<OrderView> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    /** 從 Session 取出使用者 ID，沒有就回傳 null，不丟例外（跟 CartApiController 同樣的寫法） */
    private Long getUserIdOrNull(HttpSession session) {
        Object userId = session.getAttribute("userId");
        return userId == null ? null : (Long) userId;
    }
}