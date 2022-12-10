package edu.si.ossearch;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAdminServer
public class OSSearchv2Application {

    public static void main(String[] args) {
        SpringApplication.run(OSSearchv2Application.class, args);
    }

}
