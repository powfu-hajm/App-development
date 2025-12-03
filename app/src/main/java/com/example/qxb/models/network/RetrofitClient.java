package com.example.qxb.models.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    // 直接使用电脑IP
//    private static final String BASE_URL = "http://10.101.132.34:8080/api/";

    // 如果需要切换环境，可以在这里注释/取消注释
     private static final String BASE_URL = "http://10.0.2.2:8080/api/"; // 模拟器

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            System.out.println("=== 使用基础URL: " + BASE_URL + " ===");

            // 详细的日志拦截器
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();
                        android.util.Log.d("NetworkDebug", "请求URL: " + original.url());

                        okhttp3.Response response = chain.proceed(original);

                        android.util.Log.d("NetworkDebug", "响应码: " + response.code());
                        return response;
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}