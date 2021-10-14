package com.android.smartlock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.smartlock.Fragmet.DashboardFragment;
import com.android.smartlock.Global.Global;
import com.android.smartlock.List.AppListAdapter;
import com.android.smartlock.Fragmet.LockFragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {

    private Fragment fragment=null;
    Intent mServiceIntent;
    private MyService mYourService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mYourService = new MyService();
        mServiceIntent = new Intent(this, mYourService.getClass());
        if (!isMyServiceRunning(mYourService.getClass())) {
            startService(mServiceIntent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnimatedBottomBar animatedBottomBar= findViewById(R.id.bottom_bar);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,  new DashboardFragment()).commit();
        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {
                switch (i){
                    case 0:
                        //Lock Page
                        fragment= new LockFragment();
                        break;
                    case 1:
                        //Dashboard
                        fragment= new DashboardFragment();
                        break;
                }
                if(fragment!=null)
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {

            }
        });
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetching the stored data
        // from the SharedPreference
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        int a = sh.getInt("test", 0);
        // Setting the fetched data
        // in the EditTexts
        //checkApp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
       // checkApp();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
      //  checkApp();
    }



}