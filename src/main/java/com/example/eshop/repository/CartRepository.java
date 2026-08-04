package com.example.eshop.repository;

import com.example.eshop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /** 依使用者 ID 查詢他的購物車（可能不存在，因為新會員還沒建立過購物車） */
    Optional<Cart> findByUserId(Long userId);
}