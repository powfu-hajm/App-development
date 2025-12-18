package org.example.emotionbackend.controller;

import org.example.emotionbackend.common.Result;
import org.example.emotionbackend.dto.LoginDTO;
import org.example.emotionbackend.dto.RegisterDTO;
import org.example.emotionbackend.entity.User;
import org.example.emotionbackend.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import org.example.emotionbackend.dto.UserUpdateDTO;
import org.example.emotionbackend.utils.UserContext;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterDTO registerDTO) {
        try {
            User user = userService.register(registerDTO);
            return Result.success("注册成功", user);
        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        try {
            String token = userService.login(loginDTO);
            return Result.success("登录成功", token);
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<User> updateUser(@RequestBody UserUpdateDTO updateDTO) {
        try {
            Long userId = UserContext.getUserId();
            User user = userService.updateUser(userId, updateDTO);
            return Result.success("更新成功", user);
        } catch (Exception e) {
            log.error("更新失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = UserContext.getUserId();
            String avatarUrl = userService.uploadAvatar(userId, file);
            return Result.success("上传成功", avatarUrl);
        } catch (Exception e) {
            log.error("上传头像失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<User> getUserInfo() {
        try {
            Long userId = UserContext.getUserId();
            User user = userService.getById(userId);
            user.setPassword(null); // 不返回密码
            return Result.success("获取成功", user);
        } catch (Exception e) {
            return Result.error("获取用户信息失败");
        }
    }
}

