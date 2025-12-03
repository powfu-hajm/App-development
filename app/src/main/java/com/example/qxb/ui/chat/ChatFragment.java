package com.example.qxb.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.adapter.ChatAdapter;
import com.example.qxb.models.entity.ChatMessage;
import com.example.qxb.R;
import com.example.qxb.models.network.ChatRequest;
import com.example.qxb.models.network.ChatResponse;
import com.example.qxb.models.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        return view;
    }

    private void initViews(View view) {
        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);

        messageList = new ArrayList<>();
        // Add welcome message
        messageList.add(new ChatMessage(
                "你好，我是你的专属心理支持者小青。在这里我们可以没有任何负担的私密交流，我会认真倾听。",
                ChatMessage.TYPE_AI,
                System.currentTimeMillis()
        ));
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messageList);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMessages.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        // 1. Show user message immediately
        addMessage(message, ChatMessage.TYPE_USER);
        etMessage.setText("");
        btnSend.setEnabled(false); // Prevent double sending

        // 2. Call API
        RetrofitClient.getApiService().sendChatMessage(new ChatRequest(message))
                .enqueue(new Callback<ChatResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ChatResponse> call, @NonNull Response<ChatResponse> response) {
                        btnSend.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null) {
                            ChatResponse chatResponse = response.body();
                            if (chatResponse.isSuccess()) {
                                addMessage(chatResponse.getReply(), ChatMessage.TYPE_AI);
                            } else {
                                addMessage("错误: " + chatResponse.getError(), ChatMessage.TYPE_AI);
                            }
                        } else {
                            addMessage("抱歉，小青暂时连不上服务器。", ChatMessage.TYPE_AI);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ChatResponse> call, @NonNull Throwable t) {
                        btnSend.setEnabled(true);
                        addMessage("网络错误: " + t.getMessage(), ChatMessage.TYPE_AI);
                        t.printStackTrace();
                    }
                });
    }

    private void addMessage(String content, int type) {
        messageList.add(new ChatMessage(content, type, System.currentTimeMillis()));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessages.scrollToPosition(messageList.size() - 1);
    }
}