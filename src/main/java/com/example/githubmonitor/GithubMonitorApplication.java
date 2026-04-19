package com.example.githubmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // <--- Add this
public class GithubMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(GithubMonitorApplication.class, args);
    }
}