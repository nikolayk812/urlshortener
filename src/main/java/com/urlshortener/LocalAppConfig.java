package com.urlshortener;

import com.urlshortener.service.UrlHitCountingCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:db/h2/h2.properties")
public class LocalAppConfig extends AppConfig {
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        return createHikariDataSource(url, username, password);
    }

    @Bean
    public UrlHitCountingCache cache() {
        return new UrlHitCountingCache();
    }

}
