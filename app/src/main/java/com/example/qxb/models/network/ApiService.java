package com.example.qxb.models.network;

import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.entity.Diary;
import com.example.qxb.models.network.MoodChartData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/diary")
    Call<ApiResponse<Diary>> createDiary(@Body Diary diary);

    @GET("api/diaries")
    Call<ApiResponse<List<Diary>>> getDiaries(@Query("userId") Long userId);

    @PUT("api/diary")
    Call<ApiResponse<Diary>> updateDiary(@Body Diary diary);

    @DELETE("api/diary")
    Call<ApiResponse<Void>> deleteDiary(@Query("id") Long id);

    @GET("api/mood-chart")
    Call<ApiResponse<List<MoodChartData>>> getMoodChart(@Query("userId") Long userId);

    @POST("ai/chat")
    Call<ChatResponse> sendChatMessage(@Body ChatRequest request);

}



