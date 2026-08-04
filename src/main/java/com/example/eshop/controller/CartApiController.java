package com.example.eshop.controller;

import com.example.eshop.dto.CartItemView;
import com.example.eshop.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCart(HttpSession session) {
        Long userId = getUserIdOrNull(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "尚未登入"));
        }
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItem(HttpSession session, @RequestBody Map<String, Object> body) {
        Long userId = getUserIdOrNull(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "尚未登入"));
        }

        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = Integer.parseInt(body.get("quantity").toString());

        try {
            CartItemView result = cartService.addItem(userId, productId, quantity);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateQuantity(HttpSession session, @PathVariable Long id, @RequestBody Map<String, Object> body) {
        if (getUserIdOrNull(session) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "尚未登入"));
        }

        int quantity = Integer.parseInt(body.get("quantity").toString());
        try {
            CartItemView result = cartService.updateQuantity(id, quantity);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> removeItem(HttpSession session, @PathVariable Long id) {
        if (getUserIdOrNull(session) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "尚未登入"));
        }
        cartService.removeItem(id);
        return ResponseEntity.noContent().build();
    }

    /** 從 Session 取出使用者 ID，沒有就回傳 null，不丟例外 */
    private Long getUserIdOrNull(HttpSession session) {
        Object userId = session.getAttribute("userId");
        return userId == null ? null : (Long) userId;
    }
}