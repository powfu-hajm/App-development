package com.example.qxb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.R;
import com.example.qxb.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

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
//        setupRecyclerView();
//        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);

        messageList = new ArrayList<>();
        // 添加欢迎消息
        messageList.add(new ChatMessage(
                "你好，我是你的专属心理支持者Moor AI。在这里我们可以没有任何负担的私密交流，我会认真倾听。",
                ChatMessage.TYPE_AI,
                System.currentTimeMillis()
        ));
    }

//    private void setupRecyclerView() {
//        chatAdapter = new ChatFragment(messageList);
//        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
//        rvMessages.setAdapter(chatAdapter);
//    }
//
//    private void setupClickListeners() {
//        btnSend.setOnClickListener(v -> sendMessage());
//    }

//    private void sendMessage() {
//        String message = etMessage.getText().toString().trim();
//        if (!message.isEmpty()) {
//            // 添加用户消息
//            messageList.add(new ChatMessage(message, ChatMessage.TYPE_USER, System.currentTimeMillis()));
//            chatAdapter.notifyItemInserted(messageList.size() - 1);
//            rvMessages.scrollToPosition(messageList.size() - 1);
//            etMessage.setText("");
//
//            // 模拟AI回复
//            simulateAIResponse(message);
//        }
//    }

//    private void simulateAIResponse(String userMessage) {
//        // 这里应该调用实际的AI接口
//        String response = "我理解你的感受。你能多告诉我一些关于这方面的情况吗？";
//
//        getActivity().runOnUiThread(() -> {
//            messageList.add(new ChatMessage(response, ChatMessage.TYPE_AI, System.currentTimeMillis()));
//            chatAdapter.notifyItemInserted(messageList.size() - 1);
//            rvMessages.scrollToPosition(messageList.size() - 1);
//        });
//    }
}