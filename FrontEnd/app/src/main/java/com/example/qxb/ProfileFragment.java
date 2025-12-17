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
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.qxb.model.User;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private View menuConsultation, menuTests, menuDiaries, menuSettings;
    private ImageButton btnEditProfile;
    
    // 新增：用户信息视图
    private ImageView ivProfileAvatar;
    private TextView tvProfileName, tvProfileId, tvProfileTime;
    
    private ApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        apiService = RetrofitClient.getApiService();
        
        initViews(view);
        setupClickListeners();

        return view;
    }

    // 关键：每次页面可见时（包括从编辑页返回），都重新加载数据
    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
    }

    private void initViews(View view) {
        btnLogout = view.findViewById(R.id.btnLogout);
        menuConsultation = view.findViewById(R.id.menu_consultation);
        menuTests = view.findViewById(R.id.menu_tests);
        menuDiaries = view.findViewById(R.id.menu_diaries);
        menuSettings = view.findViewById(R.id.menu_settings);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        
        // 绑定用户信息控件
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileId = view.findViewById(R.id.tvProfileId);
        tvProfileTime = view.findViewById(R.id.tvProfileTime);
    }
    
    private void loadUserInfo() {
        if (apiService == null) return;
        
        apiService.getUserInfo().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    User user = response.body().getData();
                    
                    // 更新UI
                    if (tvProfileName != null) {
                        tvProfileName.setText(user.getNickname() != null ? user.getNickname() : user.getUsername());
                    }
                    
                    if (tvProfileId != null) {
                        tvProfileId.setText("ID: " + user.getId());
                    }
                    
                    // 加载头像
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty() && ivProfileAvatar != null && getContext() != null) {
                        String fullUrl = RetrofitClient.BASE_URL.replace("/api/", "") + user.getAvatar();
                        Log.d("ProfileFragment", "Loading avatar from: " + fullUrl);
                        
                        Glide.with(getContext())
                             .load(fullUrl)
                             .diskCacheStrategy(DiskCacheStrategy.NONE) // 不缓存
                             .skipMemoryCache(true) // 跳过内存缓存
                             .placeholder(R.drawable.ic_profile_user)
                             .listener(new RequestListener<Drawable>() {
                                 @Override
                                 public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                     Log.e("ProfileFragment", "Avatar load failed: " + e.getMessage(), e);
                                     return false; // 允许 Glide 继续处理错误占位符
                                 }

                                 @Override
                                 public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                     Log.d("ProfileFragment", "Avatar loaded successfully");
                                     return false;
                                 }
                             })
                             .into(ivProfileAvatar);
                    } else {
                        Log.d("ProfileFragment", "Avatar path is empty or null");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // 静默失败，不打扰用户
            }
        });
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> logout());

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        menuConsultation.setOnClickListener(v -> {
            Toast.makeText(getContext(), "我的咨询记录", Toast.LENGTH_SHORT).show();
        });

        menuTests.setOnClickListener(v -> {
             Toast.makeText(getContext(), "我的测试记录", Toast.LENGTH_SHORT).show();
        });

        menuDiaries.setOnClickListener(v -> {
            Toast.makeText(getContext(), "我的日记记录", Toast.LENGTH_SHORT).show();
        });

        menuSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
    }

    private void logout() {
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.logout();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();

        Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
    }
}
