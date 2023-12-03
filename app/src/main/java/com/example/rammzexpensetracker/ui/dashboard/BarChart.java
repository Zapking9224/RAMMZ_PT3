package com.example.rammzexpensetracker.ui.dashboard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.rammzexpensetracker.R;


import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BarChart extends LinearLayout {

    private TextView labelTextView;
    private ProgressBar progressBar;
    private TextView valueTextView;

    public BarChart(Context context) {
        super(context);
        init(context);
    }

    public BarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void SetData(String label, int progress, String category) {
        labelTextView.setText(label);
        progressBar.setProgress(progress);

        String progressText = getContext().getString(R.string.progress_text, progress);
        valueTextView.setText(progressText);
        progressBar.setProgressTintList(ColorStateList.valueOf(GetBarColor(category)));
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.bar_chart, this, true);

        labelTextView = findViewById(R.id.categoryText);
        progressBar = findViewById(R.id.expenseProgressBar);
        valueTextView = findViewById(R.id.percentageText);

        // You can customize the initialization or set listeners here
    }

    // Provide methods to update the label, progress, and value
    public void setLabel(String label) {
        labelTextView.setText(label);
    }

    public Integer GetBarColor(String category) {
        Map<String, Integer> categoryColors = new HashMap<>();

        categoryColors.put("Food", Color.rgb(255, 0, 0)); // Red
        categoryColors.put("Housing", Color.rgb(255, 165, 0)); // Orange
        categoryColors.put("Transportation", Color.rgb(255, 255, 0)); // Yellow
        categoryColors.put("Entertainment", Color.rgb(0, 128, 0)); // Green
        categoryColors.put("Fitness", Color.rgb(0, 0, 255)); // Blue
        categoryColors.put("Travel", Color.rgb(128, 0, 128)); // Purple
        categoryColors.put("Education", Color.rgb(255, 192, 203)); // Pink
        categoryColors.put("Taxes", Color.rgb(128, 128, 128)); // Gray
        categoryColors.put("Insurance", Color.rgb(0, 255, 255)); // Cyan
        categoryColors.put("Other", Color.rgb(255, 99, 71)); // Tomato

        return categoryColors.get(category);
    }
}
