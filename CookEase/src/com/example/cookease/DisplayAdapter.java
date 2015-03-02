package com.example.cookease;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DisplayAdapter extends ArrayAdapter<String>{

    Context context; 
    int layoutResourceId;
    ArrayList<String> data;
    
    public DisplayAdapter(Context context, int layoutResourceId, ArrayList<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DisplayHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new DisplayHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.rcp_txt);
            
            row.setTag(holder);
        }
        else
        {
            holder = (DisplayHolder)row.getTag();
        }
        
        String String = data.get(position);
        holder.txtTitle.setText(String);
        
        return row;
    }
    
    static class DisplayHolder
    {
        TextView txtTitle;
    }
}