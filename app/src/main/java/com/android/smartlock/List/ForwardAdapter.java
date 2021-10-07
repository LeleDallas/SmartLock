package com.android.smartlock.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.android.smartlock.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class ForwardAdapter extends ArrayAdapter {

    private List<String> appName , appPackage;
    private List<Drawable> appIcon;
    private LayoutInflater inflater;
    private SharedPreferences sh;

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public ForwardAdapter(Context context, List<String> appName,List<String> appPackage,List<Drawable> appIcon ) {
        super(context, R.layout.row_list);
        this.appName = appName;
        this.appIcon = appIcon;
        this.appPackage = appPackage;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return appName.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.row_list, null);
        } else {
            view = convertView;
        }
        SharedPreferences sh = view.getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        Set<String> hs = sh.getStringSet("config", new HashSet<>());
        // Lookup view for data population
        Button btn = view.findViewById(R.id.button);
        if(hs.contains(appPackage.get(position)))
            btn.setText("Remove");
        else
            btn.setText("Add");

        btn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sh.edit();
            Set<String> in = new HashSet<>(sh.getStringSet("config", new HashSet<>()));
            if(btn.getText()=="Remove") {
                in.remove(appPackage.get(position));
                btn.setText("Add");
            }else{
                in.add(appPackage.get(position));
                btn.setText("Remove");
            }
            editor.putStringSet("config", in);
            editor.apply();
        });
        ImageView img= view.findViewById(R.id.icon_list);
        img.setImageDrawable(appIcon.get(position));
        setMargins(view, 150, 510, 50, 50);
        return view;
    }

    @Override
    public String getItem(int position) {
        return appName.get(position);
    }
}
