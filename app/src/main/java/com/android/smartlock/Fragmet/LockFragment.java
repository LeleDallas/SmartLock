package com.android.smartlock.Fragmet;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import com.android.smartlock.List.AppListAdapter;
import com.android.smartlock.R;
import com.github.badoualy.datepicker.DatePickerTimeline;

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


    public String converLongToTimeChar(long usedTime) {
        String hour="", min="", sec="";

        int h=(int)(usedTime/1000/60/60);
        if (h!=0)
            hour = h+"h ";

        int m=(int)((usedTime/1000/60) % 60);
        if (m!=0)
            min = m+"m ";

        int s=(int)((usedTime/1000) % 60);
        if (s==0 && (h!=0 || m!=0))
            sec="";
        else
            sec = s+"s";

        return hour+min+sec;
    }
    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lock, container, false);
    }

    RecyclerView recyclerView;
    AppListAdapter adapter;
    int flags = PackageManager.GET_META_DATA |
            PackageManager.GET_SHARED_LIBRARY_FILES |
            PackageManager.GET_UNINSTALLED_PACKAGES;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final PackageManager pm = getActivity().getPackageManager();
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.list_app);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        getData(pm,packages);

        DatePickerTimeline timeline=getView().findViewById(R.id.timeline);
        Calendar currentDate = Calendar.getInstance();
        int years = currentDate.get(Calendar.YEAR);
        int months = currentDate.get(Calendar.MONTH);
        int days = currentDate.get(Calendar.DAY_OF_MONTH);
        timeline.setFirstVisibleDate(years, months, days-7);
        timeline.setSelectedDate(years, months, days);
        timeline.setLastVisibleDate(years, months, days);
        timeline.setOnDateSelectedListener((year, month, day, index) -> {
            getData(pm, packages,year,month,day);
        });
    }

    public void getData( PackageManager pm, List<ApplicationInfo> packages){
        ArrayList<String> apps = new ArrayList<>();
        ArrayList<Drawable> imgs= new ArrayList<>();
        ArrayList<String> times= new ArrayList<>();
        for (ApplicationInfo packageInfo : packages) {
            //get installed
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                UsageStatsManager usageStatsManager = (UsageStatsManager) getContext().getSystemService(Context.USAGE_STATS_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                long start = calendar.getTimeInMillis();
                long end = System.currentTimeMillis();
                Map<String, UsageStats> stats = usageStatsManager.queryAndAggregateUsageStats(start, end);
                Long totalTimeUsageInMillis= 0L;
                if (stats.get(packageInfo.packageName)!=null)
                    totalTimeUsageInMillis = stats.get(packageInfo.packageName).getTotalTimeInForeground();
                times.add("Usage time: "+ converLongToTimeChar(totalTimeUsageInMillis));
                apps.add((String) pm.getApplicationLabel(packageInfo));
                try {
                    imgs.add(getActivity().getApplicationContext().getPackageManager().getApplicationIcon(packageInfo.packageName));
                }catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        adapter = new AppListAdapter(getContext(), apps, imgs,times);
        recyclerView.setAdapter(adapter);
    }

    public void getData( PackageManager pm, List<ApplicationInfo> packages, int year, int month, int day){
        ArrayList<String> apps = new ArrayList<>();
        ArrayList<Drawable> imgs= new ArrayList<>();
        ArrayList<String> times= new ArrayList<>();
        month+=1;
        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                UsageStatsManager usageStatsManager = (UsageStatsManager) getContext().getSystemService(Context.USAGE_STATS_SERVICE);
                String strThatDay = year+"/"+month+"/"+ day;
                Log.i("DATE", strThatDay);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                Date d = null;
                try {d = formatter.parse(strThatDay); } catch (ParseException e) {e.printStackTrace(); }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d);
                calendar.add(Calendar.DATE, -1);
                long start = calendar.getTimeInMillis();
                calendar.add(Calendar.DATE, +1);
                long end = calendar.getTimeInMillis();
                Map<String, UsageStats> stats = usageStatsManager.queryAndAggregateUsageStats(start, end);
                Long totalTimeUsageInMillis= 0L;
                if (stats.get(packageInfo.packageName)!=null)
                    totalTimeUsageInMillis = stats.get(packageInfo.packageName).getTotalTimeInForeground();
                times.add("Usage time: "+ converLongToTimeChar(totalTimeUsageInMillis));
                apps.add((String) pm.getApplicationLabel(packageInfo));
                try {
                    imgs.add(getActivity().getApplicationContext().getPackageManager().getApplicationIcon(packageInfo.packageName));
                }catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        adapter = new AppListAdapter(getContext(), apps, imgs,times);
        recyclerView.setAdapter(adapter);
    }
}