package org.apache.dubbo.benchmark.controller;

import org.apache.dubbo.benchmark.bean.Page;
import org.apache.dubbo.benchmark.bean.User;
import org.apache.dubbo.benchmark.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dubborest")
public class DubboRestController {

    @DubboReference(protocol = "rest")
    //@DubboReference(protocol = "rest",  check = true)
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


