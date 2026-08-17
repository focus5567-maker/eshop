package com.example.eshop.config;

// SqlSessionFactory：MyBatis 的核心引擎介面，
// 你這個檔案最主要的任務，就是手動組裝出這個物件、放進 Spring 容器。
import org.apache.ibatis.session.SqlSessionFactory;

// SqlSessionFactoryBean：MyBatis 官方提供的「建造者」工具，
// 專門用來一步一步組裝出 SqlSessionFactory（設定 DataSource、Mapper XML 位置、行為設定），
// 不用自己手動處理底層那些複雜的組裝細節
//（自己組交易管理設定、自己讀取解析每個 XML 檔案的動態 SQL 語法等）。
import org.mybatis.spring.SqlSessionFactoryBean;

// @Bean：標記在方法上，告訴 Spring「這個方法的回傳值，交給你（Spring）放進容器管理」，
// 適合用在「這個物件不是我自己寫的類別，需要自己動手客製化組裝」的情況，
// 這裡就是拿它來標記 sqlSessionFactory() 這個方法。
import org.springframework.context.annotation.Bean;

// @Configuration：標記在類別上，告訴 Spring「這整個類別是設定/組態類別」，
// 啟動時要讀取裡面所有 @Bean 方法，把回傳的物件註冊進容器。
import org.springframework.context.annotation.Configuration;

// PathMatchingResourcePatternResolver：Spring 提供的工具，
// 可以用萬用字元（例如 *.xml）批次尋找檔案，
// 這裡用它去讀取 resources/mapper/ 資料夾底下所有 Mapper XML 檔案的位置。
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

// DataSource：代表「資料庫連線」的標準 Java 介面，
// 這裡的 DataSource 不是自己另外建立的，是 Spring Boot 啟動時，
// 依照 application.yml 裡的連線設定（帳號、密碼、URL）自動建立好、
// 放進容器的那一份，JPA 也是共用同一個 DataSource，兩套框架連的是同一個資料庫連線池。
import javax.sql.DataSource;

// 這個類別只做一件事：手動組裝出 MyBatis 運作需要的核心物件 SqlSessionFactory。
//
// 為什麼要手動做，不能交給 Spring Boot 自動處理：
// 這個專案同時裝了 JPA(Hibernate) 跟 MyBatis 兩套資料存取框架，
// MyBatis 的自動配置類別（MybatisAutoConfiguration）本來會自動讀取 application.yml
// 裡 mybatis.* 那些設定，自動組裝出這個物件，
// 但因為兩邊自動配置機制會互相干擾，這個自動配置常常沒辦法順利完成，
// 所以改成在這裡手動組裝，不依賴自動配置，也因此 application.yml 裡的
// mybatis.mapper-locations、mybatis.configuration.* 這些設定值目前完全沒有作用，
// 全部改成用下面這個方法裡的 Java 程式碼直接指定。
@Configuration
public class MyBatisConfig {

    // ============================================================
    // 組裝 SqlSessionFactory：介面，MyBatis 運作的核心引擎
    // ============================================================
    //
    // 誰會用到這裡組裝出來的東西：
    // → 所有標了 @Mapper 的介面（目前只有 ProductSearchMapper.java）
    //   Spring 啟動時（靠 EshopApplication.java 上的 @MapperScan），
    //   會掃描到 @Mapper 介面，並且需要靠這裡組好的 SqlSessionFactory，
    //   才能把這些介面變成「真正可以呼叫、可以執行 SQL」的物件，
    //   之後 ProductApiController.java 才能直接注入 ProductSearchMapper 來用。
    //
    // 換句話說：沒有這個方法組裝好的 SqlSessionFactory，
    // ProductSearchMapper 這個介面就只是一個空殼，完全沒辦法運作。
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {

        // SqlSessionFactoryBean：類別，MyBatis 官方提供的「建造者」工具，
        // 專門用來一步一步組裝出 SqlSessionFactory，不用自己手動 new 一個（過程很複雜）。
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        // dataSource 是 Spring Boot 啟動時，
        // 依照 application.yml 裡 spring.datasource.* 的連線設定（帳號、密碼、URL）
        // 自動建立好的物件，這部分的自動配置沒有受到 JPA/MyBatis 衝突影響，正常生效。
        // 這裡直接把它「借」過來共用，MyBatis 才知道要連去哪個資料庫，
        // 不用另外自己寫一份重複的連線設定。
        // 這個 DataSource，JPA（透過 Hibernate）也是共用同一份，兩套框架連的是同一個資料庫連線池。
        factoryBean.setDataSource(dataSource);

        // 設定去哪裡找「實際的 SQL 語句」。
        // "classpath:mapper/*.xml" 對應到專案裡的實際位置：src/main/resources/mapper/ 資料夾，
        // 目前裡面只有 ProductSearchMapper.xml 一個檔案，
        // 之後不管新增幾個 Mapper XML，只要放進這個資料夾，都會被自動抓到，不用額外註冊。
        factoryBean.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml")
        );

        // 建立 MyBatis 自己的 Configuration 物件（注意：這是 MyBatis 內部的 Configuration 類別，
        // 跟這個檔案最上面 Spring 的 @Configuration 註解是不同的東西，只是剛好同名）。
        // 用來設定 MyBatis 的行為細節。
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();

        // 開啟「底線轉駝峰」自動對應功能。
        //
        // 誰會受惠於這個設定：
        // → ProductSearchMapper.xml 裡查詢出來的欄位（例如 image_url、category_name），
        //   會自動對應到 ProductSearchResult.java（DTO）裡的 imageUrl、categoryName 屬性，
        //   不用在 SQL 裡手動寫 AS 別名一一轉換。
        // → 最終這些轉換好的資料，會被 ProductApiController.java 包成 JSON 回傳給 Vue 前端
        //   （ShopView.vue）使用，商品搜尋頁的圖片、分類名稱能不能正確顯示，
        //   全部仰賴這一行設定有沒有生效。
        //
        // 這是必須手動設定的原因：application.yml 裡原本也有寫
        // mybatis.configuration.map-underscore-to-camel-case: true 這個設定，
        // 但因為整個 SqlSessionFactory 是手動建立（不是 Spring Boot 自動建立），
        // yml 裡這個設定不會被自動讀取，必須在這裡用程式碼重新設定一次才會真正生效。
        configuration.setMapUnderscoreToCamelCase(true);

        // 把剛才設定好的 Configuration，交給建造者，讓它套用這些行為設定。
        factoryBean.setConfiguration(configuration);

        // 組裝完成，回傳最終的 SqlSessionFactory，
        // 這個回傳值會被 Spring 收進容器管理（因為上面標了 @Bean），
        // 之後 MyBatis 內部需要用到它的地方（例如把 @Mapper 介面變成可用物件時），
        // Spring 會自動提供這一份。
        return factoryBean.getObject();
    }
}