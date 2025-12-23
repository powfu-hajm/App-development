// ApiService.java - 修正版本（匹配后端接口）
package com.example.qxb;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import okhttp3.MultipartBody;

import com.example.qxb.models.PageResponse;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.network.Diary;
import com.example.qxb.models.dto.LoginDTO;
import com.example.qxb.models.network.MoodChartData;
import com.example.qxb.models.network.RegisterRequest;
import com.example.qxb.models.test.TestPaper;
import com.example.qxb.models.test.TestPaperDetail;
import com.example.qxb.models.test.TestResult;
import com.example.qxb.models.test.TestSubmitRequest;
import com.example.qxb.models.UserUpdateDTO;
import com.example.qxb.models.User;
import com.example.qxb.models.Article;

import com.google.gson.annotations.SerializedName;

public interface ApiService {

    // ========== 用户相关接口 ==========
    @POST("user/login")
    Call<ApiResponse<String>> login(@Body LoginDTO dto);

    @POST("user/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest request);

    @POST("user/update")
    Call<ApiResponse<User>> updateUser(@Body UserUpdateDTO request);

    @Multipart
    @POST("user/avatar")
    Call<ApiResponse<String>> uploadAvatar(@Part MultipartBody.Part file);

    @GET("user/info")
    Call<ApiResponse<User>> getUserInfo();

    // ========== 新增：账户注销接口 ==========
    @POST("user/delete")
    Call<ApiResponse<Boolean>> deleteAccount();

    // ========== 日记相关接口 ==========
    // 创建日记 - 与后端完全匹配
    @POST("diary")
    Call<ApiResponse<Diary>> createDiary(@Body Diary diary);

    // 获取日记列表 - 注意后端是/diaries不是/diary/list，且不需要传递userId参数
    @GET("diaries")
    Call<ApiResponse<List<Diary>>> getDiaries();

    // 更新日记
    @PUT("diary")
    Call<ApiResponse<Diary>> updateDiary(@Body Diary diary);

    // 删除日记 - 注意后端使用@RequestParam，所以需要用@Query传递id
    @DELETE("diary")
    Call<ApiResponse<Void>> deleteDiary(@Query("id") Long id);

    // 获取情绪图表数据 - 注意后端是/mood-chart不是/diary/mood-chart
    @GET("mood-chart")
    Call<ApiResponse<List<MoodChartData>>> getMoodChart();

    // 获取单个日记详情 - 后端有这个方法
    @GET("diary")
    Call<ApiResponse<Diary>> getDiary(@Query("id") Long id);

    // ========== 心理测试相关接口 ==========
    @GET("test/papers")
    Call<ApiResponse<List<TestPaper>>> getTestPapers();

    @GET("test/paper/{id}")
    Call<ApiResponse<TestPaperDetail>> getTestPaperDetail(@Path("id") Long paperId);

    @POST("test/submit")
    Call<ApiResponse<TestResult>> submitTest(@Body TestSubmitRequest request);

    // 获取测试历史 - 可能需要移除userId参数，由后端从token获取
    @GET("test/history")
    Call<ApiResponse<List<TestResult>>> getTestHistory();

    // ========== AI 对话相关接口 ==========
    @POST("ai/chat")
    Call<ChatResponse> chatWithAI(@Body ChatRequest request);

    @GET("ai/history")
    Call<ApiResponse<List<ChatHistoryMessage>>> getChatHistory();

    @GET("ai/test")
    Call<String> testAI();

    // 在ApiService.java中修改文章接口部分
    @GET("article/list")
    Call<ApiResponse<List<Article>>> getArticles();

    // 修改分页接口返回类型为PageResponse
    @GET("article/page")
    Call<ApiResponse<PageResponse<Article>>> getArticlePage(
            @Query("page") int page,
            @Query("size") int size,
            @Query("typeId") Integer typeId
    );

    // 添加Article单条查询接口
    @GET("article/{id}")
    Call<ApiResponse<Article>> getArticleDetail(@Path("id") Long id);
}

// ========== DTO Classes ==========

class ChatRequest {
    private String message;

    public ChatRequest() {}

    public ChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

class ChatResponse {
    @SerializedName("reply")
    private String reply;

    @SerializedName("success")
    private boolean success;

    @SerializedName("error")
    private String error;

    public ChatResponse() {}

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

class ChatHistoryMessage {
    private String role;
    private String content;
    private String createTime;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}