package org.example.emotionbackend.service;

import org.example.emotionbackend.dto.LoginDTO;
import org.example.emotionbackend.dto.RegisterDTO;
import org.example.emotionbackend.dto.UserUpdateDTO;
import org.example.emotionbackend.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService extends IService<User> {

    User register(RegisterDTO registerDTO);

    String login(LoginDTO loginDTO);

    void initRootUser();

    User updateUser(Long userId, UserUpdateDTO updateDTO);

    String uploadAvatar(Long userId, MultipartFile file);

    // 新增：注销账户方法
    boolean deleteAccount(Long userId);
}