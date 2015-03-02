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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ShoppingListFragment extends Fragment {

		private ListView list;
		private Button button;
		ArrayList<Ingredient> shoppingListItems;
		private ArrayAdapter<Ingredient> arrayAdapter;
		private Comparator<Ingredient> comparator;
		int showy;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {    
			super.onCreate(savedInstanceState);
		    View view = inflater.inflate(R.layout.shopping_view, container, false);
		    list = (ListView) view.findViewById(R.id.listview_shopping);
		    this.shoppingListItems = new ArrayList<Ingredient>();
		    this.comparator = new foodComparator();
		    Collections.sort(shoppingListItems, comparator);
		    for (Ingredient i : NavigationDrawer.db.getAllIngredients()) {
		    	if (i.getNeed()) {
		    		shoppingListItems.add(i);
		    	}
		    }
		    arrayAdapter = new ShoppingArrayAdapter(view.getContext(), 
		    		R.layout.shopping_list_item, this.shoppingListItems, new foodComparator());
		    list.setAdapter(arrayAdapter);
		    showy = 0;
		    registerForContextMenu(list);
		    
		    setHasOptionsMenu(true);
		    button = (Button) view.findViewById(R.id.buy_button);
		    button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final ArrayList<Ingredient> wants = new ArrayList<Ingredient>();
					for (Ingredient i : shoppingListItems) {
						if (i.getWant()) {
							wants.add(i);
						}
					}
					if (wants.size() == 0) {
						return;
					}
					LayoutInflater inflater = getActivity().getLayoutInflater();
					final View view = inflater.inflate(R.layout.shopping_to_pantry, null);
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage(R.string.sure);
				        builder.setView(view);
				        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
				        	public void onClick(DialogInterface dialog, int id) {
				    			for (Ingredient i : wants) {
				    				i.setHave(true);
				    				i.setWant(false);
				    				i.setNeed(false);
				    				NavigationDrawer.db.insertIngredient(i, false);
				    			}
					        	populateList();
								CharSequence text = "Moved foods to pantry!";
								Context context = getActivity().getApplicationContext();
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
				}
			});
		    if (shoppingListItems.size() == 0 && showy == 0) {
				AlertDialog.Builder newbuilder = new AlertDialog.Builder(getActivity());
		    	newbuilder.setMessage("It looks like your Shopping List is empty! Add some items?");
		    	newbuilder.show();
		    	showy++;
		    }
		    
		    return view;
		    
//		    CheckBox checkbox = R.layout.food_list_item.findViewById(R.id.checkbox);
//		    int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
//		    checkbox.setButtonDrawable(id);
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			// Inflate the menu; this adds items to the action bar if it is present.
			inflater.inflate(R.menu.shopping, menu);
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem m) {
			switch(m.getItemId()) {
			case R.id.action_add_shopping:
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
						  i.setNeed(true);
						  i.setHave(false);
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
			case R.id.action_help_shopping:
			{
				AlertDialog.Builder helpbuilder = new AlertDialog.Builder(getActivity());
		    	helpbuilder.setMessage(
		    			"Welcome to your new shopping list - one that you can take everywhere you go." +
		    			"\n\nTo add an item to your shopping list, simply click \"Add\" in the menu in the top right corner." +
		    			"\n\nTo edit or delete an item, press and hold the item you want to change." +
		    			"\n\nTo add items to your pantry, check them off and press the button at the bottom of the screen when you're ready." +
		    			"\n\nQuestions or comments? Email us at nephrai1@jhu.edu.");
		    	helpbuilder.show();
			}
			default: return super.onOptionsItemSelected(m);
			}
		}
		
		public static FoodDatabase getDbAdapter() {
			return NavigationDrawer.db;
		}
		
		public final void addFood(final Ingredient i) {
			NavigationDrawer.db.insertIngredient(i);
		}
		
		public void populateList() {
			shoppingListItems.clear();
		    for (Ingredient i : NavigationDrawer.db.getAllIngredients()) {
		    	if (i.getNeed()) {
		    		shoppingListItems.add(i);
		    	}
		    }
		    Collections.sort(shoppingListItems, comparator);
		    arrayAdapter.notifyDataSetChanged();

		}
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		    super.onCreateContextMenu(menu, v, menuInfo);
		    MenuInflater inflater = getActivity().getMenuInflater();
		    inflater.inflate(R.menu.shopping_list_context, menu);
		    menu.setHeaderTitle("Options");
		}
		
		@Override
		public boolean onContextItemSelected(MenuItem item) {
		    final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		    
		    switch (item.getItemId()) {
		        case R.id.menu_delete:
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					//LayoutInflater inflater = getActivity().getLayoutInflater();
					builder.setTitle(R.string.delete_shopping_food);
					builder.setPositiveButton(R.string.delete_food, new DialogInterface.OnClickListener() {
						  public void onClick(DialogInterface dialog, int id) {
							  Context context = getActivity().getApplicationContext();
							  Ingredient i = arrayAdapter.getItem(info.position);
							  i.setNeed(false);
							  NavigationDrawer.db.insertIngredient(i, false);
							  populateList();
							  CharSequence text = i.getName() + " removed from shopping list";
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
		        default:
		            return super.onContextItemSelected(item);
		    }
		}
		

}


