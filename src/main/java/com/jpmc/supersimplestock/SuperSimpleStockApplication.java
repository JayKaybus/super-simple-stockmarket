package com.jpmc.supersimplestock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SuperSimpleStockApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuperSimpleStockApplication.class, args);
        System.out.println("Welcome to Super Simple Stock Market Application");
    }

}
