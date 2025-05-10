package com.sw.yutnori;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.sw.yutnori.domain")
public class YutnoriApplication {
    public static void main(String[] args) {
        SpringApplication.run(YutnoriApplication.class, args);
    }
}
