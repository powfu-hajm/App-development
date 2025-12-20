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
@RequestMapping("/user")
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
        log.info("注册请求: username={}, nickname={}", dto.getUsername(), dto.getNickname());
        User user = userService.register(dto);
        return Result.success(user);
    }

    /**
     * 登录：返回token字符串
     */
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginDTO dto) {
        log.info("登录请求: username={}", dto.getUsername());
        String token = userService.login(dto);
        return Result.success(token);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("未登录");
        }
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 隐藏密码
        user.setPassword(null);
        return Result.success(user);
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

    /**
     * 注销账户
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteAccount() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("未登录");
        }

        boolean result = userService.deleteAccount(userId);
        if (result) {
            log.info("用户ID {} 已成功注销账户", userId);
            return Result.success(true);
        } else {
            return Result.error("账户注销失败");
        }
    }
}