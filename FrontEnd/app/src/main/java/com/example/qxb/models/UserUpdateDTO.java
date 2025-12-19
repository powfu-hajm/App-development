package com.example.qxb.models;

public class UserUpdateDTO {
    private String nickname;
    private String phone;
    private String oldPassword;
    private String newPassword;

    public UserUpdateDTO(String nickname, String phone, String oldPassword, String newPassword) {
        this.nickname = nickname;
        this.phone = phone;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}



