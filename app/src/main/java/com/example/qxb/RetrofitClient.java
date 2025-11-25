package com.example.qxb;

// 暂时注释掉Retrofit相关导入
// import retrofit2.Retrofit;
// import retrofit2.converter.gson.GsonConverterFactory;
// import okhttp3.OkHttpClient;
// import okhttp3.logging.HttpLoggingInterceptor;
// import java.util.concurrent.TimeUnit;

/**
 * Retrofit客户端 - 暂时禁用网络功能
 * 需要网络功能时取消注释
 */
public class RetrofitClient {
    // 暂时注释掉所有实现
    /*
    private static final String BASE_URL = "https://your-api-server.com/"; // 替换为你的实际API地址
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            // 添加日志拦截器用于调试
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
    */

    // 提供一个空的占位方法，避免编译错误
    public static Object getApiService() {
        return null; // 网络功能暂未启用
    }
}