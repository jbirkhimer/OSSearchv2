package edu.si.ossearch.scheduler.config;

import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


/**
 * @author jbirkhimer
 */
//@Configuration
//public class QuartzConfiguration {
//
//    @Bean
//    @QuartzDataSource
//    @ConfigurationProperties(prefix = "spring.datasource.quartz")
//    public DataSource quartzDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//}
