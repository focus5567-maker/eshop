package com.example.eshop.mapper;

import com.example.eshop.dto.ProductSearchResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品搜尋 Mapper（MyBatis - XML 軌道）
 *
 * 這裡只留方法簽名，不寫任何 SQL。
 * 實際的 SQL 語句寫在 resources/mapper/ProductMapper.xml，
 * MyBatis 會依照「介面完整路徑（namespace）+ 方法名稱（id）」自動找到對應的 SQL。
 */
@Mapper
public interface ProductSearchMapper {

    List<ProductSearchResult> search(@Param("keyword") String keyword,
                                      @Param("categoryId") Long categoryId,
                                      @Param("sort") String sort);
}