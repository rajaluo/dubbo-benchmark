package org.apache.dubbo.benchmark.controller;

import org.apache.dubbo.benchmark.bean.Page;
import org.apache.dubbo.benchmark.bean.User;
import org.apache.dubbo.benchmark.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dubbotriple")
public class DubboTripleController {

    //@DubboReference(protocol = "tri", url = "tri://localhost:20881", client = "netty4")
    @DubboReference(protocol = "tri", client = "netty4")
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

