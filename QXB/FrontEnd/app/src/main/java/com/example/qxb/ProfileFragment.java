package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.qxb.models.User;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.network.Diary;
import com.example.qxb.utils.SessionManager;
import com.example.qxb.utils.ThemeManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private View menuConsultation, menuTests, menuDiaries, menuSettings, menuTheme;
    private ImageButton btnEditProfile;
    private TextView tvCurrentTheme;
    private ThemeManager themeManager;

    // 用户信息视图
    private ImageView ivProfileAvatar;
    private TextView tvProfileName, tvProfileNickname, tvProfileId, tvProfileTime;

    // 数据统计
    private TextView tvDiaryCount, tvTestCount, tvStreakCount;

    // 新增：账户注销按钮
    private Button btnDeleteAccount;

    private ApiService apiService;
    private SessionManager sessionManager;
    private Long currentUserId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(getContext());
        themeManager = ThemeManager.getInstance(getContext());

        // 获取当前用户ID
        currentUserId = sessionManager.getUserId();
        Log.d("ProfileFragment", "当前用户ID: " + currentUserId);

        initViews(view);
        setupClickListeners();

        // 加载用户信息和统计数据
        loadUserInfo();
        loadStatistics();

        return view;
    }

    // 每次页面可见时都重新加载数据
    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
        loadStatistics();
    }

    private void initViews(View view) {
        btnLogout = view.findViewById(R.id.btnLogout);
        menuConsultation = view.findViewById(R.id.menu_consultation);
        menuTests = view.findViewById(R.id.menu_tests);
        menuDiaries = view.findViewById(R.id.menu_diaries);
        menuSettings = view.findViewById(R.id.menu_settings);
        menuTheme = view.findViewById(R.id.menu_theme);
        tvCurrentTheme = view.findViewById(R.id.tvCurrentTheme);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // 更新当前主题显示
        updateThemeDisplay();

        // 绑定用户信息控件
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileNickname = view.findViewById(R.id.tvProfileNickname);
        tvProfileId = view.findViewById(R.id.tvProfileId);
        tvProfileTime = view.findViewById(R.id.tvProfileTime);

        // 绑定统计控件
        try {
            tvDiaryCount = view.findViewById(R.id.tvDiaryCount);
            tvTestCount = view.findViewById(R.id.tvTestCount);
            tvStreakCount = view.findViewById(R.id.tvStreakCount);
        } catch (Exception e) {
            Log.d("ProfileFragment", "统计控件未找到，跳过初始化");
        }

        // 新增：账户注销按钮
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
    }

    private void loadUserInfo() {
        if (apiService == null) return;

        // 先从MainActivity获取缓存，再更新
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null && mainActivity.getCurrentUser() != null) {
            updateUserUI(mainActivity.getCurrentUser());
            currentUserId = mainActivity.getCurrentUser().getId();
            Log.d("ProfileFragment", "从MainActivity获取用户ID: " + currentUserId);
        }

        // 尝试从服务器获取最新信息
        if (sessionManager.isLoggedIn()) {
            apiService.getUserInfo().enqueue(new Callback<ApiResponse<User>>() {
                @Override
                public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        User user = response.body().getData();

                        Log.d("ProfileFragment", "用户信息加载成功: " + user.toString());
                        Log.d("ProfileFragment", "用户ID: " + user.getId());
                        Log.d("ProfileFragment", "创建时间: " + user.getCreateTime());

                        // 更新用户ID
                        currentUserId = user.getId();
                        sessionManager.saveUserId(user.getId());

                        // 更新MainActivity中的用户信息
                        MainActivity mainActivity = (MainActivity) getActivity();
                        if (mainActivity != null) {
                            mainActivity.updateCurrentUser(user);
                        }

                        // 更新UI
                        updateUserUI(user);

                        // 重新加载统计数据
                        loadStatistics();
                    } else {
                        Log.d("ProfileFragment", "API返回用户信息为空，使用备用方案");
                        // 使用备用方案
                        useFallbackUserInfo();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                    Log.e("ProfileFragment", "加载用户信息失败: " + t.getMessage());
                    // 使用备用方案
                    useFallbackUserInfo();
                }
            });
        } else {
            // 使用备用方案
            useFallbackUserInfo();
        }
    }

    private void updateUserUI(User user) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            try {
                // 显示用户名
                if (tvProfileName != null) {
                    String username = user.getUsername();
                    if (username != null && !username.trim().isEmpty()) {
                        tvProfileName.setText("用户名: " + username);
                        Log.d("ProfileFragment", "设置用户名: " + username);
                    } else {
                        tvProfileName.setText("用户名: 用户");
                    }
                }

                // 显示昵称
                if (tvProfileNickname != null) {
                    String nickname = user.getNickname();
                    if (nickname != null && !nickname.trim().isEmpty() && !nickname.equals("新用户")) {
                        tvProfileNickname.setText("昵称: " + nickname);
                        Log.d("ProfileFragment", "设置昵称: " + nickname);
                    } else {
                        tvProfileNickname.setText("昵称: 未设置");
                        Log.d("ProfileFragment", "用户昵称为空或为默认值");
                    }
                }

                // 显示用户ID
                if (tvProfileId != null) {
                    if (user.getId() != null) {
                        tvProfileId.setText("ID: " + user.getId().toString());
                        Log.d("ProfileFragment", "设置用户ID: " + user.getId().toString());
                    } else {
                        tvProfileId.setText("ID: 未知");
                        Log.d("ProfileFragment", "用户ID为空");
                    }
                }

                // 显示注册时间
                if (tvProfileTime != null) {
                    String createTime = user.getCreateTime();
                    if (createTime != null && !createTime.trim().isEmpty()) {
                        // 格式化时间显示
                        String formattedTime = formatCreateTime(createTime);
                        tvProfileTime.setText("加入时间：" + formattedTime);
                        Log.d("ProfileFragment", "设置加入时间: " + formattedTime + " (原始: " + createTime + ")");
                    } else {
                        // 如果没有创建时间，使用当前日期作为示例
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        String currentDate = sdf.format(new Date());
                        tvProfileTime.setText("加入时间：" + currentDate);
                        Log.d("ProfileFragment", "使用当前日期作为加入时间: " + currentDate);
                    }
                }

                // 加载头像
                if (ivProfileAvatar != null && getContext() != null) {
                    if (user.hasAvatar()) {
                        String avatarUrl = getFullAvatarUrl(user.getAvatar());
                        Log.d("ProfileFragment", "加载头像: " + avatarUrl);

                        Glide.with(getContext())
                                .load(avatarUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.ic_profile_user)
                                .error(R.drawable.ic_profile_user)
                                .into(ivProfileAvatar);
                    } else {
                        ivProfileAvatar.setImageResource(R.drawable.ic_profile_user);
                        Log.d("ProfileFragment", "使用默认头像");
                    }
                }
            } catch (Exception e) {
                Log.e("ProfileFragment", "更新UI失败: " + e.getMessage(), e);
            }
        });
    }

    // 备用方案：使用SessionManager中的信息
    private void useFallbackUserInfo() {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            try {
                // 从SessionManager获取用户名
                SessionManager sessionManager = new SessionManager(getContext());
                String username = sessionManager.getUsername();

                // 创建用户对象
                User user = new User();
                if (username != null) {
                    user.setUsername(username);
                } else {
                    user.setUsername("用户");
                }

                // 设置ID
                if (currentUserId != null && currentUserId > 0) {
                    user.setId(currentUserId);
                } else {
                    // 默认用户ID
                    user.setId(1L);
                    currentUserId = 1L;
                    sessionManager.saveUserId(1L);
                }

                // 设置创建时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                String currentDate = sdf.format(new Date());
                user.setCreateTime(currentDate);

                // 更新MainActivity缓存
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.updateCurrentUser(user);
                }

                // 更新UI
                updateUserUI(user);

                Log.d("ProfileFragment", "使用备用用户信息: " + user.toString());
            } catch (Exception e) {
                Log.e("ProfileFragment", "备用方案失败: " + e.getMessage());
            }
        });
    }

    // 格式化创建时间
    private String formatCreateTime(String createTime) {
        if (createTime == null || createTime.trim().isEmpty()) {
            return "未知";
        }

        try {
            // 尝试解析常见的日期时间格式
            String[] datePatterns = {
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd",
                    "yyyy/MM/dd HH:mm:ss",
                    "yyyy/MM/dd",
                    "yyyy年MM月dd日 HH:mm:ss",
                    "yyyy年MM月dd日"
            };

            for (String pattern : datePatterns) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat(pattern, Locale.CHINA);
                    Date date = inputFormat.parse(createTime);
                    if (date != null) {
                        // 输出为中文格式
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
                        return outputFormat.format(date);
                    }
                } catch (ParseException e) {
                    // 继续尝试下一个格式
                    continue;
                }
            }

            // 如果所有格式都失败，返回原始字符串
            return createTime;
        } catch (Exception e) {
            Log.e("ProfileFragment", "格式化时间失败: " + e.getMessage());
            return createTime;
        }
    }

    // 获取完整的头像URL
    private String getFullAvatarUrl(String avatarPath) {
        if (avatarPath == null || avatarPath.isEmpty()) {
            return "";
        }

        if (avatarPath.startsWith("http")) {
            return avatarPath;
        }

        // 处理相对路径
        String baseUrl = RetrofitClient.BASE_URL;
        if (baseUrl == null) {
            return avatarPath;
        }

        // 清理baseUrl
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        // 清理avatarPath
        if (avatarPath.startsWith("/")) {
            avatarPath = avatarPath.substring(1);
        }

        return baseUrl + "/" + avatarPath;
    }

    private void loadStatistics() {
        // 加载日记数量
        loadDiaryCount();
        // 加载测试完成数量
        loadTestCount();
        // 加载连续打卡天数
        loadStreakCount();
    }

    private void loadDiaryCount() {
        if (apiService == null || tvDiaryCount == null) return;

        if (!sessionManager.isLoggedIn()) {
            tvDiaryCount.setText("0");
            Log.d("ProfileFragment", "用户未登录，跳过加载日记数量");
            return;
        }

        // 修复：调用无参数的getDiaries()方法
        apiService.getDiaries().enqueue(new Callback<ApiResponse<List<Diary>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Diary>>> call,
                                   Response<ApiResponse<List<Diary>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    int count = response.body().getData().size();
                    tvDiaryCount.setText(String.valueOf(count));
                    Log.d("ProfileFragment", "日记数量: " + count);
                } else {
                    tvDiaryCount.setText("0");
                    String errorMsg = "获取日记数量失败";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg += ": " + response.body().getMessage();
                    }
                    Log.d("ProfileFragment", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Diary>>> call, Throwable t) {
                tvDiaryCount.setText("0");
                Log.e("ProfileFragment", "获取日记数量失败: " + t.getMessage());
            }
        });
    }

    private void loadTestCount() {
        if (apiService == null || tvTestCount == null) return;

        if (!sessionManager.isLoggedIn()) {
            tvTestCount.setText("0");
            Log.d("ProfileFragment", "用户未登录，跳过加载测试数量");
            return;
        }

        // 修复：调用无参数的getTestHistory()方法
        apiService.getTestHistory().enqueue(new Callback<ApiResponse<List<com.example.qxb.models.test.TestResult>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<com.example.qxb.models.test.TestResult>>> call,
                                   Response<ApiResponse<List<com.example.qxb.models.test.TestResult>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    int count = response.body().getData().size();
                    tvTestCount.setText(String.valueOf(count));
                    Log.d("ProfileFragment", "测试完成数量: " + count);
                } else {
                    tvTestCount.setText("0");
                    String errorMsg = "获取测试数量失败";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg += ": " + response.body().getMessage();
                    }
                    Log.d("ProfileFragment", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<com.example.qxb.models.test.TestResult>>> call, Throwable t) {
                tvTestCount.setText("0");
                Log.e("ProfileFragment", "获取测试数量失败: " + t.getMessage());
            }
        });
    }

    private void loadStreakCount() {
        // 这里可以调用连续打卡接口获取连续打卡天数
        // 暂时使用默认值
        if (tvStreakCount != null) {
            tvStreakCount.setText("0");
        }
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> logout());

        // 新增：账户注销功能
        if (btnDeleteAccount != null) {
            btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
        }

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        menuConsultation.setOnClickListener(v -> {
            Toast.makeText(getContext(), "我的咨询记录", Toast.LENGTH_SHORT).show();
        });

        menuTests.setOnClickListener(v -> {
            // 跳转到测试记录页面
            Intent intent = new Intent(getActivity(), TestActivity.class);
            startActivity(intent);
        });

        menuDiaries.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DiaryListActivity.class);
            startActivity(intent);
        });

        menuSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        // 主题切换点击事件
        if (menuTheme != null) {
            menuTheme.setOnClickListener(v -> showThemeDialog());
        }
    }

    /**
     * 更新主题显示文本
     */
    private void updateThemeDisplay() {
        if (tvCurrentTheme != null && themeManager != null) {
            tvCurrentTheme.setText(themeManager.getCurrentThemeName());
        }
    }

    /**
     * 显示主题选择对话框
     */
    private void showThemeDialog() {
        if (getActivity() == null) return;

        String[] themes = {"浅色主题", "深色主题", "跟随系统"};
        int currentTheme = themeManager.getThemeMode();

        new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setTitle("选择主题")
                .setSingleChoiceItems(themes, currentTheme, (dialog, which) -> {
                    themeManager.setThemeMode(which);
                    updateThemeDisplay();
                    dialog.dismiss();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void logout() {
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.logout();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }

        Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
    }

    // 新增：显示账户注销确认对话框
    private void showDeleteAccountDialog() {
        if (getActivity() == null) return;

        new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setTitle("确认注销账户")
                .setMessage("⚠️ 严重警告 ⚠️\n\n" +
                        "确定要永久注销当前账户吗？\n" +
                        "此操作将：\n" +
                        "1. 永久删除您的所有个人数据\n" +
                        "2. 删除您的日记、测试记录等\n" +
                        "3. 无法恢复！\n\n" +
                        "请谨慎操作！")
                .setPositiveButton("确认注销", (dialog, which) -> deleteAccount())
                .setNegativeButton("取消", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // 新增：执行账户注销
    private void deleteAccount() {
        if (apiService == null) {
            Toast.makeText(getContext(), "网络服务不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        if (btnDeleteAccount != null) {
            btnDeleteAccount.setEnabled(false);
        }

        Toast.makeText(getContext(), "正在注销账户...", Toast.LENGTH_SHORT).show();

        apiService.deleteAccount().enqueue(new Callback<ApiResponse<Boolean>>() {
            @Override
            public void onResponse(Call<ApiResponse<Boolean>> call, Response<ApiResponse<Boolean>> response) {
                if (btnDeleteAccount != null) {
                    btnDeleteAccount.setEnabled(true);
                }

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Boolean> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null && apiResponse.getData()) {
                        // 账户注销成功
                        Toast.makeText(getContext(), "账户已成功注销", Toast.LENGTH_SHORT).show();
                        Log.d("ProfileFragment", "账户注销成功");

                        // 清除本地登录状态
                        SessionManager sessionManager = new SessionManager(getContext());
                        sessionManager.logout();

                        // 跳转到登录页面
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("account_deleted", true);
                        startActivity(intent);

                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ?
                                apiResponse.getMessage() : "账户注销失败";
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e("ProfileFragment", "账户注销失败: " + errorMsg);
                    }
                } else {
                    String errorMsg = "服务器响应错误";
                    if (response.code() == 401) {
                        errorMsg = "未登录或登录已过期";
                    } else if (response.code() == 404) {
                        errorMsg = "接口不存在";
                    } else if (response.code() == 500) {
                        errorMsg = "服务器内部错误";
                    }

                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("ProfileFragment", "账户注销失败，状态码: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {
                if (btnDeleteAccount != null) {
                    btnDeleteAccount.setEnabled(true);
                }

                String errorMsg = "网络错误: " + t.getMessage();
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                Log.e("ProfileFragment", "账户注销请求失败", t);
            }
        });
    }
}