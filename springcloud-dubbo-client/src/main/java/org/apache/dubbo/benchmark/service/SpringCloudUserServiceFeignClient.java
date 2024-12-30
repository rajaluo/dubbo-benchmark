package org.apache.dubbo.benchmark.service;

import org.apache.dubbo.benchmark.bean.Page;
import org.apache.dubbo.benchmark.bean.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "springcloud-dubbo-server", url = "http://localhost:8081/users", configuration = org.apache.dubbo.benchmark.config.OkHttpConfig.class)
public interface SpringCloudUserServiceFeignClient {
    
    @GetMapping("/exist/{email}")
    boolean existUser(@PathVariable("email") String email);

    @PostMapping("/create")
    boolean createUser(@RequestBody User user);

    @GetMapping("/get/{id}")
    User getUser(@PathVariable("id") Long id);

    @GetMapping("/list/{pageNo}")
    Page<User> listUser(@PathVariable("pageNo") int pageNo);
} 