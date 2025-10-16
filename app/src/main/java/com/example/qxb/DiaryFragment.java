package com.example.qxb;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class DiaryFragment extends Fragment {

    private ChipGroup chipGroupMoods, chipGroupReasons;
    private EditText etDiaryContent;
    private TextView tvCharCount;
    private Button btnSaveDiary;

    private List<String> selectedMoods = new ArrayList<>();
    private List<String> selectedReasons = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        initViews(view);
        setupClickListeners();
        setupTextWatcher();

        return view;
    }

    private void initViews(View view) {
        // 修复：使用正确的ID
        chipGroupMoods = view.findViewById(R.id.chipGroupMoods);
        chipGroupReasons = view.findViewById(R.id.chipGroupReasons);
        etDiaryContent = view.findViewById(R.id.etDiaryContent);
        tvCharCount = view.findViewById(R.id.tvCharCount);
        btnSaveDiary = view.findViewById(R.id.btnSaveDiary);
    }

    private void setupClickListeners() {
        // 心情选择监听
        if (chipGroupMoods != null) {
            chipGroupMoods.setOnCheckedStateChangeListener((group, checkedIds) -> {
                selectedMoods.clear();
                for (int id : checkedIds) {
                    Chip chip = group.findViewById(id);
                    if (chip != null) {
                        selectedMoods.add(chip.getText().toString());
                    }
                }

                // 限制最多选择2个
                if (selectedMoods.size() > 2) {
                    Chip lastChip = group.findViewById(checkedIds.get(checkedIds.size() - 1));
                    if (lastChip != null) {
                        lastChip.setChecked(false);
                        Toast.makeText(getContext(), "最多只能选择2个心情", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // 原因选择监听
        if (chipGroupReasons != null) {
            chipGroupReasons.setOnCheckedStateChangeListener((group, checkedIds) -> {
                selectedReasons.clear();
                for (int id : checkedIds) {
                    Chip chip = group.findViewById(id);
                    if (chip != null) {
                        selectedReasons.add(chip.getText().toString());
                    }
                }

                // 限制最多选择3个
                if (selectedReasons.size() > 3) {
                    Chip lastChip = group.findViewById(checkedIds.get(checkedIds.size() - 1));
                    if (lastChip != null) {
                        lastChip.setChecked(false);
                        Toast.makeText(getContext(), "最多只能选择3个原因", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (btnSaveDiary != null) {
            btnSaveDiary.setOnClickListener(v -> saveDiary());
        }
    }

    private void setupTextWatcher() {
        if (etDiaryContent != null && tvCharCount != null) {
            etDiaryContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    tvCharCount.setText(s.length() + "/45");

                    // 限制最大字符数
                    if (s.length() > 45) {
                        etDiaryContent.setText(s.subSequence(0, 45));
                        etDiaryContent.setSelection(45);
                        Toast.makeText(getContext(), "最多只能输入45个字符", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void saveDiary() {
        String content = etDiaryContent.getText().toString().trim();

        // 验证输入
        if (selectedMoods.isEmpty()) {
            Toast.makeText(getContext(), "请至少选择一个心情", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(getContext(), "请写点什么记录今天的心情", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建日记条目
        DiaryEntry diaryEntry = new DiaryEntry(
                System.currentTimeMillis(),
                selectedMoods,
                selectedReasons,
                content
        );

        // 保存日记
        saveDiaryToDatabase(diaryEntry);

        // 显示成功提示
        Toast.makeText(getContext(), "日记保存成功", Toast.LENGTH_SHORT).show();

        // 重置表单
        resetForm();
    }

    private void saveDiaryToDatabase(DiaryEntry diaryEntry) {
        // 实际的数据保存逻辑
        // 这里可以保存到SharedPreferences、Room数据库或发送到服务器

        // 模拟保存成功
        System.out.println("日记保存: " + diaryEntry.toString());
    }

    private void resetForm() {
        if (etDiaryContent != null) {
            etDiaryContent.setText("");
        }

        if (chipGroupMoods != null) {
            chipGroupMoods.clearCheck();
        }

        if (chipGroupReasons != null) {
            chipGroupReasons.clearCheck();
        }

        if (tvCharCount != null) {
            tvCharCount.setText("0/45");
        }

        selectedMoods.clear();
        selectedReasons.clear();
    }
}