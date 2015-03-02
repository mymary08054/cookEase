package com.example.cookease;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ShoppingArrayAdapter extends ArrayAdapter<Ingredient> implements View.OnCreateContextMenuListener {
	
	private Context context;
	private int resource;
	private Comparator<Ingredient> comparator = null;
	private ArrayList<Ingredient> food;
	
	public ShoppingArrayAdapter(Context context, int i, ArrayList<Ingredient> a, Comparator<Ingredient> c) {
		super(context, i, a);
		this.context = context;
		this.resource = i;
		this.food = a;
		this.comparator = c;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resource, parent, false);
		}
		
		row.setOnCreateContextMenuListener(null);
		
		final Ingredient i = food.get(position);
		if (i != null) {
			TextView name = (TextView) row.findViewById(R.id.shopping_food_name);
			name.setText(i.getName());
			final CheckBox checkbox = (CheckBox) row.findViewById(R.id.checkbox);
			checkbox.setOnClickListener(new CompoundButton.OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean checked = checkbox.isChecked();
					if (checked) {
						i.setWant(true);
					} else {
						i.setWant(false);
					}
				}
			});
			if (i.getWant()) {
				checkbox.setChecked(true);
			} else {
				checkbox.setChecked(false);
			}
		}
		return row;
	}
	
	public void sort() {
		Collections.sort(food, comparator); // sort the list
		notifyDataSetChanged();
	}
	
	@Override
	public void add(Ingredient i) {
		food.add(i); // add food to the list
		sort(); // sort the list
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu arg0, View arg1,
			ContextMenuInfo arg2) {
		// TODO Auto-generated method stub
		
	}
}
