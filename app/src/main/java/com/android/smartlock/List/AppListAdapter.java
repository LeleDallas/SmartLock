package com.android.smartlock.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import com.android.smartlock.MainActivity;
import com.android.smartlock.R;

import java.util.List;



public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private List<String> mData;
    private List<Drawable> imgs;
    private List<String> times;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private SharedPreferences sh;

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


        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.App_name);
            time=itemView.findViewById(R.id.App_time);
            icon= itemView.findViewById(R.id.AppIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            int a = sh.getInt("test", 0)+1;
            SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putInt("test", a);
            myEdit.commit();
            Toast.makeText(view.getContext(),"a:"+a, Toast.LENGTH_LONG).show();
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}