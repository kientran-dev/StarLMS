package com.starlms.starlms.admin.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.starlms.starlms.R;

import java.util.ArrayList;

public class AdminDashboardFragment extends Fragment {

    private BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart = view.findViewById(R.id.bar_chart_users);
        setupBarChart();
    }

    private void setupBarChart() {
        // Dữ liệu cho cột "Đi học"
        ArrayList<BarEntry> attendedEntries = new ArrayList<>();
        attendedEntries.add(new BarEntry(0, 85));
        attendedEntries.add(new BarEntry(1, 90));
        attendedEntries.add(new BarEntry(2, 88));
        attendedEntries.add(new BarEntry(3, 92));
        attendedEntries.add(new BarEntry(4, 80));

        // Dữ liệu cho cột "Nghỉ học"
        ArrayList<BarEntry> absentEntries = new ArrayList<>();
        absentEntries.add(new BarEntry(0, 15));
        absentEntries.add(new BarEntry(1, 10));
        absentEntries.add(new BarEntry(2, 12));
        absentEntries.add(new BarEntry(3, 8));
        absentEntries.add(new BarEntry(4, 20));

        // Tạo DataSet cho mỗi nhóm
        BarDataSet attendedDataSet = new BarDataSet(attendedEntries, "Đi học");
        attendedDataSet.setColor(Color.rgb(104, 241, 175));

        BarDataSet absentDataSet = new BarDataSet(absentEntries, "Nghỉ học");
        absentDataSet.setColor(Color.rgb(255, 102, 0));

        BarData barData = new BarData(attendedDataSet, absentDataSet);
        barData.setValueTextColor(Color.BLACK);
        barData.setValueTextSize(10f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        // Cấu hình trục X
        final String[] days = new String[]{"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);

        // Cấu hình vị trí của Chú thích (Legend)
        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
        legend.setYOffset(10f);

        // Cài đặt cho việc nhóm cột
        float groupSpace = 0.1f;
        float barSpace = 0.05f;
        float barWidth = 0.4f;

        barData.setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * 5);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.invalidate();
    }
}
