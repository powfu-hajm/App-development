package com.example.qxb;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    private TextView chatDisplay;
    private EditText inputMessage;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_fragment);

        // 绑定视图 - 使用与XML中一致的ID
        chatDisplay = findViewById(R.id.tv_chat_display);
        inputMessage = findViewById(R.id.et_input_message);
        sendButton = findViewById(R.id.btn_send_message);

        // 设置发送按钮点击事件
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    // 模拟AI回复
                    String currentText = chatDisplay.getText().toString();
                    String newText = currentText + "\n\n你: " + message + "\nAI: 我明白你的感受，让我们继续聊聊...";
                    chatDisplay.setText(newText);

                    // 清空输入框
                    inputMessage.setText("");
                }
            }
        });
    }
}