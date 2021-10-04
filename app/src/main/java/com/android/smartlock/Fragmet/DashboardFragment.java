package com.android.smartlock.Fragmet;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.smartlock.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.futured.donut.DonutProgressView;
import app.futured.donut.DonutSection;


public class DashboardFragment extends Fragment {



    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkForPermission(getContext()))
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DonutProgressView dpvChart = getView().findViewById(R.id.donut_view);

        DonutSection section = new DonutSection("Section 1 Name", Color.parseColor("#a6a184"), 20.0f);
        DonutSection section1 = new DonutSection("Section 2 Name", Color.parseColor("#bdbdc7"), 10.0f);
        DonutSection section2 = new DonutSection("Section 3 Name", Color.parseColor("#000036"), 10.0f);
        DonutSection section3 = new DonutSection("Section 4 Name", Color.parseColor("#ff1136"), 10.0f);
        dpvChart.setCap(100f);
        ArrayList<DonutSection> list = new ArrayList<>(Collections.singleton(section));
        list.add(section1);
        list.add(section2);
        list.add(section3);

        dpvChart.submitData(list);


    }
}