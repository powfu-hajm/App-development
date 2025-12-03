package com.example.qxb;

import androidx.lifecycle.ViewModel;

public class TestViewModel extends ViewModel {

    private int currentQuestionIndex = 0;

    public void answerQuestion(int selectedOptionId) {
        // 这里实现答题逻辑
        System.out.println("用户选择了选项: " + selectedOptionId);
        // 可以记录答案、计算分数等
    }

    public void nextQuestion() {
        // 切换到下一题
        currentQuestionIndex++;
        System.out.println("切换到第 " + currentQuestionIndex + " 题");
    }

    public void previousQuestion() {
        // 切换到上一题
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            System.out.println("切换到第 " + currentQuestionIndex + " 题");
        }
    }

    public void submitTest() {
        // 这里实现测试提交逻辑
        System.out.println("测试提交完成");
        // 可以计算总分、保存结果等
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
}