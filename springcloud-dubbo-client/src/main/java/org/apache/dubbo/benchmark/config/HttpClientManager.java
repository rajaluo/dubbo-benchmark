package org.apache.dubbo.benchmark.config;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class HttpClientManager implements DisposableBean {
    private final OkHttpClient okHttpClient;
    
    public HttpClientManager() {
        ConnectionPool connectionPool = new ConnectionPool(100, 60, TimeUnit.SECONDS);
        
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(200);
        dispatcher.setMaxRequestsPerHost(100);
        
        this.okHttpClient = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .dispatcher(dispatcher)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }
    
    public OkHttpClient getClient() {
        return okHttpClient;
    }
    
    @Override
    public void destroy() throws Exception {
        // 优雅关闭
        okHttpClient.dispatcher().executorService().shutdown();
        okHttpClient.connectionPool().evictAll();
        
        // 等待任务完成
        if (!okHttpClient.dispatcher().executorService().awaitTermination(5, TimeUnit.SECONDS)) {
            okHttpClient.dispatcher().executorService().shutdownNow();
        }
    }
} 