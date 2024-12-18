package com.hexalab.silverplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.hexalab.silverplus")
public class SilverplusApplication {

    public static void main(String[] args) {
        SpringApplication.run(SilverplusApplication.class, args);
    }

}
