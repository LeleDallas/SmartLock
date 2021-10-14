package com.android.smartlock.Global;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.palette.graphics.Palette;

import com.android.smartlock.List.AppListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Global {
    ArrayList<Drawable> imgs;
    ArrayList<String> times, packageName,apps;
    PackageManager pm;
    Activity activity;
    public ArrayList<String> getApps() {
        return apps;
    }

    public ArrayList<Drawable> getImgs() {
        return imgs;
    }

    public ArrayList<String> getTimes() {
        return times;
    }

    public ArrayList<String> getPackageName() {
        return packageName;
    }

    public void setActivity(Activity activity){
        this.activity=activity;
    }
    public Activity getActivity(){return  this.activity;}

    public Global(){
        apps = new ArrayList<>();
        imgs= new ArrayList<>();
        times= new ArrayList<>();
        packageName= new ArrayList<>();
    }

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



    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;
        if(drawable==null)
            return null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, 100, 100);
        drawable.draw(canvas);
        return bitmap;
    }


    public Bitmap resourceToBitmap(String resid, Activity activity) {
        Drawable icon = null;
        try {
            icon = activity.getApplicationContext().getPackageManager().getApplicationIcon(resid);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return drawableToBitmap(icon);
    }

    public static int getDominantColor(Bitmap bitmap) {
        if(bitmap==null)
            return 0;
        Palette palette = Palette.generate(bitmap);
        int vibrant = palette.getVibrantColor(0x000000);
        return vibrant;
    }

    public void getData(PackageManager pm, List<ApplicationInfo> packages, Activity activity, Context context){
        apps = new ArrayList<>();
        imgs= new ArrayList<>();
        times= new ArrayList<>();
        this.pm=pm;
        packageName= new ArrayList<>();
        for (ApplicationInfo packageInfo : packages) {
            //get installed
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
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
                    imgs.add(activity.getApplicationContext().getPackageManager().getApplicationIcon(packageInfo.packageName));
                    //Log.i("TAG", packageInfo.packageName);
                }catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                packageName.add(packageInfo.packageName);
            }
        }
    }



    public void getData( PackageManager pm, List<ApplicationInfo> packages, int year, int month, int day, Activity activity, Context context){
        apps = new ArrayList<>();
        imgs= new ArrayList<>();
        times= new ArrayList<>();
        packageName= new ArrayList<>();
        this.pm=pm;
        month+=1;
        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
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
                    imgs.add(activity.getApplicationContext().getPackageManager().getApplicationIcon(packageInfo.packageName));
                }catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                packageName.add(packageInfo.packageName);
            }
        }
    }

    public static int lcm(int... input) {
        int result = input[0];
        for(int i = 1; i < input.length; i++) result = lcm(result, input[i]);
        return result;
    }

    public String getNameFromPackage(String val) {
        ApplicationInfo app = null;
        try {
            app = pm.getApplicationInfo(val, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  "com.com.cpm";    }


}
