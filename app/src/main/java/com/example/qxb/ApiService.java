package com.example.qxb;

// 暂时注释掉Retrofit导入
// import retrofit2.Call;
// import retrofit2.http.GET;
// import retrofit2.http.Header;
// import retrofit2.http.Query;
import java.util.List;

/**
 * API服务接口定义
 * 暂时注释掉网络相关功能
 */
public interface ApiService {

    /**
     * 获取内容列表（支持筛选）
     * 暂时注释掉，等需要网络功能时再启用
     */
    // @GET("api/content/list")
    // Call<List<Content>> getContentList(
    //         @Query("type") String type,
    //         @Query("page") int page,
    //         @Query("size") int size
    // );

    /**
     * 获取推荐内容（智能推荐）
     * 暂时注释掉，等需要网络功能时再启用
     */
    // @GET("api/content/recommend")
    // Call<List<Content>> getRecommendations(
    //         @Header("User-ID") Long userId
    // );
}