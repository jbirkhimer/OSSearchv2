package edu.si.ossearch.scheduler.config;

import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;


/**
 * @author jbirkhimer
 */
//@Configuration
public class QuartzConfiguration {
//    @Autowired
//    DataSource dataSource;

//    @Bean
//    @QuartzDataSource
//    @ConfigurationProperties(prefix = "spring.datasource.quartz")
//    public DataSource quartzDataSource() {
//        return DataSourceBuilder.create().build();
//    }

//    @Bean
//    @QuartzDataSource
//    public DataSource quartzDataSource() {
//        return dataSource;
//    }

    /*@Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, PlatformTransactionManager transactionManager)
            throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setQuartzProperties(quartzProperties());
        factory.setGlobalJobListeners(jobsListenerService);
        return factory;
    }*/

}
