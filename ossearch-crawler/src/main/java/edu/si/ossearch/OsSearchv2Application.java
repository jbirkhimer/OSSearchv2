package edu.si.ossearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
//@EnableAdminServer
public class OsSearchv2Application {

    public static void main(String[] args) {
        SpringApplication.run(OsSearchv2Application.class, args);
    }

}
