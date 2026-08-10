package com.example.eshop.controller;

import com.example.eshop.dto.ProductSearchResult;
import com.example.eshop.entity.Category;
import com.example.eshop.service.CategoryService;
import com.example.eshop.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductSearchService productSearchService;
    private final CategoryService categoryService;

    @GetMapping("/products")
    public List<ProductSearchResult> search(@RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Long categoryId,
                                             @RequestParam(required = false) String sort) {
        return productSearchService.search(keyword, categoryId, sort);
    }

    @GetMapping("/categories")
    public List<Map<String, Object>> getCategories() {
        List<Category> categories = categoryService.findAll();
        return categories.stream()
                .map(c -> Map.<String, Object>of("id", c.getId(), "name", c.getName()))
                .collect(Collectors.toList());
    }
}