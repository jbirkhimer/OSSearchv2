package edu.si.ossearch.collection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


/**
 * @author jbirkhimer
 */
//@Configuration
//public class JPAConfiguration {
//
//    @Bean
//    @Primary
//    @ConfigurationProperties(prefix="spring.datasource.jpa")
//    public DataSource mysqlDataSource(){
//        return DataSourceBuilder
//                .create()
//                .build();
//    }
//}
