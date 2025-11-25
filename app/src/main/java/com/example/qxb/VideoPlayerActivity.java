package com.example.qxb;

// 正确的导入
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {  // 确保类名是 VideoPlayerActivity
    private VideoView videoView;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // 安全地获取数据
        ContentItem video = null;
        if (getIntent() != null && getIntent().hasExtra("video_item")) {
            video = getIntent().getParcelableExtra("video_item");
        }

        if (video != null && video.getMediaUrl() != null) {
            setupVideoPlayer(video);
        } else {
            // 处理数据为空的情况
            finish();
        }
    }

    private void setupVideoPlayer(ContentItem video) {
        videoView = findViewById(R.id.videoView);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        try {
            Uri videoUri = Uri.parse(video.getMediaUrl());
            videoView.setVideoURI(videoUri);
            videoView.setMediaController(mediaController);

            // 设置准备监听器
            videoView.setOnPreparedListener(mp -> {
                videoView.start();
            });

            // 设置错误监听器
            videoView.setOnErrorListener((mp, what, extra) -> {
                // 处理播放错误
                finish();
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}