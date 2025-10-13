package com.topcinnamon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyFirstApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyFirstApplication.class, args);
    }
}