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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.smartlock.Global.Global;
import com.android.smartlock.List.ForwardAdapter;
import com.android.smartlock.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.callback.OnPieSelectListener;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton addConfig= getView().findViewById(R.id.addConfig);
        final PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Global g= new Global();
        g.getData(pm,packages, getActivity(), getContext());
        addConfig.setOnClickListener(v -> {
            ForwardAdapter adapter= new ForwardAdapter(getContext(), g.getApps(), g.getPackageName(),g.getImgs());
            DialogPlus dialog = DialogPlus.newDialog(requireContext())
                    .setContentHeight( getActivity().getWindowManager().getDefaultDisplay().getHeight())
                    .setAdapter(adapter)
                    .setHeader(R.layout.header).setOnClickListener((dialog1, view1) -> {
                        ImageButton closeX= view1.findViewById(R.id.close_xI);
                        if(closeX!=null)
                            closeX.setOnClickListener(l ->  dialog1.dismiss());
                    })
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
                        refresh(g);
                    })
                    .create();
            dialog.show();
        });

    }


    public void  refresh(Global g) {
        AnimatedPieView mAnimatedPieView = getView().findViewById(R.id.animatedPieView);
        TextView usage= getView().findViewById(R.id.text_usage);
        TextView usageTime= getView().findViewById(R.id.text_usage_desc);
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        //fetch data and insert
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        Set<String> hs = sh.getStringSet("config", new HashSet<String>());
        for (String val:hs) {
            config.addData(new myInfo(2f, g.getDominantColor(g.resourceToBitmap(val, getActivity()) ), g.getNameFromPackage(val), val)
                    .setLabel(g.resourceToBitmap(val, getActivity()))
                    .setIconHeight(150));
        }



        config.startAngle(-90)// Starting angle offset
                .floatShadowRadius(8f)// Selected pie's shadow of expansion
                .strokeWidth(10)// Stroke width for ring-chart
                .guideLineWidth(5)
                .guideLineMarginStart(2)
                .setTextLineStartMargin(10)
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
                        totalTimeUsageInMillis = lUsageStatsMap.get(pieInfo.getPackage()).getTotalTimeInForeground();
                        if(totalTimeUsageInMillis==null)
                            totalTimeUsageInMillis=0L;
                        usage.setText(pieInfo.getDesc());
                        usageTime.setText(g.converLongToTimeChar(totalTimeUsageInMillis));
                    }
                })// Click callback
                .duration(2000);// draw pie animation duration
        mAnimatedPieView.applyConfig(config);
        mAnimatedPieView.start();

    }
}