package com.example.cookease;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeAdapter extends ArrayAdapter<Recipe>{

    Context context; 
    int layoutResourceId;
    ArrayList<Recipe> data;
    
    public RecipeAdapter(Context context, int layoutResourceId, ArrayList<Recipe> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecipeHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new RecipeHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.recipe_image);
            holder.txtTitle = (TextView)row.findViewById(R.id.recipe_title);
            
            row.setTag(holder);
        }
        else
        {
            holder = (RecipeHolder)row.getTag();
        }
        
        Recipe recipe = data.get(position);
        holder.txtTitle.setText(recipe.getName());
       // Log.w("RCPAdapter", "TITLE: " + recipe.getName());
        if(!recipe.getPic().equals(""))
        {
        	//imgIcon.setImageBitmap(BitmapFactory.decodeFile("/storage/emulated/0/Pictures/JPEG_19691231_190000_-495578489.jpg"));
        	holder.imgIcon.setImageBitmap(BitmapFactory.decodeFile(recipe.getPic()));
        } else {
        	holder.imgIcon.setImageResource(R.drawable.default_cookease_icon);
        }
        
        //holder.imgIcon.setImageBitmap(BitmapFactory.decodeFile(recipe.getPic()));
        //Log.w("RCPAdapter", "IMG: " + recipe.getPic());
        
        return row;
    }
    
    static class RecipeHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}