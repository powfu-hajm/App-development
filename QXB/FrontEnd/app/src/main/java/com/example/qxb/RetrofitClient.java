package com.example.qxb;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import android.util.Log;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class RetrofitClient {
    // 优先使用环境变量配置的IP，方便切换
    public static String BASE_URL = null;

    private static Retrofit retrofit = null;
    public static void reset() {
        retrofit = null;
    }

    // 全局 Token 缓存
    public static String authToken = null;

    // 添加上下文引用
    private static Context appContext;

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
        retrofit = null; // 重置retrofit实例以使用新URL
        Log.d("NetworkDebug", "设置新的BASE_URL: " + BASE_URL);
    }

    // 初始化上下文
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static ApiService getApiService() {
        return getApiService(appContext);
    }

    public static ApiService getApiService(Context context) {
        if (context != null && appContext == null) {
            appContext = context.getApplicationContext();
        }

        if (retrofit == null) {
            // 检查网络连接
            if (!isNetworkAvailable(appContext)) {
                Log.e("NetworkDebug", "没有可用的网络连接");
                return null;
            }

            // 如果没有设置BASE_URL，使用默认配置
            if (BASE_URL == null) {
                // 可以根据调试环境自动选择
                if (isEmulator()) {
                    // 模拟器使用10.0.2.2
                    BASE_URL = "http://10.0.2.2:8080/";
                    Log.d("NetworkDebug", "使用模拟器地址: " + BASE_URL);
                } else {
                    // 真机使用电脑局域网IP，请根据实际情况修改
                    // 注意：这里需要你手动修改为你的服务器IP地址
                    BASE_URL = "http://10.101.134.23:8080/"; // 修改为你的实际IP
                    Log.d("NetworkDebug", "使用真机地址: " + BASE_URL);
                }
            }

            Log.d("NetworkDebug", "=== 创建Retrofit实例 ===");
            Log.d("NetworkDebug", "使用基础URL: " + BASE_URL);

            // 详细的日志拦截器
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
                Log.d("NetworkDebug", message);
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 添加超时和重试机制
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时
                    .readTimeout(30, TimeUnit.SECONDS)     // 读取超时
                    .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时
                    .retryOnConnectionFailure(true)        // 启用重试
                    .addInterceptor(chain -> {
                        // 检查网络状态
                        if (!isNetworkAvailable(appContext)) {
                            throw new NoNetworkException("没有可用的网络连接");
                        }

                        // 添加请求头
                        okhttp3.Request original = chain.request();
                        okhttp3.Request.Builder requestBuilder = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json");

                        // === 核心修改：如果已登录，自动添加 Token ===
                        if (authToken != null && !authToken.isEmpty()) {
                            requestBuilder.header("Authorization", "Bearer " + authToken);
                            Log.d("NetworkDebug", "已添加 Token 认证头");
                        }

                        requestBuilder.method(original.method(), original.body());

                        okhttp3.Request request = requestBuilder.build();

                        // 打印请求信息
                        Log.d("NetworkDebug", "请求URL: " + request.url());
                        Log.d("NetworkDebug", "请求方法: " + request.method());
                        Log.d("NetworkDebug", "请求头: " + request.headers());

                        try {
                            okhttp3.Response response = chain.proceed(request);
                            Log.d("NetworkDebug", "响应码: " + response.code());
                            Log.d("NetworkDebug", "响应头: " + response.headers());

                            // 检查响应码
                            if (!response.isSuccessful()) {
                                Log.w("NetworkDebug", "请求失败，响应码: " + response.code());
                            }

                            return response;
                        } catch (Exception e) {
                            Log.e("NetworkDebug", "请求失败: " + e.getMessage(), e);
                            throw e;
                        }
                    })
                    .addInterceptor(logging)
                    .build();

            try {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                Log.d("NetworkDebug", "Retrofit实例创建成功");
            } catch (Exception e) {
                Log.e("NetworkDebug", "创建Retrofit实例失败: " + e.getMessage(), e);
                return null;
            }
        }
        return retrofit.create(ApiService.class);
    }

    // 检查是否在模拟器上运行
    private static boolean isEmulator() {
        return android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(android.os.Build.PRODUCT);
    }

    // 检查网络是否可用
    static boolean isNetworkAvailable(Context context) {
        if (context == null) return true; // 如果context为空，假设网络可用

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // 网络连通性测试方法
    public static void testConnection() {
        if (BASE_URL != null) {
            Log.d("NetworkDebug", "测试连接到: " + BASE_URL);
            // 可以添加ping测试或简单HTTP测试
        }
    }

    // 自定义无网络异常
    public static class NoNetworkException extends RuntimeException {
        public NoNetworkException(String message) {
            super(message);
        }
    }
}