package com.example.eshop.service;

import com.example.eshop.dto.OrderItemView;
import com.example.eshop.dto.OrderView;
import com.example.eshop.entity.*;
import com.example.eshop.repository.CartItemRepository;
import com.example.eshop.repository.CartRepository;
import com.example.eshop.repository.OrderRepository;
import com.example.eshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    /**
     * 結帳：把購物車內容轉成一張訂單。
     * 整個方法用 @Transactional 包起來：中途任何一步失敗，前面做的變更全部復原，
     * 不會出現「扣了庫存但沒建立訂單」這種資料不一致的情況。
     */
    @Transactional
    public OrderView checkout(Long userId, String recipientName, String recipientPhone,
                               String shippingAddress, Order.PaymentMethod paymentMethod) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("購物車是空的"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            logger.warn("結帳失敗，購物車是空的: userId={}", userId);
            throw new IllegalStateException("購物車是空的");
        }

        // 選項 A：整單擋下 —— 先檢查「所有」項目的庫存是否足夠，
        // 只要有一項不夠，整張訂單都不建立，購物車內容維持不變。
        List<String> insufficientItems = cartItems.stream()
                .filter(item -> item.getQuantity() > item.getProduct().getStock())
                .map(item -> item.getProduct().getName() + "（庫存剩 " + item.getProduct().getStock() + "）")
                .collect(Collectors.toList());

        if (!insufficientItems.isEmpty()) {
            String message = "以下商品庫存不足：" + String.join("、", insufficientItems);
            logger.warn("結帳失敗，庫存不足: userId={}, 詳情={}", userId, message);
            throw new IllegalStateException(message);
        }

        // 所有庫存都足夠，開始建立訂單
        Order order = new Order();
        User userRef = new User();
        userRef.setId(userId);
        order.setUser(userRef);
        order.setRecipientName(recipientName);
        order.setRecipientPhone(recipientPhone);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());  // 鎖定下單當下的價格
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setSubtotal(subtotal);

            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(subtotal);

            // 扣庫存
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);  // cascade=ALL，items 會一併存檔

        // 清空購物車（訂單建立成功後，購物車項目已經沒有用了）
        cartItemRepository.deleteAll(cartItems);

        logger.info("結帳成功: userId={}, orderId={}, 總金額={}", userId, savedOrder.getId(), totalAmount);

        return toView(savedOrder);
    }

    /** 查詢某個會員的訂單清單 */
    public List<OrderView> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId).stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    /** Entity → DTO 轉換 */
    private OrderView toView(Order order) {
        OrderView view = new OrderView();
        view.setId(order.getId());
        view.setTotalAmount(order.getTotalAmount());
        view.setStatus(order.getStatus().name());
        view.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null);
        view.setRecipientName(order.getRecipientName());
        view.setRecipientPhone(order.getRecipientPhone());
        view.setShippingAddress(order.getShippingAddress());
        view.setOrderDate(order.getOrderDate());

        List<OrderItemView> itemViews = order.getItems().stream()
                .map(item -> {
                    OrderItemView itemView = new OrderItemView();
                    itemView.setProductId(item.getProduct().getId());
                    itemView.setProductName(item.getProduct().getName());
                    itemView.setQuantity(item.getQuantity());
                    itemView.setPrice(item.getPrice());
                    itemView.setSubtotal(item.getSubtotal());
                    return itemView;
                })
                .collect(Collectors.toList());
        view.setItems(itemViews);

        return view;
    }
}