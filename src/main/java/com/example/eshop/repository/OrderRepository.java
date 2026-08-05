package com.example.eshop.repository;

import com.example.eshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /** 查詢某個會員的所有訂單，依下單時間新到舊排序（訂單查詢頁會用到） */
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
}
