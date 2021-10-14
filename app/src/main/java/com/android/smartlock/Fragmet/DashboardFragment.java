package com.android.smartlock.Fragmet;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.smartlock.Global.Global;
import com.android.smartlock.List.ForwardAdapter;
import com.android.smartlock.List.IconAdapter;
import com.android.smartlock.R;
import com.google.gson.Gson;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.callback.OnPieSelectListener;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

import static android.content.Context.MODE_PRIVATE;


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

    class myInfo extends SimplePieInfo {
        String pack;

        myInfo(double value, int color,String  desc, String pack){
            super(value, color,desc);
            this.pack=pack;
        }
        private String getPackage(){
            return  pack;
        }
    }

    ImageButton addConfig;
    Button btnAdd;
    AnimatedPieView mAnimatedPieView;
    TextView usage ,usageTime;
    RecyclerView iconList;
    Switch quick_switch;

    public void setIconDrawableList(){
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        Set<String> hs = sh.getStringSet("config", new HashSet<String>());
        Log.e("ad", hs.toString());
        List<String> pk= new ArrayList<>();
        for (String i :hs) pk.add(i);
        IconAdapter a= new IconAdapter(getContext(),pk);
        iconList.setAdapter(a);
        iconList.setLayoutManager(new GridLayoutManager(getContext(), 5));
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addConfig= getView().findViewById(R.id.addConfig);
        btnAdd= getView().findViewById(R.id.button_add);
        mAnimatedPieView = getView().findViewById(R.id.animatedPieView);
        usage= getView().findViewById(R.id.text_usage);
        usageTime= getView().findViewById(R.id.text_usage_desc);
        iconList= getView().findViewById(R.id.listIcon);
        quick_switch= getView().findViewById(R.id.quick_switch);
        setIconDrawableList();
        quick_switch.setOnClickListener( v -> {
            if(quick_switch.isChecked()){
                MaterialDialog mDialog = new MaterialDialog.Builder(getActivity())
                        .setTitle("Apply Configuration")
                        .setMessage("Are you sure to set a \nDaily Usage Time of 1h 0m \nand a LOCK TIME for 1h 30m \nfor selected apps?")
                        .setCancelable(false)
                        .setPositiveButton("Ok", R.drawable.ic_block, (dialogInterface, which) -> {
                        /*ADD DATA
                        List<String> list = new ArrayList<>();
                        Gson gson= new Gson();
                        String json= gson.toJson(list);
                        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sh.edit();
                        editor.putString("configLock", json);
                        editor.apply();
                        String a = sh.getString("configLock", "");
                        Log.i("CONGI", a);*/
                            quick_switch.setChecked(true);
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("Cancel", R.drawable.ic_close, (dialogInterface, which) -> {
                            quick_switch.setChecked(false);
                            dialogInterface.dismiss();
                        })
                        .build();
                mDialog.show();
            }
        });
        final PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Global g= new Global();
        g.getData(pm,packages, getActivity(), getContext());
        btnAdd.setOnClickListener(v -> click(g));
        addConfig.setOnClickListener(v -> click(g));
        if(isEmpty()) {
            mAnimatedPieView.setVisibility(View.INVISIBLE);
            btnAdd.setVisibility(View.VISIBLE);
        }
        else {
            mAnimatedPieView.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.INVISIBLE);
            refresh(g);
        }
    }

    private void click(Global g) {
        ForwardAdapter adapter= new ForwardAdapter(getContext(), g.getApps(), g.getPackageName(),g.getImgs());
        DialogPlus dialog = DialogPlus.newDialog(requireContext())
                .setContentHeight( getActivity().getWindowManager().getDefaultDisplay().getHeight())
                .setAdapter(adapter)
                .setHeader(R.layout.header)
                .setFooter(R.layout.footer).setOnClickListener((dialog1, view1) -> {
                    Button closeX= view1.findViewById(R.id.close_x);
                    if(closeX!=null)
                        closeX.setOnClickListener(v1 ->  dialog1.dismiss());
                })
                .setPadding(120,20,120,20)
                .setContentHolder(new ListHolder())
                .setOnItemClickListener((dialog1, item, view1, position) -> {
                })
                .setCancelable(true)
                .setExpanded(true)
                .setOnCancelListener(dialog1 -> {
                    if(isEmpty()) {
                        mAnimatedPieView.setVisibility(View.INVISIBLE);
                        btnAdd.setVisibility(View.VISIBLE);
                        setIconDrawableList();
                    }
                    else {
                        mAnimatedPieView.setVisibility(View.VISIBLE);
                        btnAdd.setVisibility(View.INVISIBLE);
                        refresh(g);
                        setIconDrawableList();
                    }
                })
                .create();
        dialog.show();
    }


    private boolean isEmpty() {
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        Set<String> hs = sh.getStringSet("config", new HashSet<String>());
        // Log.i("TAG", hs.size()+"");
        return hs.size()==0;
    }


    public void  refresh(Global g) {
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        //fetch data and insert
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        Set<String> hs = sh.getStringSet("config", new HashSet<String>());
        for (String val:hs) {
            String name="";
            if (g.getPackageName().contains(val))
                name=  g.getApps().get(g.getPackageName().indexOf(val));

            Long totalTimeUsageInMillis= 0L;
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getContext().getSystemService(Context.USAGE_STATS_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            long start = calendar.getTimeInMillis();
            long end = System.currentTimeMillis();
            Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.queryAndAggregateUsageStats(start,end );
            if (lUsageStatsMap.get(val)!=null)
                totalTimeUsageInMillis = lUsageStatsMap.get(val).getTotalTimeInForeground();
            if(totalTimeUsageInMillis==0)
                continue;
            config.addData(new myInfo(totalTimeUsageInMillis, g.getDominantColor(g.resourceToBitmap(val, getActivity()) ), name, val)
                    .setLabel(g.resourceToBitmap(val, getActivity()))
                    .setIconHeight(80));
        }
        config.startAngle(-90)// Starting angle offset
                .pieRadius(260)// Set chart radius
                .floatShadowRadius(98f)// Selected pie's shadow of expansion
                .strokeWidth(10)// Stroke width for ring-chart
                .floatExpandSize(18f)
                .textGravity(AnimatedPieViewConfig.ECTOPIC)
                .splitAngle(1)
                .canTouch(true)
                .drawText(true)// Whether to draw a text description
                .selectListener((OnPieSelectListener<myInfo>) (pieInfo, isFloatUp) -> {
                    if (!isFloatUp) {
                        usage.setText("");
                        usageTime.setText("");
                    }   else{
                        Long totalTimeUsageInMillis= 0L;
                        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getContext().getSystemService(Context.USAGE_STATS_SERVICE);
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        long start = calendar.getTimeInMillis();
                        long end = System.currentTimeMillis();
                        Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.queryAndAggregateUsageStats(start,end );
                        if (lUsageStatsMap.get(pieInfo.getPackage())!=null)
                            totalTimeUsageInMillis = lUsageStatsMap.get(pieInfo.getPackage()).getTotalTimeInForeground();
                        usage.setText(pieInfo.getDesc());
                        usageTime.setText(g.converLongToTimeChar(totalTimeUsageInMillis));
                    }
                })// Click callback
                .duration(2000);// draw pie animation duration
        mAnimatedPieView.applyConfig(config);
        mAnimatedPieView.start();

    }
}