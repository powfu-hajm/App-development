package com.example.qxb;

import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.entity.Diary;
import com.example.qxb.models.network.MoodChartData;
import com.example.qxb.models.test.TestPaper;
import com.example.qxb.models.test.TestPaperDetail;
import com.example.qxb.models.test.TestResult;
import com.example.qxb.models.test.TestSubmitRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("diary")
    Call<ApiResponse<Diary>> createDiary(@Body Diary diary);

    @GET("diaries")
    Call<ApiResponse<List<Diary>>> getDiaries(@Query("userId") Long userId);

    @PUT("diary")
    Call<ApiResponse<Diary>> updateDiary(@Body Diary diary);

    @DELETE("diary")
    Call<ApiResponse<Void>> deleteDiary(@Query("id") Long id);

    @GET("mood-chart")
    Call<ApiResponse<List<MoodChartData>>> getMoodChart(@Query("userId") Long userId);

    // ========== 心理测试相关接口 ==========

    @GET("test/papers")
    Call<ApiResponse<List<TestPaper>>> getTestPapers();

    @GET("test/paper/{id}")
    Call<ApiResponse<TestPaperDetail>> getTestPaperDetail(@Path("id") Long paperId);

    @POST("test/submit")
    Call<ApiResponse<TestResult>> submitTest(@Body TestSubmitRequest request);

    @GET("test/history")
    Call<ApiResponse<List<TestResult>>> getTestHistory(@Query("userId") Long userId);
}



