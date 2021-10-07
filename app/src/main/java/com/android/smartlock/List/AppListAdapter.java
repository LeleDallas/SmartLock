package com.android.smartlock.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.smartlock.Global.Global;
import com.android.smartlock.MainActivity;
import com.android.smartlock.R;
import com.ashiqurrahman.rangedtimepickerdialog.library.TimeRangePickerDialog;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;



public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private List<String> mData;
    private List<Drawable> imgs;
    private List<String> times;
    private LayoutInflater mInflater;
    private SharedPreferences sh;
    private String name;

    // data is passed into the constructor
    public AppListAdapter(Context context, List<String> data, List<Drawable>imgs, List<String> times) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.imgs = imgs;;
        this.times = times;
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
        name=mData.get(position);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView time;
        ImageView icon;
        Switch active;
        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.App_name);
            time=itemView.findViewById(R.id.App_time);
            icon= itemView.findViewById(R.id.AppIcon);
            active= itemView.findViewById(R.id.switch1);
            // itemView.setOnClickListener(this);
            active.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    TimeRangePickerDialog dialog = new TimeRangePickerDialog(
                            (i, i1, i2, i3) -> {
                                Log.i("EVENT", "start"+i+i1+"end"+i2+i3);
                                new AlertDialog.Builder(itemView.getContext())
                                        .setTitle("Apply Configuration")
                                        .setMessage("Are you sure to set a daily USAGE TIME of "+i+"h "+i1+ "m and a LOCK TIME for " +i2+"h"+i3+ "m for "+name+"?")
                                                .setNegativeButton(android.R.string.cancel,(dialog1, which) -> {active.setChecked(false); })
                                                .setPositiveButton(android.R.string.ok, (dialog1, which) -> {
                                                    //add data to shared pref

                                                })
                                                .create()
                                                .show();
                            },
                            "Set Max Usage Time",
                            "Set Lock Time",
                            true
                    );
                    dialog.setCancelable(false);
                    dialog.show(((FragmentActivity)itemView.getContext()).getSupportFragmentManager(), "my-dialog-tag-string");
                }
                else {

                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View view) {
            Global g= new Global();

        }
    }

}