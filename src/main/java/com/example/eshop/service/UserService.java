package com.example.eshop.service;

import com.example.eshop.entity.User;
import com.example.eshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

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
        logger.info("新會員註冊: username={}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        logger.info("註冊成功: userId={}", saved.getId());
        return saved;
    }

    public User login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            logger.warn("登入失敗，帳號不存在: username={}", username);
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

    /** 會員管理：查詢所有會員，依 id 排序確保順序穩定 */
    public List<User> findAll() {
        return userRepository.findAllByOrderById();
    }

    /**
     * 會員管理：切換角色（USER ↔ ADMIN）
     * @param targetUserId 要被修改的會員 id
     * @param currentAdminId 目前操作的管理員 id，用來擋下「自己改自己」
     */
    public void updateRole(Long targetUserId, Long currentAdminId, String newRole) {
        if (targetUserId.equals(currentAdminId)) {
            logger.warn("拒絕操作：管理員嘗試修改自己的角色, userId={}", targetUserId);
            throw new IllegalStateException("不能修改自己的角色");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("查無此會員"));

        user.setRole(newRole);
        userRepository.save(user);
        logger.info("角色已更新: userId={}, newRole={}, 操作者={}", targetUserId, newRole, currentAdminId);
    }
}