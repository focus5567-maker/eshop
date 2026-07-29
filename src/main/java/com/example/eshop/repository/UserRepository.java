package com.example.eshop.repository;

import com.example.eshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 登入時用帳號查使用者
    Optional<User> findByUsername(String username);

    // 註冊時檢查帳號、Email 有沒有被別人用過
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}