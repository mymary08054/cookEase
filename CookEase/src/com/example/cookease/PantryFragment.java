package com.example.cookease;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class PantryFragment extends Fragment {

	private ListView list;
	ArrayList<Ingredient> pantryItems;
	private ArrayAdapter<Ingredient> arrayAdapter;
	private Comparator<Ingredient> comparator;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);
	    View view = inflater.inflate(R.layout.pantry_view, container, false);
	    list = (ListView) view.findViewById(R.id.listview_pantry);
	    this.pantryItems = new ArrayList<Ingredient>();
	    for (Ingredient i : NavigationDrawer.db.getAllIngredients()) {
	    	pantryItems.add(i);
	    }
	    this.comparator = new foodComparator();
	    Collections.sort(pantryItems, comparator);
	    arrayAdapter = new PantryArrayAdapter(view.getContext(), 
	    		R.layout.food_list_item, pantryItems, new foodComparator());
	    list.setAdapter(arrayAdapter);
	    
	    setHasOptionsMenu(true);
	    
	    registerForContextMenu(list);
	    
	    if (pantryItems.size() == 0) {
			AlertDialog.Builder newbuilder = new AlertDialog.Builder(getActivity());
	    	newbuilder.setMessage("It looks like your pantry is empty! Add some items? =)");
	    	newbuilder.show();
	    }
	    
	    return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		populateList();
	}
		
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.pantry, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem m) {
		switch(m.getItemId()) {
		case R.id.action_add_food:
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			final View view = inflater.inflate(R.layout.activity_add_food, null);
			builder.setTitle(R.string.add_new_food);
			builder.setView(view);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int id) {
					  EditText name = (EditText) view.findViewById(R.id.name);
					  Context context = getActivity().getApplicationContext();
					  if (name.getText().toString() == null) {
						  	CharSequence text = "Oops! Please enter a food name!";
						  	int duration = Toast.LENGTH_SHORT;
						  	Toast toast = Toast.makeText(context, text, duration);
						  	toast.show();
						  	return;
					  }
					  Ingredient i = new Ingredient(name.getText().toString(), "handfulls");
					  i.setHave(true);
					  i.setNeed(false);
					  i.setWant(false);
					  NavigationDrawer.db.insertIngredient(i);
					  populateList();
				  }
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
			populateList();
			return true;
		}
		case R.id.action_help_pantry:
		{
			AlertDialog.Builder helpbuilder = new AlertDialog.Builder(getActivity());
	    	helpbuilder.setMessage(
	    			"In your pantry, you can keep track of everything you have in your kitchen." +
	    			"\n\nTo add something to your pantry, simply click \"Add\" in the menu on the top-right of your screen." +
	    			"\n\nTo edit or delete an item, press and hold the item you want to change." +
	    			"\n\nEverything that is checked off is something you have in your pantry, ready to be used in your CookEase adventure." +
	    			" For ease of use, items you've previously had are left unchecked at the bottom of your pantry. When you have them in stock again, simply scroll down, check them off, and you're good to go!" +
	    			"\n\nQuestions or Comments? Send us a message at" +
	    			" nephrai1@jhu.edu.");
	    	helpbuilder.show();
		}
		default: return super.onOptionsItemSelected(m);
		}

	}

	public static FoodDatabase getDbAdapter() {
		return NavigationDrawer.db;
	}

	public void populateList() {
		pantryItems.clear();
		pantryItems.addAll(NavigationDrawer.db.getAllIngredients());
	    Collections.sort(pantryItems, comparator);
	    arrayAdapter.notifyDataSetChanged();

	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.pantry_context, menu);
	    menu.setHeaderTitle("Options");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    
	    switch (item.getItemId()) {
	        case R.id.menu_delete:
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				//LayoutInflater inflater = getActivity().getLayoutInflater();
				builder.setTitle(R.string.delete_pantry_food);
				builder.setPositiveButton(R.string.delete_food, new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int id) {
						  Context context = getActivity().getApplicationContext();
						  Ingredient i = arrayAdapter.getItem(info.position);
						  NavigationDrawer.db.deleteIngredient(i);
						  populateList();
						  CharSequence text = i.getName() + " removed from pantry";
						  int duration = Toast.LENGTH_SHORT;
						  Toast toast = Toast.makeText(context, text, duration);
						  toast.show();
					  }
				});
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				populateList();
	            return true;
	        case R.id.menu_edit:
				LayoutInflater inflater = getActivity().getLayoutInflater();
				AlertDialog.Builder editbuilder = new AlertDialog.Builder(getActivity());
				final View view = inflater.inflate(R.layout.activity_edit_food, null);
				editbuilder.setTitle(R.string.edit_food);
				editbuilder.setView(view);
				editbuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int id) {
						  EditText editname = (EditText) view.findViewById(R.id.editfood);
						  Context context = getActivity().getApplicationContext();
						  if (editname.getText().toString() == null) {
							  	CharSequence text = "Oops! Please enter a new food name!";
							  	int duration = Toast.LENGTH_SHORT;
							  	Toast toast = Toast.makeText(context, text, duration);
							  	toast.show();
							  	return;
						  }
						  Ingredient j = arrayAdapter.getItem(info.position);
						  Ingredient i = new Ingredient(editname.getText().toString(), "handfulls");
						  i.setHave(j.getHave());
						  i.setNeed(j.getNeed());
						  i.setWant(j.getWant());
						  NavigationDrawer.db.deleteIngredient(j);
						  NavigationDrawer.db.insertIngredient(i);
						  populateList();
					  }
				});
				editbuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {}
				});
				AlertDialog editdialog = editbuilder.create();
				editdialog.show();
				populateList();
	            return true;
	        case R.id.menu_addtoshopping:
				AlertDialog.Builder shoppingbuilder = new AlertDialog.Builder(getActivity());
				//LayoutInflater inflater = getActivity().getLayoutInflater();
				shoppingbuilder.setTitle(R.string.add_to_shopping);
				shoppingbuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int id) {
						  Context context = getActivity().getApplicationContext();
						  Ingredient i = arrayAdapter.getItem(info.position);
						  i.setNeed(true);
						  NavigationDrawer.db.insertIngredient(i, false);
						  populateList();
						  CharSequence text = i.getName() + " added to shopping list";
						  int duration = Toast.LENGTH_SHORT;
						  Toast toast = Toast.makeText(context, text, duration);
						  toast.show();
					  }
				});
				shoppingbuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {}
				});
				AlertDialog shoppingdialog = shoppingbuilder.create();
				shoppingdialog.show();
				populateList();
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
}






