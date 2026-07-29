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
        /**
     * 會員登入驗證
     * @return 驗證成功回傳 User 物件；帳號不存在、密碼錯誤、帳號被停用都回傳 null
     */
    public User login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
        return null;  // 帳號不存在
        }
        if (user.getStatus() == 0) {
        return null;  // 帳號被停用
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return null;  // 密碼不對
        }

        // 登入成功，更新最後登入時間
        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        return user;
    }
}
