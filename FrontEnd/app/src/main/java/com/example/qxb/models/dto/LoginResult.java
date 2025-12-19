package com.example.qxb.models.dto;

import com.google.gson.annotations.SerializedName;

public class LoginResult {

    @SerializedName("token")
    private String token;

    public String getToken(){
        return token;
    }
}
