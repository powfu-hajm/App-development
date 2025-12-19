package com.example.qxb;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.adapter.ChatAdapter;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.widgets.EmptyStateView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;
    private EmptyStateView emptyStateView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        initApiService();
        loadChatHistory(); // 新增：加载历史记录

        return view;
    }

    private void initViews(View view) {
        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        emptyStateView = view.findViewById(R.id.emptyStateView);

        messageList = new ArrayList<>();
        Log.d("ChatDebug", "初始化完成，消息列表大小: " + messageList.size());

        // 显示初始欢迎引导
        showWelcomeGuide();
    }

    /**
     * 显示欢迎引导空状态
     */
    private void showWelcomeGuide() {
        if (emptyStateView != null) {
            emptyStateView.setVisibility(View.VISIBLE);
            emptyStateView.setEmptyType(EmptyStateView.EmptyType.NO_CHAT);
            emptyStateView.setAction("开始聊天", v -> {
                hideEmptyState();
                etMessage.requestFocus();
            });
        }
    }

    /**
     * 显示网络错误空状态
     */
    private void showNetworkError() {
        if (emptyStateView != null && rvMessages != null) {
            rvMessages.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
            emptyStateView.setEmptyType(EmptyStateView.EmptyType.NETWORK_ERROR);
            emptyStateView.setAction("重试", v -> loadChatHistory());
        }
    }

    /**
     * 隐藏空状态
     */
    private void hideEmptyState() {
        if (emptyStateView != null && rvMessages != null) {
            emptyStateView.setVisibility(View.GONE);
            rvMessages.setVisibility(View.VISIBLE);
        }
    }

    private void initApiService() {
        apiService = RetrofitClient.getApiService();
        if (apiService == null) {
            Toast.makeText(getContext(), "网络服务初始化失败", Toast.LENGTH_SHORT).show();
            Log.e("ChatDebug", "网络服务初始化失败");
        }
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messageList);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMessages.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> sendMessage());

        // 回车发送
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == 66)) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    /**
     * 加载历史记录
     */
    private void loadChatHistory() {
        if (apiService == null) return;

        apiService.getChatHistory().enqueue(new Callback<ApiResponse<List<ChatHistoryMessage>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ChatHistoryMessage>>> call, Response<ApiResponse<List<ChatHistoryMessage>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<ChatHistoryMessage>> apiResponse = response.body();
                    if (apiResponse.getCode() == 200 && apiResponse.getData() != null) {
                        List<ChatHistoryMessage> history = apiResponse.getData();
                        Log.d("ChatDebug", "获取历史记录成功，条数: " + history.size());

                        // 有历史记录时隐藏空状态
                        if (history.size() > 0) {
                            hideEmptyState();
                        }

                        messageList.clear();
                        // 1. 添加欢迎语
                        messageList.add(new ChatMessage(
                                "你好，我是你的专属心理支持者小青AI。在这里我们可以没有任何负担的私密交流，我会认真倾听。",
                                ChatMessage.TYPE_AI,
                                System.currentTimeMillis()
                        ));

                        // 2. 添加历史记录 (注意：后端传回来的已经是正序了)
                        for (ChatHistoryMessage msg : history) {
                            int type = "user".equalsIgnoreCase(msg.getRole()) ? ChatMessage.TYPE_USER : ChatMessage.TYPE_AI;
                            messageList.add(new ChatMessage(msg.getContent(), type, System.currentTimeMillis()));
                        }
                        
                        // 3. 刷新列表并滚到底部
                        chatAdapter.notifyDataSetChanged();
                        if (messageList.size() > 0) {
                            rvMessages.scrollToPosition(messageList.size() - 1);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ChatHistoryMessage>>> call, Throwable t) {
                Log.e("ChatDebug", "加载历史记录失败", t);
            }
        });
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(getContext(), "请输入消息", Toast.LENGTH_SHORT).show();
            return;
        }

        if (apiService == null) {
            Toast.makeText(getContext(), "网络服务未初始化", Toast.LENGTH_SHORT).show();
            return;
        }

        // 发送消息时隐藏空状态
        hideEmptyState();

        // 添加用户消息到列表
        ChatMessage userMessage = new ChatMessage(message, ChatMessage.TYPE_USER, System.currentTimeMillis());
        messageList.add(userMessage);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessages.scrollToPosition(messageList.size() - 1);

        // 清空输入框
        etMessage.setText("");

        // 调用AI接口
        callAIAPI(message);
    }

    private void callAIAPI(String userMessage) {
        ChatRequest request = new ChatRequest(userMessage);

        apiService.chatWithAI(request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatResponse chatResponse = response.body();
                    if (chatResponse.isSuccess() && chatResponse.getReply() != null) {
                        String aiReply = chatResponse.getReply();
                        requireActivity().runOnUiThread(() -> {
                            ChatMessage aiMessage = new ChatMessage(
                                    aiReply,
                                    ChatMessage.TYPE_AI,
                                    System.currentTimeMillis()
                            );
                            messageList.add(aiMessage);
                            chatAdapter.notifyItemInserted(messageList.size() - 1);
                            rvMessages.scrollToPosition(messageList.size() - 1);
                        });
                    } else {
                        String errorMsg = chatResponse.getError() != null ? chatResponse.getError() : "未知错误";
                        requireActivity().runOnUiThread(() -> showError("AI服务返回错误: " + errorMsg));
                    }
                } else {
                    requireActivity().runOnUiThread(() -> showError("网络请求失败: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                requireActivity().runOnUiThread(() -> showError("网络连接失败: " + t.getMessage()));
            }
        });
    }

    private void showError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        // 添加错误消息到聊天记录
        ChatMessage errorMessage = new ChatMessage(
                "抱歉，小青暂时无法回答。请检查网络连接或稍后重试。",
                ChatMessage.TYPE_AI,
                System.currentTimeMillis()
        );
        messageList.add(errorMessage);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessages.scrollToPosition(messageList.size() - 1);
    }
}
