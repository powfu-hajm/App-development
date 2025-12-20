package com.example.qxb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.qxb.databinding.ActivityMoodChartBinding;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.network.MoodChartData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoodChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private ActivityMoodChartBinding binding;
    private ApiService apiService;
    // 删除 DEFAULT_USER_ID 常量，因为不再需要
    private static final String TAG = "MoodChartActivity";

    private BroadcastReceiver dataUpdateReceiver;

    // 心情颜色映射
    private Map<String, Integer> moodColors = new LinkedHashMap<String, Integer>() {{
        put("开心", Color.parseColor("#FF6B6B"));     // 鲜红色
        put("甜蜜", Color.parseColor("#FF9FF3"));     // 粉红色
        put("冲鸭", Color.parseColor("#F368E0"));     // 紫红色
        put("悠闲", Color.parseColor("#48DBFB"));     // 亮蓝色
        put("平淡", Color.parseColor("#54A0FF"));     // 蓝色
        put("疲惫", Color.parseColor("#FF9F43"));     // 橙色
        put("焦虑", Color.parseColor("#FFD93D"));     // 黄色
        put("压抑", Color.parseColor("#6C5CE7"));     // 紫色
        put("难过", Color.parseColor("#00D2D3"));     // 青绿色
        put("生气", Color.parseColor("#FF3838"));     // 深红色
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        binding = ActivityMoodChartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化ApiService - 修复空指针问题
        apiService = RetrofitClient.getApiService();
        if (apiService == null) {
            Toast.makeText(this, "网络服务初始化失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 修复：直接使用布局中的Toolbar，不设置SupportActionBar
        // 因为布局中已经设置了app:title，Toolbar会自动显示标题
        if (getSupportActionBar() == null) {
            setSupportActionBar(binding.toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("情绪分析图表");
        }

        setupLineChart();
        setupPieChart();
        loadChartData();

        setupBroadcastReceiver();
    }

    private void setupBroadcastReceiver() {
        dataUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("DIARY_DATA_UPDATED".equals(intent.getAction())) {
                    loadChartData();
                    Toast.makeText(MoodChartActivity.this, "图表数据已更新", Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter filter = new IntentFilter("DIARY_DATA_UPDATED");
        LocalBroadcastManager.getInstance(this).registerReceiver(dataUpdateReceiver, filter);
    }

    private void setupLineChart() {
        LineChart chart = binding.lineChart;

        // 重置图表
        chart.clear();

        // 基本设置
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
        chart.setDoubleTapToZoomEnabled(true);

        // 设置动画
        chart.animateX(1200);
        chart.animateY(1200);

        // X轴设置 - 修复日期显示
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.parseColor("#30CCCCCC"));
        xAxis.setGridLineWidth(0.5f);
        xAxis.setTextColor(Color.parseColor("#666666"));
        xAxis.setTextSize(10f);
        xAxis.setLabelRotationAngle(-45f);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setLabelCount(8, true);

        // Y轴左侧设置
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#30CCCCCC"));
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setTextColor(Color.parseColor("#666666"));
        leftAxis.setTextSize(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setLabelCount(6, true);
        leftAxis.setSpaceTop(15f);

        // 禁用右侧Y轴
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // 图例设置
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setTextSize(11f);
        legend.setXEntrySpace(15f);
        legend.setYEntrySpace(5f);
        legend.setYOffset(5f);
        legend.setFormSize(10f);
        legend.setFormToTextSpace(5f);

        // 设置图表边距
        chart.setExtraTopOffset(10f);
        chart.setExtraBottomOffset(25f);
        chart.setExtraLeftOffset(10f);
        chart.setExtraRightOffset(10f);

        Log.i(TAG, "折线图设置完成");
    }

    private void setupPieChart() {
        PieChart pieChart = binding.pieChart;

        // 基本设置
        pieChart.getDescription().setEnabled(false);
        pieChart.setTouchEnabled(true);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("心情分布");
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(Color.parseColor("#333333"));

        // 设置动画
        pieChart.animateY(1500);

        // 禁用内置图例（我们将使用自定义图例）
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        // 设置点击监听
        pieChart.setOnChartValueSelectedListener(this);

        Log.i(TAG, "饼图设置完成");
    }

    private void loadChartData() {
        if (apiService == null) {
            Toast.makeText(this, "网络服务不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "开始加载图表数据...");

        // 修复：调用无参数的getMoodChart()方法
        Call<ApiResponse<List<MoodChartData>>> call = apiService.getMoodChart();
        call.enqueue(new Callback<ApiResponse<List<MoodChartData>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MoodChartData>>> call, Response<ApiResponse<List<MoodChartData>>> response) {
                Log.i(TAG, "收到服务器响应，HTTP状态码: " + response.code());

                if (response.isSuccessful()) {
                    ApiResponse<List<MoodChartData>> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.isSuccess()) {
                        List<MoodChartData> chartData = apiResponse.getData();
                        Log.e(TAG, "DEBUG: 收到原始数据条数: " + (chartData != null ? chartData.size() : 0));
                        if (chartData != null) {
                            for (MoodChartData d : chartData) {
                                Log.e(TAG, "DEBUG: 数据项 -> 日期: " + d.getDate() + ", 心情: " + d.getMood() + ", 数量: " + d.getCount());
                            }
                        }

                        updateLineChart(chartData);
                        updatePieChart(chartData);
                    } else {
                        String errorMsg = "API响应失败";
                        if (apiResponse != null) {
                            errorMsg += " - 错误码: " + apiResponse.getCode() + ", 信息: " + apiResponse.getMessage();
                        }
                        Log.e(TAG, errorMsg);
                        Toast.makeText(MoodChartActivity.this, "加载图表数据失败", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "HTTP错误: " + response.code());
                    Toast.makeText(MoodChartActivity.this, "网络请求失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MoodChartData>>> call, Throwable t) {
                String errorMsg = "网络请求失败: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                Toast.makeText(MoodChartActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateLineChart(List<MoodChartData> chartData) {
        if (chartData == null || chartData.isEmpty()) {
            Log.w(TAG, "折线图数据为空");
            binding.lineChart.clear();
            binding.lineChart.setNoDataText("暂无情绪数据");
            binding.lineChart.setNoDataTextColor(Color.GRAY);
            return;
        }

        Log.i(TAG, "开始更新折线图，数据条数: " + chartData.size());

        // 获取所有唯一日期并排序
        Set<String> dateSet = new HashSet<>();
        for (MoodChartData data : chartData) {
            if (data.getDate() != null) {
                dateSet.add(data.getDate());
            }
        }

        List<String> dates = new ArrayList<>(dateSet);
        Collections.sort(dates, new Comparator<String>() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            @Override
            public int compare(String date1, String date2) {
                try {
                    Date d1 = sdf.parse(date1);
                    Date d2 = sdf.parse(date2);
                    return d1.compareTo(d2);
                } catch (ParseException e) {
                    return date1.compareTo(date2);
                }
            }
        });

        Log.i(TAG, "排序后的日期列表: " + dates);

        // 按心情分组数据
        Map<String, List<Entry>> moodEntriesMap = new LinkedHashMap<>();

        // 初始化所有心情的条目列表
        for (String mood : moodColors.keySet()) {
            moodEntriesMap.put(mood, new ArrayList<Entry>());
        }

        // 填充数据
        for (int i = 0; i < dates.size(); i++) {
            String currentDate = dates.get(i);

            // 为当前日期初始化所有心情的计数为0
            Map<String, Integer> dailyCounts = new HashMap<>();
            for (String mood : moodColors.keySet()) {
                dailyCounts.put(mood, 0);
            }

            // 填充实际数据
            for (MoodChartData data : chartData) {
                if (currentDate.equals(data.getDate()) && data.getMood() != null) {
                    // 处理心情标签（可能包含多个心情）
                    String[] moods = data.getMood().split(",");
                    for (String mood : moods) {
                        String trimmedMood = mood.trim();
                        if (dailyCounts.containsKey(trimmedMood)) {
                            dailyCounts.put(trimmedMood, dailyCounts.get(trimmedMood) + (data.getCount() != null ? data.getCount() : 1));
                        }
                    }
                }
            }

            // 为每种心情添加数据点
            for (Map.Entry<String, Integer> entry : dailyCounts.entrySet()) {
                String mood = entry.getKey();
                int count = entry.getValue();

                if (moodEntriesMap.containsKey(mood)) {
                    moodEntriesMap.get(mood).add(new Entry(i, count));
                }
            }
        }

        // 创建数据集
        List<ILineDataSet> dataSets = new ArrayList<>();
        int dataSetCount = 0;

        for (Map.Entry<String, List<Entry>> entry : moodEntriesMap.entrySet()) {
            String mood = entry.getKey();
            List<Entry> entries = entry.getValue();

            // 只显示有数据的心情
            if (!entries.isEmpty()) {
                // 按X轴排序
                Collections.sort(entries, new Comparator<Entry>() {
                    @Override
                    public int compare(Entry e1, Entry e2) {
                        return Float.compare(e1.getX(), e2.getX());
                    }
                });

                LineDataSet dataSet = new LineDataSet(entries, mood);

                // 设置颜色
                int color = moodColors.getOrDefault(mood, Color.GRAY);
                dataSet.setColor(color);
                dataSet.setCircleColor(color);

                // 设置线条样式
                dataSet.setLineWidth(2.5f);
                dataSet.setCircleRadius(4f);
                dataSet.setDrawCircleHole(true);
                dataSet.setCircleHoleRadius(2f);
                dataSet.setValueTextSize(9f);
                dataSet.setValueTextColor(Color.DKGRAY);
                dataSet.setDrawValues(false);

                // 设置曲线模式
                dataSet.setMode(LineDataSet.Mode.LINEAR);

                // 启用高亮
                dataSet.setDrawHorizontalHighlightIndicator(true);
                dataSet.setDrawVerticalHighlightIndicator(true);
                dataSet.setHighLightColor(color);
                dataSet.setHighlightLineWidth(1.5f);

                dataSets.add(dataSet);
                dataSetCount++;
            }
        }

        if (dataSets.isEmpty()) {
            binding.lineChart.setNoDataText("没有有效的心情数据");
            return;
        }

        LineData lineData = new LineData(dataSets);

        // 设置数据
        binding.lineChart.setData(lineData);

        // 修复：设置X轴标签格式化器 - 使用真实日期而不是数字索引
        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < dates.size()) {
                    // 使用统一的日期格式化方法
                    return formatChartDate(dates.get(index));
                }
                return "";
            }
        });

        // 设置X轴标签数量，避免过于拥挤
        if (dates.size() > 8) {
            xAxis.setLabelCount(8, true);
        } else {
            xAxis.setLabelCount(dates.size(), true);
        }

        // 强制设置X轴范围
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(dates.size() - 0.5f);

        // 刷新图表
        binding.lineChart.invalidate();

        Log.i(TAG, "折线图更新完成，数据集数量: " + dataSets.size() + ", 日期数量: " + dates.size());
        Log.i(TAG, "X轴范围: " + xAxis.getAxisMinimum() + " - " + xAxis.getAxisMaximum());
    }

    // 统一的图表日期格式化方法
    private String formatChartDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }

        try {
            // 尝试解析标准日期格式
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            // 如果解析失败，尝试其他常见格式
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException e2) {
                // 最后尝试：如果包含日期部分，提取月份和日期
                if (dateString.length() >= 10) {
                    String month = dateString.substring(5, 7);
                    String day = dateString.substring(8, 10);
                    // 去除前导零
                    if (month.startsWith("0")) month = month.substring(1);
                    if (day.startsWith("0")) day = day.substring(1);
                    return month + "/" + day;
                }
                // 返回原始字符串的后5位（通常是MM-dd）
                return dateString.length() > 5 ? dateString.substring(5) : dateString;
            }
        }
    }

    private void updatePieChart(List<MoodChartData> chartData) {
        if (chartData == null || chartData.isEmpty()) {
            Log.w(TAG, "饼图数据为空");
            binding.pieChart.clear();
            binding.pieChart.setNoDataText("暂无情绪数据");
            binding.pieChart.setNoDataTextColor(Color.GRAY);
            clearPieLegend();
            return;
        }

        Log.i(TAG, "开始更新饼图，数据条数: " + chartData.size());

        // 计算每种心情的总数
        Map<String, Float> moodTotals = new HashMap<>();
        float totalCount = 0;

        for (MoodChartData data : chartData) {
            if (data.getMood() != null && data.getCount() != null) {
                // 处理心情标签（可能包含多个心情）
                String[] moods = data.getMood().split(",");
                for (String mood : moods) {
                    String trimmedMood = mood.trim();
                    float count = data.getCount().floatValue();

                    moodTotals.put(trimmedMood, moodTotals.getOrDefault(trimmedMood, 0f) + count);
                    totalCount += count;
                }
            }
        }

        // 创建饼图数据条目
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (Map.Entry<String, Float> entry : moodTotals.entrySet()) {
            String mood = entry.getKey();
            float count = entry.getValue();

            // 只添加有数据的心情
            if (count > 0 && moodColors.containsKey(mood)) {
                float percentage = (count / totalCount) * 100;
                entries.add(new PieEntry(count, mood));
                colors.add(moodColors.get(mood));

                Log.d(TAG, "饼图数据 - 心情: " + mood + ", 数量: " + count + ", 占比: " + percentage + "%");
            }
        }

        if (entries.isEmpty()) {
            binding.pieChart.setNoDataText("没有有效的心情数据");
            clearPieLegend();
            return;
        }

        // 创建数据集
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f); // 扇形间距
        dataSet.setSelectionShift(5f); // 选中时突出距离

        // 设置数值显示
        dataSet.setValueLinePart1OffsetPercentage(80f);
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setValueLineColor(Color.BLACK);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueFormatter(new PercentFormatter(binding.pieChart)); // 显示百分比

        // 创建数据对象
        PieData pieData = new PieData(dataSet);
        binding.pieChart.setData(pieData);

        // 刷新图表
        binding.pieChart.invalidate();

        Log.i(TAG, "饼图更新完成，扇形数量: " + entries.size() + ", 总记录数: " + totalCount);

        // 更新中心文本
        binding.pieChart.setCenterText(String.format(Locale.getDefault(), "心情分布\n总计: %.0f次", totalCount));

        // 更新自定义图例
        updatePieLegend(moodTotals, totalCount);
    }

    private void updatePieLegend(Map<String, Float> moodTotals, float totalCount) {
        LinearLayout legendContainer = binding.pieLegendContainer;
        legendContainer.removeAllViews();

        // 添加标题
        TextView title = new TextView(this);
        title.setText("图例说明");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#333333"));
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.bottomMargin = dpToPx(8);
        legendContainer.addView(title, titleParams);

        // 创建水平布局容器用于图例项
        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setGravity(Gravity.CENTER);

        // 创建两个垂直列
        LinearLayout column1 = new LinearLayout(this);
        column1.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams col1Params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        horizontalLayout.addView(column1, col1Params);

        LinearLayout column2 = new LinearLayout(this);
        column2.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams col2Params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        horizontalLayout.addView(column2, col2Params);

        // 添加每种心情的图例
        int index = 0;
        for (Map.Entry<String, Integer> colorEntry : moodColors.entrySet()) {
            String mood = colorEntry.getKey();
            int color = colorEntry.getValue();

            if (moodTotals.containsKey(mood) && moodTotals.get(mood) > 0) {
                float count = moodTotals.get(mood);
                float percentage = (count / totalCount) * 100;

                LinearLayout legendItem = new LinearLayout(this);
                legendItem.setOrientation(LinearLayout.HORIZONTAL);
                legendItem.setGravity(Gravity.CENTER_VERTICAL);
                legendItem.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));

                // 颜色方块
                View colorView = new View(this);
                colorView.setBackgroundColor(color);
                LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(dpToPx(16), dpToPx(16));
                colorParams.rightMargin = dpToPx(8);
                legendItem.addView(colorView, colorParams);

                // 文字说明
                TextView legendText = new TextView(this);
                String legendStr = String.format(Locale.getDefault(), "%s: %.0f次", mood, count);
                legendText.setText(legendStr);
                legendText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                legendText.setTextColor(Color.parseColor("#666666"));
                legendText.setSingleLine(true);
                legendItem.addView(legendText, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

                // 根据索引分配到不同列
                if (index % 2 == 0) {
                    column1.addView(legendItem);
                } else {
                    column2.addView(legendItem);
                }
                index++;
            }
        }

        legendContainer.addView(horizontalLayout);
    }

    private void clearPieLegend() {
        LinearLayout legendContainer = binding.pieLegendContainer;
        legendContainer.removeAllViews();

        TextView emptyText = new TextView(this);
        emptyText.setText("暂无数据");
        emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        emptyText.setTextColor(Color.parseColor("#999999"));
        emptyText.setGravity(Gravity.CENTER);
        legendContainer.addView(emptyText);
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    // 饼图点击事件
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e instanceof PieEntry) {
            PieEntry pieEntry = (PieEntry) e;
            String label = pieEntry.getLabel();
            float value = pieEntry.getValue();
            float percentage = (value / binding.pieChart.getData().getYValueSum()) * 100;

            Toast.makeText(this,
                    label + "\n数量: " + (int)value + "次\n占比: " + String.format(Locale.getDefault(), "%.1f%%", percentage),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected() {
        // 什么都不选时的回调
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataUpdateReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(dataUpdateReceiver);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    // 添加状态栏设置方法
    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, com.example.qxb.R.color.colorPrimary));

            // 设置状态栏文字颜色为浅色（白色）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int flags = window.getDecorView().getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(flags);
            }
        }
    }
}