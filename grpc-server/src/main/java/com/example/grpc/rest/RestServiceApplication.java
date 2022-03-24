package com.example.grpc.rest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class RestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }
}
// extends SpringBootServletInitializer
