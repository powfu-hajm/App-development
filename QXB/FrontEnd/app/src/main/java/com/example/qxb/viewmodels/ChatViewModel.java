package com.example.qxb.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<List<String>> messages;
    private List<String> messageList;

    public ChatViewModel() {
        messageList = new ArrayList<>();
        messages = new MutableLiveData<>();
        messages.setValue(messageList);
    }

    public LiveData<List<String>> getMessages() {
        return messages;
    }

    public void sendMessage(String message) {
        messageList.add("你: " + message);
        messageList.add("AI: 这是模拟回复");
        messages.setValue(new ArrayList<>(messageList));
    }

    public void clearChat() {
        messageList.clear();
        messages.setValue(new ArrayList<>(messageList));
    }
}