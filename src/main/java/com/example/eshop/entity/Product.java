package com.example.eshop.entity;

// jakarta.persistence.* 是 JPA 的核心套件，
// 裡面的 @Entity、@Id、@Column、@ManyToOne 這些註解都來自這裡，
// 是 JPA 用來「認識」這個類別要對應資料庫哪張表、哪些欄位的依據。
import jakarta.persistence.*;

// Lombok 提供的工具，自動幫你產生 getter/setter 方法（例如 getName()、setName()），
// 不用自己一個一個手寫，編譯的時候會自動補上。
import lombok.Getter;
import lombok.Setter;

// Hibernate（JPA 的實作框架）提供的註解，
// 讓 createdAt、updatedAt 這兩個欄位能自動被填值，不用自己手動設定時間。
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

// BigDecimal：Java 專門用來處理「精確金額」的型態，
// 不用 double/float 是因為那兩種型態做小數運算時會有誤差，不適合存錢。
import java.math.BigDecimal;

// LocalDateTime：Java 8 之後標準的日期時間型態，用來存 createdAt、updatedAt。
import java.time.LocalDateTime;

// 這是「商品」對應到資料庫 products 表的物件，純粹是資料的容器，本身不做任何邏輯判斷。
//
// 誰會用到這個類別，以及「用它做什麼」：
// → ProductRepository.java
//   靠這個類別上的 @Table(name="products")、@Id 這些註解，
//   知道要對應資料庫的哪張表、哪個欄位是主鍵，才能自動組出 SQL。
// → ProductService.java
//   把這個物件當作參數傳來傳去，在裡面做業務邏輯判斷（例如要不要處理圖片上傳），
//   判斷完之後，才呼叫 ProductRepository.save() 真正把它存進資料庫
//   （Service 自己不會直接碰資料庫，是把物件「轉交」給 Repository 去做）。
// → ProductController.java
//   表單送出的資料（name=電視&price=8000&stock=5...這種格式），
//   會被 Spring 依照「表單欄位名稱」對應「這個類別的屬性名稱」，
//   自動呼叫對應的 setName()、setPrice()、setStock() 組裝成一個完整的 Product 物件，
//   不用自己一個一個手動賦值。
// → products/form.html、list.html
//   Thymeleaf 樣板直接用 th:text="${p.name}" 這種寫法，讀取這個物件的欄位顯示在畫面上。
@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 跟 Category 是「多對一」關聯：很多商品可以屬於同一個分類。
    // 資料庫裡實際存的是 category_id 這個外鍵欄位，
    // 但 Java 這邊透過這個關聯設定，讓你可以直接寫 product.getCategory().getName()
    // 拿到完整的分類物件，不用自己再手動查一次。
    //
    // LAZY 的意思：查商品的時候，不會馬上把分類資料也一起查出來（省效能），
    // 只有你真的呼叫 product.getCategory() 之後的方法時，才會額外去資料庫補查一次分類資料。
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    private String description;

    // 存的是圖片的「路徑字串」（例如 /uploads/xxx.jpg），不是圖片檔案本身，
    // 真正的圖片檔案存在 static/uploads/ 資料夾裡，這個欄位只是指向它的網址。
    private String imageUrl;

    // 1 = 上架, 0 = 下架
    @Column(nullable = false)
    private Integer status = 1;

    // 這兩個時間戳記，Hibernate 會在存檔時自動幫你填值，
    // 你自己完全不用寫 product.setCreatedAt(...) 這種程式碼。
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}