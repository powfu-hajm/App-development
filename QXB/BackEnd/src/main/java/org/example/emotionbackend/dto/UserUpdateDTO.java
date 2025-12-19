package org.example.emotionbackend.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String nickname;
    private String phone;
    private String oldPassword;
    private String newPassword;
}

