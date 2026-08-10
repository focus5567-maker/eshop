package com.example.eshop.service;

import com.example.eshop.dto.ProductSearchResult;
import com.example.eshop.mapper.ProductSearchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchMapper productSearchMapper;

    public List<ProductSearchResult> search(String keyword, Long categoryId, String sort) {
        return productSearchMapper.search(keyword, categoryId, sort);
    }
}