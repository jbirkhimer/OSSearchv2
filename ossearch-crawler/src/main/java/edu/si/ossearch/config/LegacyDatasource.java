package edu.si.ossearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

/**
 * @author jbirkhimer
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.legacy")
public class LegacyDatasource {

    @NotNull
    private String username;
    @NotNull
    private String password;

    @NotNull
    private String url;

    private String driverClassName;


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    @Bean
    public JdbcTemplate legacyJdbcTemplate() {
        DataSource dataSource = DataSourceBuilder
                .create()
                .url(this.url)
                .username(this.username)
                .password(this.password)
                .driverClassName(this.driverClassName)
                .build();

        return new JdbcTemplate(dataSource);
    }

}
