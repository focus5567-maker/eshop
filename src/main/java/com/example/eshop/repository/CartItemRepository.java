package com.example.eshop.repository;

import com.example.eshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /** 查詢某台購物車裡的所有商品項目 */
    List<CartItem> findByCartId(Long cartId);

    /** 檢查某台購物車裡，是否已經有這個商品（用來判斷加入購物車時要新增還是累加數量） */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
