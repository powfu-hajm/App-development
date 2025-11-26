package com.example.mood_frontend.network;

import com.example.mood_frontend.network.model.ApiResponse;
import com.example.mood_frontend.network.model.Diary;
import com.example.mood_frontend.network.model.MoodChartData;

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
}



