package org.example.emotionbackend.controller;

import org.example.emotionbackend.common.Result;
import org.example.emotionbackend.dto.LoginDTO;
import org.example.emotionbackend.dto.RegisterDTO;
import org.example.emotionbackend.dto.UserUpdateDTO;
import org.example.emotionbackend.entity.User;
import org.example.emotionbackend.service.IUserService;
import org.example.emotionbackend.utils.UserContext;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IUserService userService;
    /**
     * 测试接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("UserController is OK");
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterDTO dto) {
        User user = userService.register(dto);
        return Result.success(user);
    }

    /**
     * 登录：返回token字符串
     */
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginDTO dto) {
        String token = userService.login(dto);
        return Result.success(token);
    }

    /**
     * 修改用户
     */
    @PostMapping("/update")
    public Result<User> update(@Valid @RequestBody UserUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        User updated = userService.updateUser(userId, dto);
        return Result.success(updated);
    }


}
