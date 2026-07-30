package com.example.eshop.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MyBatis 手動設定類別
 *
 * 為什麼需要手動宣告：
 * 這個專案同時使用 JPA(Hibernate)與 MyBatis 兩套資料存取框架，
 * 兩邊的自動配置機制互相影響，導致 MyBatis 沒辦法自動產生 SqlSessionFactory。
 * 這裡改成明確告訴 Spring：SqlSessionFactory 要用哪個 DataSource、去哪裡找 Mapper XML。
 */
@Configuration
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);  // 共用 Spring Boot 已經建好的同一個 DataSource
        factoryBean.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml")
        );
        return factoryBean.getObject();
    }
}