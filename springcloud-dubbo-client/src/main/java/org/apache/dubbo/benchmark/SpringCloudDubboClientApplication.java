package org.apache.dubbo.benchmark;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "org.apache.dubbo.benchmark.service")
@EnableDubbo
public class SpringCloudDubboClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudDubboClientApplication.class, args);
    }
} 