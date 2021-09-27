package com.android.smartlock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.android.smartlock.Fragmet.DashboardFragment;
import com.android.smartlock.List.AppListAdapter;
import com.android.smartlock.Fragmet.LockFragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {

    private Fragment fragment=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}