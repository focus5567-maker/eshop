package com.example.eshop.interceptor;

// HttpServletRequest：代表這次進來的請求，可以從這裡拿到網址、Session 等資訊
import jakarta.servlet.http.HttpServletRequest;

// HttpServletResponse：代表要回傳給瀏覽器的回應，這裡用它做「導向另一個頁面」這個動作
import jakarta.servlet.http.HttpServletResponse;

// HttpSession：代表這個使用者的登入狀態，靠瀏覽器帶的 Cookie 對應到伺服器這邊記住的資料
import jakarta.servlet.http.HttpSession;

// HandlerInterceptor：Spring MVC 提供的介面，
// 實作它、並且在 WebConfig.java 裡註冊後，
// 就能讓這個類別「站在請求進入 Controller 之前」，先做一次檢查
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 後台管理權限攔截器
 * 依規格書第五節第3️點：Controller 方法在執行前透過程式碼判斷 Session 中角色是否符合需求，
 * 若角色不符，導向「權限不足」頁面（403）。
 * 這裡用攔截器統一擋在 /products、/categories 之前，不用逐一修改每個 Controller 方法。
 *
 * 誰會呼叫這個類別、什麼時候被呼叫：
 * → 這個類別不是被你自己的程式碼呼叫的，
 *   是 Spring MVC 內部的 DispatcherServlet（請求處理的總指揮），
 *   在每次有請求打進「已註冊這個攔截器的網址」時，自動觸發呼叫。
 * → 哪些網址會觸發它：WebConfig.java 的 addInterceptors() 裡註冊了
 *   /products/**、/categories/**、/admin/**，
 *   只有打這幾個網址的請求，才會先經過這裡檢查。
 */
public class AdminRoleInterceptor implements HandlerInterceptor {

    // preHandle：HandlerInterceptor 介面規定一定要實作的方法，
    // 名稱、參數、回傳型態都是介面規定好的，不能自己改。
    //
    // 回傳 true：檢查通過，放行，請求繼續往下走到真正的 Controller
    //           （例如 ProductController.listProducts()）
    // 回傳 false：檢查沒過，攔下來，不會執行到 Controller
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 從這次的請求裡，拿出對應的 Session
        // （Session 是登入時，LoginController 那邊寫入的，這裡是「讀取」的一端）
        HttpSession session = request.getSession();

        // 第一關：檢查有沒有登入
        // 誰會寫入這個 userId：LoginController.login() 登入成功時
        if (session.getAttribute("userId") == null) {
            // sendRedirect：跟 Controller 回傳 "redirect:/login" 效果一樣，
            // 只是這裡是用 Java 程式碼直接呼叫 response 物件來做，
            // 因為攔截器不像 Controller 方法，沒辦法直接 return 一個 "redirect:..." 字串
            //
            // 誰會接住這個導向：LoginController 的 GET /login，顯示登入表單
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // 第二關：檢查角色是不是 ADMIN
        // 誰會寫入這個 role：LoginController.login() 登入成功時，
        // 從資料庫查出來的 User.role 欄位值（USER 或 ADMIN）
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            // 誰會接住這個導向：ErrorPageController 的 GET /403，
            // 顯示「權限不足」的提示頁面（templates/error/403.html）
            response.sendRedirect(request.getContextPath() + "/403");
            return false;
        }

        // 兩關都通過（有登入、且是 ADMIN），放行，
        // 請求會繼續往下走，真正呼叫到 ProductController、CategoryController
        // 或 AdminUserController 對應的方法
        return true;
    }
}