package com.example.eshop.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 後台管理權限攔截器
 * 依規格書第五節第3️點：Controller 方法在執行前透過程式碼判斷 Session 中角色是否符合需求，
 * 若角色不符，導向「權限不足」頁面（403）。
 * 這裡用攔截器統一擋在 /products、/categories 之前，不用逐一修改每個 Controller 方法。
 */
public class AdminRoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        if (session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        if (!"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/403");
            return false;
        }

        return true;
    }
}