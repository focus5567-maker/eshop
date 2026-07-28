package com.example.eshop.config;

import com.example.eshop.entity.Category;
import com.example.eshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CategoryRepository categoryRepository;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, Category>() {
            @Override
            public Category convert(String id) {
                if (id == null || id.isBlank()) return null;
                return categoryRepository.findById(Long.parseLong(id)).orElse(null);
            }
        });
    }
}