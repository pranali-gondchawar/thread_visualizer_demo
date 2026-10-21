package com.example.threadvisualizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ThreadVisualizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThreadVisualizerApplication.class, args);
    }
}

