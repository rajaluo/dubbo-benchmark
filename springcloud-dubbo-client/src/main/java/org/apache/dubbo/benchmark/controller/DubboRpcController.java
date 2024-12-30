package org.apache.dubbo.benchmark.controller;

import org.apache.dubbo.benchmark.bean.Page;
import org.apache.dubbo.benchmark.bean.User;
import org.apache.dubbo.benchmark.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dubborpc")
public class DubboRpcController {

    //@DubboReference(protocol = "dubbo", url = "dubbo://localhost:20882", client = "netty4")
    @DubboReference(protocol = "dubbo", client = "netty4")
    private UserService userService;

    @GetMapping("/exist/{email}")
    public boolean existUser(@PathVariable String email) {
        return userService.existUser(email);
    }

    @PostMapping("/create")
    public boolean createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/get/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/list/{pageNo}")
    public Page<User> listUser(@PathVariable int pageNo) {
        return userService.listUser(pageNo);
    }
} 