package org.example.emotionbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.emotionbackend.dto.LoginDTO;
import org.example.emotionbackend.dto.RegisterDTO;
import org.example.emotionbackend.entity.User;
import org.example.emotionbackend.mapper.UserMapper;
import org.example.emotionbackend.service.IUserService;
import org.example.emotionbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

import org.example.emotionbackend.dto.UserUpdateDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private JwtUtils jwtUtils;

    // 上传目录
    private static final String UPLOAD_DIR = "uploads/avatars/";

    @Override
    public User register(RegisterDTO registerDTO) {
        // 1. 检查用户名是否存在
        Long count = this.lambdaQuery()
                .eq(User::getUsername, registerDTO.getUsername())
                .count();
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        // 简单加密：MD5
        user.setPassword(encryptPassword(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : "新用户");
        
        this.save(user);
        return user;
    }

    @Override
    public String login(LoginDTO loginDTO) {
        // 1. 查找用户
        User user = this.lambdaQuery()
                .eq(User::getUsername, loginDTO.getUsername())
                .one();
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 校验密码
        String inputPassword = encryptPassword(loginDTO.getPassword());
        if (!inputPassword.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 3. 生成 Token
        return jwtUtils.generateToken(user.getId(), user.getUsername());
    }

    @Override
    @PostConstruct // 启动时自动执行
    public void initRootUser() {
        try {
            Long count = this.lambdaQuery()
                    .eq(User::getUsername, "root")
                    .count();
            
            if (count == 0) {
                log.info("检测到 root 用户不存在，正在自动创建...");
                User root = new User();
                root.setUsername("root");
                root.setPassword(encryptPassword("root")); // 密码也是 root
                root.setNickname("超级管理员");
                this.save(root);
                log.info("Root 用户创建成功！账号: root, 密码: root");
            }
        } catch (Exception e) {
            log.error("初始化 Root 用户失败", e);
        }
    }

    @Override
    public User updateUser(Long userId, UserUpdateDTO updateDTO) {
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 修改昵称
        if (StringUtils.hasText(updateDTO.getNickname())) {
            user.setNickname(updateDTO.getNickname());
        }
        
        // 修改手机号
        if (StringUtils.hasText(updateDTO.getPhone())) {
            user.setPhone(updateDTO.getPhone());
        }

        // 修改密码
        if (StringUtils.hasText(updateDTO.getNewPassword())) {
            // 如果要修改密码，必须验证旧密码（除非是管理员重置，这里假设是用户自己改）
            if (!StringUtils.hasText(updateDTO.getOldPassword())) {
                throw new RuntimeException("修改密码需要提供旧密码");
            }
            String oldEncrypted = encryptPassword(updateDTO.getOldPassword());
            if (!oldEncrypted.equals(user.getPassword())) {
                throw new RuntimeException("旧密码错误");
            }
            user.setPassword(encryptPassword(updateDTO.getNewPassword()));
        }

        this.updateById(user);
        // 为了安全，返回的User不应包含密码，这里简化处理，Controller层最好转VO
        user.setPassword(null);
        return user;
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }

        // 1. 准备目录
        String projectPath = System.getProperty("user.dir");
        File dir = new File(projectPath, UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2. 生成文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + suffix;

        // 3. 保存文件
        try {
            File dest = new File(dir, fileName);
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("上传头像失败", e);
            throw new RuntimeException("上传头像失败: " + e.getMessage());
        }

        // 4. 更新数据库
        String avatarUrl = "/uploads/avatars/" + fileName;
        User user = this.getById(userId);
        if (user != null) {
            user.setAvatar(avatarUrl);
            this.updateById(user);
        }

        return avatarUrl;
    }

    private String encryptPassword(String password) {
        // 简单的 MD5 加密，生产环境建议用 BCrypt
        return DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
    }
}

