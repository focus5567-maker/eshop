package com.example.eshop.controller;

import com.example.eshop.dto.ProductSearchResult;
import com.example.eshop.mapper.ProductSearchMapper;
import com.example.eshop.service.CategoryService;
import com.example.eshop.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品搜尋 API
 * 專門給 Vue 前台呼叫，複用既有的 ProductSearchMapper（MyBatis），不重寫查詢邏輯。
 */
@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductSearchMapper productSearchMapper;
    private final CategoryService categoryService;

    @GetMapping("/products")
    public List<ProductSearchResult> search(@RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Long categoryId,
                                             @RequestParam(required = false) String sort) {
        return productSearchMapper.search(keyword, categoryId, sort);
    }

    @GetMapping("/categories")
    public List<Map<String, Object>> getCategories() {
        List<Category> categories = categoryService.findAll();
        return categories.stream()
                .map(c -> Map.<String, Object>of("id", c.getId(), "name", c.getName()))
                .collect(Collectors.toList());
    }
}