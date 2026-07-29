package com.example.eshop.service;

import com.example.eshop.entity.User;
import com.example.eshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // 之前在 WebConfig 準備好的加密工具，這裡第一次真正用到

    /** 檢查帳號是否已被使用，註冊時用來擋重複 */
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    /** 檢查 Email 是否已被註冊過 */
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    /** 註冊新會員，密碼在存進資料庫前先加密 */
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
