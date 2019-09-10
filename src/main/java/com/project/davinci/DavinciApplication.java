package com.project.davinci;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.project.davinci.persistence")
public class DavinciApplication {

    public static void main(String[] args) {
        SpringApplication.run(DavinciApplication.class, args);
    }

}
