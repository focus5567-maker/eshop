package com.example.eshop.service;

import com.example.eshop.dto.CartItemView;
import com.example.eshop.entity.Cart;
import com.example.eshop.entity.CartItem;
import com.example.eshop.entity.Product;
import com.example.eshop.entity.User;
import com.example.eshop.repository.CartItemRepository;
import com.example.eshop.repository.CartRepository;
import com.example.eshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * 取得使用者的購物車，如果還沒有就自動建立一台新的。
     * 之後每個操作都會先呼叫這個方法。
     */
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    User userRef = new User();
                    userRef.setId(userId);
                    newCart.setUser(userRef);
                    logger.info("為使用者建立新購物車: userId={}", userId);
                    return cartRepository.save(newCart);
                });
    }

    /** 查詢購物車內容，轉成顯示用的格式 */
    public List<CartItemView> getCartItems(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cartItemRepository.findByCartId(cart.getId()).stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    /** 加入購物車：已存在就累加數量，不存在就新增一筆 */
    public CartItemView addItem(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        int newQuantity = item.getQuantity() + quantity;
        if (newQuantity > product.getStock()) {
            logger.warn("加入購物車失敗，庫存不足: productId={}, 要求數量={}, 庫存={}",
                    productId, newQuantity, product.getStock());
            throw new IllegalStateException("庫存不足");
        }

        item.setQuantity(newQuantity);
        CartItem saved = cartItemRepository.save(item);
        logger.info("加入購物車: userId={}, productId={}, 數量={}", userId, productId, newQuantity);
        return toView(saved);
    }

    /** 調整數量（+/- 按鈕會呼叫這個） */
    public CartItemView updateQuantity(Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("購物車項目不存在"));

        if (quantity > item.getProduct().getStock()) {
            logger.warn("調整數量失敗，庫存不足: cartItemId={}, 要求數量={}", cartItemId, quantity);
            throw new IllegalStateException("庫存不足");
        }
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            logger.info("數量歸零，移除購物車項目: cartItemId={}", cartItemId);
            return null;
        }

        item.setQuantity(quantity);
        CartItem saved = cartItemRepository.save(item);
        return toView(saved);
    }

    /** 移除商品 */
    public void removeItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
        logger.info("移除購物車項目: cartItemId={}", cartItemId);
    }

    /** Entity → DTO 轉換，集中寫在這裡，避免各方法重複邏輯 */
    private CartItemView toView(CartItem item) {
        CartItemView view = new CartItemView();
        view.setId(item.getId());
        view.setProductId(item.getProduct().getId());
        view.setProductName(item.getProduct().getName());
        view.setPrice(item.getProduct().getPrice());
        view.setQuantity(item.getQuantity());
        view.setSubtotal(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        view.setStock(item.getProduct().getStock());
        return view;
    }
}
