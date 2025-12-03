package com.example.qxb.data.network;

import com.example.qxb.data.models.ChatRequest;
import com.example.qxb.data.models.ChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("ai/chat")
    Call<ChatResponse> sendChatMessage(@Body ChatRequest request);
}
