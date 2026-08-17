eshop

全端電商平台，個人練習專案。後台管理與登入註冊採 Thymeleaf 傳統渲染，消費者前台（商品搜尋、購物車、結帳、訂單）採 Vue 3 + Vue Router 建置 SPA，透過 RESTful API 串接前後端。


使用技術
1. 後端：Java 17、Spring Boot 4.1.0
2. 資料存取：Spring Data JPA/Hibernate（主要 CRUD）、MyBatis（商品動態搜尋）
3. 認證機制：不使用 Spring Security，手刻 HttpSession；角色權限用自訂 Interceptor
4. 前端：Thymeleaf + Bootstrap 5.1.3（後台/會員）、Vue 3 + Vue Router 4 + Vite（消費者前台）
5. 資料庫：PostgreSQL（Hibernate ddl-auto: update 自動維護表結構，無 Flyway/Liquibase）
6. 部署：Docker Compose（web-app + postgres-db）


功能
1. 會員註冊/登入（BCrypt 加密、HttpSession 認證）
2. 商品管理（CRUD、圖片上傳、關鍵字搜尋）
3. 分類管理
4. 商品前台瀏覽搜尋（關鍵字、分類、排序）
5. 購物車（加入/調整數量/移除）
6. 結帳與訂單（建立、查詢、取消，含庫存與價格鎖定機制）
7. 後台角色權限控管（Interceptor 攔截，ADMIN 限定）
8. 會員管理（角色切換）


架構
使用者
 ├─ 登入/註冊/後台管理 → Thymeleaf（整頁渲染）
 └─ 商品搜尋/購物車/結帳/訂單 → Vue Router SPA
        ↓ REST API
   Spring Boot Controller → Service → JPA / MyBatis → PostgreSQL


已知限制
未實作 CSRF 防護、修改密碼、地址簿、金流串接、發票功能
訂單狀態僅支援建立/取消，尚無出貨/完成的後台管理
圖片上傳存於本機路徑，僅適用開發環境
