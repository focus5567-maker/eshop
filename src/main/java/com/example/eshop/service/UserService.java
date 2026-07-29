package com.example.eshop.service;

import com.example.eshop.entity.User;
import com.example.eshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    // 使用 SLF4J 日誌系統，取代 System.out.println
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    public User register(User user) {
        logger.info("新會員註冊: username={}", user.getUsername());  // INFO：正常流程節點
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        logger.info("註冊成功: userId={}", saved.getId());
        return saved;
    }

    public User login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            logger.warn("登入失敗，帳號不存在: username={}", username);  // WARN：可預期的異常情況
            return null;
        }
        if (user.getStatus() == 0) {
            logger.warn("登入失敗，帳號已被停用: username={}", username);
            return null;
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            logger.warn("登入失敗，密碼錯誤: username={}", username);
            return null;
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        logger.info("登入成功: username={}", username);

        return user;
    }
}
