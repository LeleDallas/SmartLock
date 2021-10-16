package com.android.smartlock.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.smartlock.MainActivity;
import com.android.smartlock.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private List<String> imgs;

    public IconAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.imgs = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.image_icon_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            Drawable icon =holder.itemView.getContext().getPackageManager().getApplicationIcon(imgs.get(position));
            holder.icon.setImageDrawable(icon);
            holder.pk= imgs.get(position);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return imgs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        String pk;
        ViewHolder(View itemView) {
            super(itemView);
            icon= itemView.findViewById(R.id.imgIcon);
            icon.setOnClickListener(v -> {
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent launchIntent = mInflater.getContext().getPackageManager().getLaunchIntentForPackage(pk);
                        mInflater.getContext().startActivity(launchIntent);                    }
                }, 10000);
            });
        }
    }
}