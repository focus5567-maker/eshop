package com.example.eshop.repository;

import com.example.eshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /** 查詢某台購物車裡的所有商品項目，依 id 排序，確保順序穩定不會因為更新而跳動 */
    List<CartItem> findByCartIdOrderById(Long cartId);

    /** 檢查某台購物車裡，是否已經有這個商品（用來判斷加入時是新增還是累加） */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
