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

    @Transactional
    public OrderView checkout(Long userId, String recipientName, String recipientPhone,
                               String shippingAddress, Order.PaymentMethod paymentMethod) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("購物車是空的"));

        List<CartItem> cartItems = cartItemRepository.findByCartIdOrderById(cart.getId());
        if (cartItems.isEmpty()) {
            logger.warn("結帳失敗，購物車是空的: userId={}", userId);
            throw new IllegalStateException("購物車是空的");
        }

        List<String> insufficientItems = cartItems.stream()
                .filter(item -> item.getQuantity() > item.getProduct().getStock())
                .map(item -> item.getProduct().getName() + "（庫存剩 " + item.getProduct().getStock() + "）")
                .collect(Collectors.toList());

        if (!insufficientItems.isEmpty()) {
            String message = "以下商品庫存不足：" + String.join("、", insufficientItems);
            logger.warn("結帳失敗，庫存不足: userId={}, 詳情={}", userId, message);
            throw new IllegalStateException(message);
        }

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
            orderItem.setPrice(product.getPrice());
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setSubtotal(subtotal);

            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(subtotal);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        logger.info("結帳成功: userId={}, orderId={}, 總金額={}", userId, savedOrder.getId(), totalAmount);

        return toView(savedOrder);
    }

    public List<OrderView> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId).stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    /**
     * 取消訂單：只有本人的訂單、且狀態為 PENDING 或 PAID 才能取消。
     * 取消時把當初扣掉的庫存加回去，避免庫存憑空消失。
     */
    @Transactional
    public OrderView cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("查無此訂單"));

        if (!order.getUser().getId().equals(userId)) {
            logger.warn("取消訂單失敗，非本人訂單: userId={}, orderId={}", userId, orderId);
            throw new IllegalArgumentException("查無此訂單");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING && order.getStatus() != Order.OrderStatus.PAID) {
            logger.warn("取消訂單失敗，狀態不允許取消: orderId={}, status={}", orderId, order.getStatus());
            throw new IllegalStateException("此訂單目前狀態無法取消");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(Order.OrderStatus.CANCELED);
        order.setCanceledAt(LocalDateTime.now());
        Order saved = orderRepository.save(order);

        logger.info("取消訂單成功: userId={}, orderId={}", userId, orderId);
        return toView(saved);
    }

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