package org.example.emotionbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.emotionbackend.dto.LoginDTO;
import org.example.emotionbackend.dto.RegisterDTO;
import org.example.emotionbackend.entity.User;

import org.example.emotionbackend.dto.UserUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService extends IService<User> {
    /**
     * 用户注册
     */
    User register(RegisterDTO registerDTO);

    /**
     * 用户登录，返回 Token
     */
    String login(LoginDTO loginDTO);

    /**
     * 初始化 Root 用户
     */
    void initRootUser();

    /**
     * 更新用户信息
     */
    User updateUser(Long userId, UserUpdateDTO updateDTO);

    /**
     * 上传头像
     */
    String uploadAvatar(Long userId, MultipartFile file);
}

