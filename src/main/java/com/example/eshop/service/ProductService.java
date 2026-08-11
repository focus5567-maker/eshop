package com.example.eshop.service;

import com.example.eshop.entity.Product;
import com.example.eshop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // 圖片實際存放的資料夾（專案內的 static/uploads），這個路徑底下的檔案，
    // 因為在 static 資料夾裡，瀏覽器可以直接透過網址存取
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    /**
     * 儲存或更新商品，並且處理圖片上傳。
     * @param product 表單資料組成的商品物件
     * @param imageFile 使用者選擇的圖片檔案，可能是空的（例如編輯時沒有換圖）
     */
    public Product saveProduct(Product product, MultipartFile imageFile) {
        // 只有使用者真的選了新檔案，才處理上傳；沒選檔案就跳過，保留原本的 imageUrl
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = storeImage(imageFile);
            product.setImageUrl(imageUrl);
        } else if (product.getId() != null) {
            // 編輯模式且沒有換圖：要保留資料庫裡原本的 imageUrl，
            // 因為表單傳過來的 product 物件，imageUrl 欄位是空的（表單沒有這個欄位的值）
            Product existing = productRepository.findById(product.getId()).orElse(null);
            if (existing != null) {
                product.setImageUrl(existing.getImageUrl());
            }
        }

        return productRepository.save(product);
    }

    /** 把上傳的檔案實際存進硬碟，回傳可以被瀏覽器存取的網址 */
    private String storeImage(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 用 UUID 當檔名前綴，避免不同商品上傳同名檔案互相覆蓋
            String originalFilename = file.getOriginalFilename();
            String newFilename = UUID.randomUUID().toString() + "-" + originalFilename;

            Path targetPath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 回傳的網址，對應 static 資料夾的網址規則：/uploads/檔名
            return "/uploads/" + newFilename;
        } catch (IOException e) {
            throw new RuntimeException("圖片上傳失敗", e);
        }
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
    }
}