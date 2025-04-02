package edu.si.ossearch.config;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * http trace and the actuator functionality has been removed by default in Spring Boot 2.2.0
 * this class is needed to enabled it
 * @author jbirkhimer
 */
@Configuration
public class HttpTraceActuatorConfiguration {
    @Bean
    public HttpExchangeRepository httpTraceRepository() {
        return new InMemoryHttpExchangeRepository();
    }
}
