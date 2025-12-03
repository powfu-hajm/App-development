package com.example.qxb.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.qxb.models.entity.ChatMessage;
import com.example.qxb.repository.ChatRepository;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private final ChatRepository chatRepository;
    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public ChatViewModel() {
        this.chatRepository = new ChatRepository();
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void sendMessage(String content) {
        isLoading.setValue(true);
        // 调用Repository发送消息
        chatRepository.sendMessage(content, new ChatRepository.Callback() {
            @Override
            public void onSuccess(List<ChatMessage> newMessages) {
                isLoading.setValue(false);
                messages.setValue(newMessages);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                // 处理错误
            }
        });
    }

    public void loadChatHistory(String conversationId) {
        isLoading.setValue(true);
        chatRepository.loadChatHistory(conversationId, new ChatRepository.Callback() {
            @Override
            public void onSuccess(List<ChatMessage> chatMessages) {
                isLoading.setValue(false);
                messages.setValue(chatMessages);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                // 处理错误
            }
        });
    }
}