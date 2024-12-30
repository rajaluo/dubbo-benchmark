package org.apache.dubbo.benchmark;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
@EnableFeignClients
public class SpringCloudDubboServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudDubboServerApplication.class, args);
    }
} 