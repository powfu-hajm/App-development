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

import com.example.qxb.model.PageResponse;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.network.PageData;
import com.example.qxb.models.network.Diary;
import com.example.qxb.models.dto.LoginDTO;
import com.example.qxb.models.dto.LoginResult;
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
    Call<ApiResponse<LoginResult>> login(@Body LoginDTO dto);

    @POST("user/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest request);

    @POST("user/update")
    Call<ApiResponse<User>> updateUser(@Body UserUpdateDTO request);

    @Multipart
    @POST("user/avatar")
    Call<ApiResponse<String>> uploadAvatar(@Part MultipartBody.Part file);

    @GET("user/info")
    Call<ApiResponse<User>> getUserInfo();

    // ========== 日记相关接口==========
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

    // ========== AI 对话相关接口 ==========
    @POST("ai/chat")
    Call<ChatResponse> chatWithAI(@Body ChatRequest request);

    @GET("ai/history")
    Call<ApiResponse<List<ChatHistoryMessage>>> getChatHistory();

    @GET("ai/test")
    Call<String> testAI();

    // ========== 文章相关接口 ==========
    @GET("article/crawl")
    Call<ApiResponse<String>> triggerCrawl();

    @GET("article/page")
    Call<PageResponse<Article>> getArticles(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("type") int type
    );
    @GET("article/list")
    Call<PageResponse<Article>> getArticleList(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

}

// ========== DTO Classes ==========

// ChatRequest 类
class ChatRequest {
    private String message;

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

// ChatResponse 类
class ChatResponse {
    @SerializedName("reply")
    private String reply;

    @SerializedName("success")
    private boolean success;

    @SerializedName("error")
    private String error;

    public ChatResponse() {}

    public ChatResponse(String reply, boolean success, String error) {
        this.reply = reply;
        this.success = success;
        this.error = error;
    }

    // Getters
    public String getReply() {
        return reply;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    // Setters
    public void setReply(String reply) {
        this.reply = reply;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setError(String error) {
        this.error = error;
    }

    // 辅助方法
    public boolean hasError() {
        return error != null && !error.trim().isEmpty();
    }
}

class ChatHistoryMessage {
    private String role;
    private String content;
    private String createTime;

    public String getRole() { return role; }
    public String getContent() { return content; }
    public String getCreateTime() { return createTime; }
}
