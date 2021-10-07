package com.android.smartlock.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.smartlock.Global.Global;
import com.android.smartlock.R;
import com.ashiqurrahman.rangedtimepickerdialog.library.TimeRangePickerDialog;
import com.google.gson.Gson;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

import static android.content.Context.MODE_PRIVATE;


public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private List<String> mData, pk;
    private List<Drawable> imgs;
    private List<String> times;
    private LayoutInflater mInflater;
    private SharedPreferences sh;
    Global global;

    // data is passed into the constructor
    public AppListAdapter(Context context, List<String> data, List<Drawable>imgs, List<String> times,  List<String> pk,  Global global) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.imgs = imgs;;
        this.times = times;
        this.global=global;
        this.pk=pk;
    }

    // inflates the row layout
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_row, parent, false);
        //store preference
        this.sh = mInflater.getContext().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    // binds the data to components in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.myTextView.setText(mData.get(position));
        holder.icon.setImageDrawable(imgs.get(position));
        holder.time.setText(times.get(position));
        holder.pk= pk.get(position);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        TextView time;
        ImageView icon;
        Switch active;
        String pk;
        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.App_name);
            time=itemView.findViewById(R.id.App_time);
            icon= itemView.findViewById(R.id.AppIcon);
            active= itemView.findViewById(R.id.switch1);
            active.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    TimeRangePickerDialog dialog = new TimeRangePickerDialog(
                            (i, i1, i2, i3) -> {
                                MaterialDialog mDialog = new MaterialDialog.Builder(global.getActivity())
                                        .setTitle("Apply Configuration")
                                        .setMessage("Are you sure to set a \nDaily Usage Time of "+i+"h "+i1+ "m \nand a LOCK TIME for " +i2+"h"+i3+ "m \nfor "+myTextView.getText()+"?")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", R.drawable.ic_block, (dialogInterface, which) -> {
                                            //ADD DATA
                                            Time t1 = Time.valueOf(i+":"+i1+":00");
                                            Time t2 = Time.valueOf(i2+":"+i3+":00");
                                            Long l1 = t1.getTime();
                                            Long l2 = t2.getTime();
                                            List<String> list = new ArrayList<>();
                                            list.add(pk);
                                            list.add(l1+"");
                                            list.add(l2+"");
                                            Gson gson= new Gson();
                                            String json= gson.toJson(list);
                                            SharedPreferences sh = global.getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sh.edit();
                                            editor.putString("configLock", json);
                                            editor.apply();
                                            String a = sh.getString("configLock", "");
                                            Log.i("CONGI", a);
                                            dialogInterface.dismiss();
                                        })
                                        .setNegativeButton("Cancel", R.drawable.ic_close, (dialogInterface, which) -> {
                                            active.setChecked(false);
                                            dialogInterface.dismiss();
                                        })
                                        .build();
                                mDialog.show();
                            },
                            "Set Max Usage Time",
                            "Set Lock Time",
                            true
                    );
                    dialog.setCancelable(false);
                    dialog.isCancelable();
                    dialog.show(((FragmentActivity)itemView.getContext()).getSupportFragmentManager(), "datePicker");
                }
                else {
                    //REMOVE FROM CONFIG
                    SharedPreferences sh = global.getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sh.edit();
                    editor.remove(pk);
                    editor.apply();
                    String a = sh.getString("configLock", "");
                    Log.i("CONGI", a);
                }
            });
        }
    }
}