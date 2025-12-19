package com.example.qxb;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.qxb.models.User;
import com.example.qxb.models.UserUpdateDTO;
import com.example.qxb.models.network.ApiResponse;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private TextInputEditText etNickname, etPhone, etOldPassword, etNewPassword;
    private Button btnSave;
    private ApiService apiService;
    private Uri photoUri;

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "需要相机权限", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    uploadAvatar(photoUri);
                }
            });

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    uploadAvatar(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        apiService = RetrofitClient.getApiService();

        initViews();
        loadUserInfo();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ivAvatar = findViewById(R.id.ivAvatar);
        etNickname = findViewById(R.id.etNickname);
        etPhone = findViewById(R.id.etPhone);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSave = findViewById(R.id.btnSave);

        ivAvatar.setOnClickListener(v -> showImagePicker());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserInfo() {
        apiService.getUserInfo().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    User user = response.body().getData();
                    if (user.getNickname() != null) etNickname.setText(user.getNickname());
                    if (user.getPhone() != null) etPhone.setText(user.getPhone());
                    
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        String fullUrl = RetrofitClient.BASE_URL.replace("/api/", "") + user.getAvatar();
                        Glide.with(EditProfileActivity.this)
                             .load(fullUrl)
                             .placeholder(R.drawable.ic_launcher_background)
                             .into(ivAvatar);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "加载用户信息失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImagePicker() {
        String[] options = {"拍照", "从相册选择"};
        new AlertDialog.Builder(this)
                .setTitle("更换头像")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermission();
                    } else {
                        pickImageLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            photoUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider",
                    photoFile);
            takePictureLauncher.launch(photoUri);
        } catch (IOException ex) {
            Toast.makeText(this, "创建文件失败", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "avatar_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir("Pictures");
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void uploadAvatar(Uri uri) {
        File file = getFileFromUri(uri);
        if (file != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            apiService.uploadAvatar(body).enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String avatarUrl = response.body().getData();
                        String fullUrl = RetrofitClient.BASE_URL.replace("/api/", "") + avatarUrl;
                         Glide.with(EditProfileActivity.this)
                             .load(fullUrl)
                             .into(ivAvatar);
                         Toast.makeText(EditProfileActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "上传出错: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            
            File tempFile = File.createTempFile("upload", ".jpg", getCacheDir());
            FileOutputStream out = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            inputStream.close();
            out.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveProfile() {
        String nickname = etNickname.getText() != null ? etNickname.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String oldPwd = etOldPassword.getText() != null ? etOldPassword.getText().toString().trim() : "";
        String newPwd = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";

        if (nickname.isEmpty()) {
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        UserUpdateDTO dto = new UserUpdateDTO(nickname, phone, oldPwd, newPwd);
        apiService.updateUser(dto).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditProfileActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String error = "保存失败";
                    if (response.body() != null) error = response.body().getMessage();
                    Log.e("EditProfile", "Save failed code: " + response.code() + " msg: " + response.message());
                    Toast.makeText(EditProfileActivity.this, error + " (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e("EditProfile", "Network error", t);
                Toast.makeText(EditProfileActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}



