package org.apache.dubbo.benchmark.config;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {

    @Bean
    @Primary
    public OkHttpClient okHttpClient() {
        // 创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            16,  // 核心线程数
            64,  // 最大线程数
            60L, // 空闲线程存活时间
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true); // 设置为守护线程
                return thread;
            }
        );

        // 配置Dispatcher
        Dispatcher dispatcher = new Dispatcher(executor);
        dispatcher.setMaxRequests(64);           // 最大并发请求数
        dispatcher.setMaxRequestsPerHost(32);     // 每个主机的最大并发请求数

        // 配置连接池
        ConnectionPool connectionPool = new ConnectionPool(
            32,  // 最大空闲连接数
            5,   // 空闲连接存活时间
            TimeUnit.MINUTES
        );

        return new OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectionPool(connectionPool)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    }

    @Bean
    public feign.okhttp.OkHttpClient feignOkHttpClient(OkHttpClient okHttpClient) {
        return new feign.okhttp.OkHttpClient(okHttpClient);
    }
} 