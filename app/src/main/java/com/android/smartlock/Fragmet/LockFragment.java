package com.android.smartlock.Fragmet;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.smartlock.Global.Global;
import com.android.smartlock.List.AppListAdapter;
import com.android.smartlock.R;
import com.github.badoualy.datepicker.DatePickerTimeline;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LockFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lock, container, false);
    }

    RecyclerView recyclerView;
    AppListAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final PackageManager pm = getActivity().getPackageManager();
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.list_app);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Global g= new Global();
        g.getData(pm,packages, getActivity(),getContext());
        adapter = new AppListAdapter(getContext(), g.getApps(), g.getImgs(),g.getTimes());
        recyclerView.setAdapter(adapter);
        DatePickerTimeline timeline=getView().findViewById(R.id.timeline);
        Calendar currentDate = Calendar.getInstance();
        int years = currentDate.get(Calendar.YEAR);
        int months = currentDate.get(Calendar.MONTH);
        int days = currentDate.get(Calendar.DAY_OF_MONTH);
        timeline.setFirstVisibleDate(years, months, days-7);
        timeline.setSelectedDate(years, months, days);
        timeline.setLastVisibleDate(years, months, days);
        timeline.setOnDateSelectedListener((year, month, day, index) -> {
            g.getData(pm, packages,year,month,day,getActivity(), getContext());
            adapter = new AppListAdapter(getContext(),g.getApps(), g.getImgs(),g.getTimes());
            recyclerView.setAdapter(adapter);
        });
    }

}

