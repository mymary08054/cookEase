package com.example.cookease;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CookbookFragment extends Fragment implements View.OnClickListener{

	//global/static variables
	private static ArrayList<Recipe> testRecipes; //search result recipes that will show in the list view
	private static int rcpPosition; //selected recipe from list display
	private static Recipe displayedRecipe; //recipe that will show in recipe display
	private static int number_of_tags = 9;
	//Values for the search Fragment (Main fragment)
	private String user_rcp_search; //The String that the user puts in the search bar.
	//These are for the tags dialog
	private ArrayList<String> selectedTags; //String of tags selected from tags dialog
	private boolean[] checkedTagIndices; //keep track of which tags user checked in dialog	
	final Context context = getActivity();

	public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		setHasOptionsMenu(true);
		//inflate view with search layout
		final View view = inflater.inflate(R.layout.cookbook_search_layout, container, false);
		//instantiate variables
		selectedTags = new ArrayList<String>();
		checkedTagIndices = new boolean[number_of_tags];

		//Choose Tags button specifics
		Button tagsbutton = (Button) view.findViewById(R.id.choosetags_button);
		tagsbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialog tagsDialog = onCreateTagsDialog(savedInstanceState);
				tagsDialog.show();
			}
		}); 
		//Find Recipes button specifics
		Button rcpbutton = (Button) view.findViewById(R.id.findrecipes_button);
		rcpbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				user_rcp_search = ((EditText)view.findViewById(R.id.user_recipe_search)).getText().toString();
				//selectedTags -->tags used for searching
				testRecipes = FoodDatabaseSearch.Search(NavigationDrawer.db.getAllRecipes(), NavigationDrawer.db.getAllIngredients(), selectedTags, user_rcp_search);
				if (testRecipes.size() == 0) {
					CharSequence text = "Oops! You don't have enough food in your pantry to make anything!";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, duration);
					toast.show();
					return;
				}

				//Change to the ListFragment
				Fragment f;
				f = new RecipeListFragment();
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();         
				fragmentTransaction.replace(R.id.content_frame, f);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		});
		return view; 
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.cookbook, menu);
	}

	/**
	 * Action bar specifics
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem m) {
		switch(m.getItemId()) {
		case R.id.action_add_rcp:
		{
			Fragment f;
			f = new RecipeAddFragment();
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();         
			fragmentTransaction.replace(R.id.content_frame, f);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
			return true;
		}
		case R.id.action_delete_rcp:
		{
			Fragment f;
			f = new RecipeDeleteFragment();
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();         
			fragmentTransaction.replace(R.id.content_frame, f);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
			return true;
		}
		case R.id.action_help_cookbook:
		{
			AlertDialog.Builder helpbuilder = new AlertDialog.Builder(getActivity());
			helpbuilder.setMessage(
					"Welcome to your new electronic cookbook!" +
							"\n\nHere, you can search for recipes by entering keywords, choosing tags, and pressing the \"Find Recipes!\" button." +
							" Recipes that use items you have in your pantry will come up for you to browse." +
							"\n\nTo view all recipes, simply click the all recipes button." +
							"\n\nIn the top right corner, you can add or delete recipes." +
					"\n\nQuestions or comments? Email us at nephrai1@jhu.edu.");
			helpbuilder.show();
		}
		default: return super.onOptionsItemSelected(m);
		}
	}

	/**
	 * dialog pop-up for user to select tags
	 * @param savedInstanceState
	 * @return
	 */
	public Dialog onCreateTagsDialog(Bundle savedInstanceState) {

		final ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();  // Where we track the selected items
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Log.w("NULL?", builder.toString());
		// Set the dialog title
		builder.setTitle(R.string.cookbook_choosetags_button)
		// Specify the list array, the items to be selected by default (null for none),
		// and the listener through which to receive callbacks when items are selected
		.setMultiChoiceItems(R.array.tags_array, checkedTagIndices,
				new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which,
					boolean isChecked) {
				if (isChecked) {
					// If the user checked the item, add it to the selected items
					mSelectedItems.add(which);
				} else if (mSelectedItems.contains(which)) {
					// Else, if the item is already in the array, remove it 
					mSelectedItems.remove(Integer.valueOf(which));
				}
			}
		})
		// Set the action buttons
		.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// User clicked OK, so save the mSelectedItems results somewhere
				// or return them to the component that opened the dialog
				Resources res = getResources();
				String[] tags = res.getStringArray(R.array.tags_array);
				selectedTags.clear();
				for(int i = 0; i < mSelectedItems.size(); i++)
				{
					selectedTags.add(tags[Integer.parseInt(mSelectedItems.get(i).toString())]);
				}
			}
		})
		.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				for(int i = 0; i < checkedTagIndices.length; i++)
				{
					checkedTagIndices[i] = false;
				}
			}
		});
		return builder.create();
	}
	
	public static class RecipeAddByIDFragment extends Fragment {

		public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {    
			super.onCreate(savedInstanceState);
			final View rootView = inflater.inflate(R.layout.add_rcp_by_code_layout, container, false);

			Button cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
			cancelButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Fragment f;
					f = new CookbookFragment();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction().replace(R.id.content_frame, f).commit();
					//fragmentManager.popBackStackImmediate();
				}
			});

			Button doneButton = (Button) rootView.findViewById(R.id.done_button);
			doneButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String rcpID = ((EditText)rootView.findViewById(R.id.input_rcp_id)).getText().toString();
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy);
					RecipeAdder.AddRecipe(rcpID);
					Fragment f;
					f = new CookbookFragment();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction().replace(R.id.content_frame, f).commit();
//					FragmentManager fragmentManager = getFragmentManager();
//					fragmentManager.popBackStackImmediate();
				}
			});

			return rootView;
		}

	}

	public static class RecipeAddFragment extends Fragment {
		private String imgPath;
		private ImageView imgIcon;
		private String rcpTitle;
		private ArrayList<Ingredient> ingList; //input to the actual db
		private String dirInput; //user input
		private ArrayList<String> dirList; //input to db
		//addDialog variables
		private ArrayList<String> selectedTags; //String of tags selected from tags dialog-->put into new recipe
		private boolean[] checkedTagIndices; //keep track of which tags user checked in dialog
		private ListView list;
		
		public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {    
			super.onCreate(savedInstanceState);
			final View rootView = inflater.inflate(R.layout.add_recipe_layout, container, false);
			//initialize values
			imgPath = "";
			rcpTitle = "";
			dirInput = "";
			selectedTags = new ArrayList<String>();
			ingList = new ArrayList<Ingredient>();
			dirList = new ArrayList<String>();
			checkedTagIndices = new boolean [number_of_tags];
			imgIcon = (ImageView) rootView.findViewById(R.id.preview_add_img);
			list = (ListView) rootView.findViewById(R.id.ingredientlistview);
			final ArrayAdapter<Ingredient> arrayAdapter = new ArrayAdapter<Ingredient>(getActivity(), android.R.layout.simple_list_item_1, ingList);
		    list.setAdapter(arrayAdapter);		    
		    setListViewHeightBasedOnChildren(list);
			final Spinner spinner = (Spinner) rootView.findViewById(R.id.unit);
			
			ArrayAdapter<CharSequence> spinAdapt = ArrayAdapter.createFromResource(getActivity(),
			        R.array.units_array, android.R.layout.simple_spinner_item);
			spinAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(spinAdapt);
			
			Button addIngredient = (Button) rootView.findViewById(R.id.addIngredientButton);
			addIngredient.setOnClickListener(new View.OnClickListener() {
				EditText quantity;
				EditText name;
				String unit;
				Ingredient i;

				@Override
				public void onClick(View v) {
					quantity = (EditText) rootView.findViewById(R.id.quantity);
					name = (EditText) rootView.findViewById(R.id.ingredient_name);

					unit = spinner.getSelectedItem().toString();

					i = new Ingredient(name.getText().toString(), unit, quantity.getText().toString());
					ingList.add(i);
				    arrayAdapter.notifyDataSetChanged();
				    setListViewHeightBasedOnChildren(list);
				    quantity.setText("");
				    name.setText("");
				}
			});
			
			// Camera functions
			final ImageView v = (ImageView) rootView.findViewById(R.id.camera_image);
			v.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					switch (arg1.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						//v.setImageBitmap(res.getDrawable(R.drawable.img_down));
						//dispatchTakePictureIntent();
						Dialog picdialog = onCreateChoosePicDialog(savedInstanceState);
						picdialog.show();
						break;
					}
					case MotionEvent.ACTION_CANCEL:{
						//v.setImageBitmap(res.getDrawable(R.drawable.img_up));
						break;
					}
					}
					return true;
				}
			});

			Button tagsbutton = (Button) rootView.findViewById(R.id.makercp_choosetags_button);
			tagsbutton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Dialog tagsDialog = onCreateTagsDialog(savedInstanceState);
					tagsDialog.show();
				}
			});

			Button cancelButton = (Button) rootView.findViewById(R.id.makercp_cancel_button);
			cancelButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentManager fragmentManager = getFragmentManager();
				    fragmentManager.popBackStack();

				}
			});

			Button doneButton = (Button) rootView.findViewById(R.id.makercp_done_button);
			doneButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					rcpTitle = ((EditText)rootView.findViewById(R.id.input_rcp_title)).getText().toString();
					if (ingList.size() == 0) {
						Toast toast = Toast.makeText(getActivity(), "You need at least one ingredient to make a recipe!", Toast.LENGTH_SHORT);
						toast.show();
						return;
					}
					//handling ri
					dirInput = ((EditText) rootView.findViewById(R.id.input_instructions)).getText().toString();
					String[] splitInput2 = dirInput.split("\n");
					for(int i = 0; i < splitInput2.length; i++)
						dirList.add(splitInput2[i]);
					String servingInput = ((EditText) rootView.findViewById(R.id.input_serving_size)).getText().toString();
					if(servingInput.equals(""))
						servingInput = "0";
					String cookTimeInput = ((EditText)rootView.findViewById(R.id.cooking_time_input)).getText().toString();
					//Log.w("Rest: ", "title: " + rcpTitle + " servInput " + servingInput + " cookTime " + cookTimeInput);

					Recipe addrcp = new Recipe(rcpTitle, imgPath, Integer.parseInt(servingInput), false, ingList, dirList, selectedTags, cookTimeInput);
					NavigationDrawer.db.insertRecipe(addrcp);

					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.popBackStack();
				}
			});

			return rootView;
		}
		
		static final int RESULT_LOAD_IMAGE = 0;
		public Dialog onCreateChoosePicDialog(Bundle savedInstanceState) {
		    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle(R.string.choose_pic_dialog_title)
		           .setItems(R.array.pic_choose_array, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		               if(which == REQUEST_TAKE_PHOTO)
		            	   dispatchTakePictureIntent();
		               if(which == RESULT_LOAD_IMAGE)
		               {
		            	   Intent i = new Intent(
		                           Intent.ACTION_PICK,
		                           android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		                           startActivityForResult(i, RESULT_LOAD_IMAGE);
		               }
		               }
		           })
		           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		               }
		    });
		    return builder.create();
		}
		

		/**
		 * Camera functions
		 */
		static final int REQUEST_TAKE_PHOTO = 1;
		private void dispatchTakePictureIntent() {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
				// Create the File where the photo should go
				File photoFile = null;
				try {
					photoFile = createImageFile();
				} catch (IOException ex) {
					// Error occurred while creating the File
				}
				// Continue only if the File was successfully created
				if (photoFile != null) {
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(photoFile));
					startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
				}		    }
		}

		public static void setListViewHeightBasedOnChildren(ListView listView) {
	        ListAdapter listAdapter = listView.getAdapter(); 
	        if (listAdapter == null) {
	            return;
	        }

	        int totalHeight = 0;
	        for (int i = 0; i < listAdapter.getCount(); i++) {
	            View listItem = listAdapter.getView(i, null, listView);
	            listItem.measure(0, 0);
	            totalHeight += listItem.getMeasuredHeight();
	        }

	        ViewGroup.LayoutParams params = listView.getLayoutParams();
	        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	        listView.setLayoutParams(params);
	        listView.requestLayout();
	    }
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			//if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) { //No idea how to get resultCode
			if (requestCode == REQUEST_TAKE_PHOTO && data != null) {
				//Bundle extras = data.getExtras();
				//Bitmap imageBitmap = (Bitmap) extras.get("data"); //returned camera image
				//imgIcon.setImageBitmap(imageBitmap);
				if(!imgPath.equals(""))
					deleteExternalStoragePrivateFile(imgPath);
				if(hasExternalStoragePrivateFile(imgPath))
					Log.w("IMG PATH", "IMG EXISTS");
				imgIcon.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
			}
			if(requestCode == RESULT_LOAD_IMAGE && data != null)
			{
				Uri selectedImage = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	 
	            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
	                    filePathColumn, null, null, null);
	            cursor.moveToFirst();
	 
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String picturePath = cursor.getString(columnIndex);
	            imgPath = picturePath;
	            cursor.close();
	             
	            imgIcon.setImageBitmap(BitmapFactory.decodeFile(imgPath));
			}
		}

		String mCurrentPhotoPath = "";
		//Save photo taken
		private File createImageFile() throws IOException {
			// Create an image file name
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date(0));
			String imageFileName = "JPEG_" + timeStamp + "_";
			File storageDir = Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_PICTURES);
			File image = File.createTempFile(
					imageFileName,  /* prefix */
					".jpg",         /* suffix */
					storageDir      /* directory */
					);

			// Save a file: path for use with ACTION_VIEW intents
			mCurrentPhotoPath = image.getAbsolutePath();
			imgPath = mCurrentPhotoPath;
			//Log.w("RCP SET?", displayedRecipe.getPic());
			return image;
		}

		void deleteExternalStoragePrivateFile(String deleteImgPath) {
			// Get path for the file on external storage.  If external
			// storage is not currently mounted this will fail.
			File file = new File(getActivity().getExternalFilesDir(null), deleteImgPath);
			if (file != null) {
				file.delete();
			}
		}

		boolean hasExternalStoragePrivateFile(String imgPath) {
			// Get path for the file on external storage.  If external
			// storage is not currently mounted this will fail.
			File file = new File(getActivity().getExternalFilesDir(null), imgPath);
			return file.exists();
		}

		public Dialog onCreateTagsDialog(Bundle savedInstanceState) {

			final ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();  // Where we track the selected items
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// Set the dialog title
			builder.setTitle(R.string.cookbook_choosetags_button)
			// Specify the list array, the items to be selected by default (null for none),
			// and the listener through which to receive callbacks when items are selected
			.setMultiChoiceItems(R.array.tags_array, checkedTagIndices,
					new DialogInterface.OnMultiChoiceClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which,
						boolean isChecked) {
					if (isChecked) {
						// If the user checked the item, add it to the selected items
						mSelectedItems.add(which);
					} else if (mSelectedItems.contains(which)) {
						// Else, if the item is already in the array, remove it 
						mSelectedItems.remove(Integer.valueOf(which));
					}
				}
			})
			// Set the action buttons
			.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK, so save the mSelectedItems results somewhere
					// or return them to the component that opened the dialog
					Resources res = getResources();
					String[] tags = res.getStringArray(R.array.tags_array); //TODO CHANGE THIS THINGGG
					selectedTags.clear();
					for(int i = 0; i < mSelectedItems.size(); i++)
					{
						selectedTags.add(tags[Integer.parseInt(mSelectedItems.get(i).toString())]);
					}
				}
			})
			.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					for(int i = 0; i < checkedTagIndices.length; i++)
					{
						checkedTagIndices[i] = false;
					}
				}
			});
			return builder.create();
		}

	}

	public static class RecipeDeleteFragment extends Fragment {
		//addDialog variables
		private ArrayList<String> selectedTags; //String of tags selected from tags dialog-->put into new recipe
		private boolean[] checkedTagIndices; //keep track of which tags user checked in dialog
		private ArrayList<Recipe> displayRCPs; //recipes displayed to select for deletion
		private ArrayList<Recipe> selectedRCPs; //String of tags selected from tags dialog-->TO DELETE
		private boolean[] checkedTagIndices1; //keep track of which tags user checked in dialog
		private String user_del_rcp; //search string

		public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {    
			super.onCreate(savedInstanceState);
			final View rootView = inflater.inflate(R.layout.delete_recipe_layout, container, false);
			//instantiate variables
			selectedTags = new ArrayList<String>();
			checkedTagIndices = new boolean[number_of_tags];
			displayRCPs = new ArrayList<Recipe>();
			selectedRCPs = new ArrayList<Recipe>();

			//Choose Tags button specifics
			Button tagsbutton = (Button) rootView.findViewById(R.id.del_choosetags_button);
			tagsbutton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Dialog tagsDialog = onCreateTagsDialog(savedInstanceState);
					tagsDialog.show();
				}
			});

			Button cancel = (Button) rootView.findViewById(R.id.del_cancel_button);
			cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.popBackStack();
				}
			});

			//Find Recipes button specifics
			Button rcpbutton = (Button) rootView.findViewById(R.id.del_findrecipes_button);
			rcpbutton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
					user_del_rcp = ((EditText)rootView.findViewById(R.id.del_user_recipe_search)).getText().toString();
					//selectedTags
					displayRCPs = FoodDatabaseSearch.SearchAll(NavigationDrawer.db.getAllRecipes(), selectedTags, user_del_rcp);
					if (displayRCPs.size() == 0) {
						Context context = getActivity().getApplicationContext();
						CharSequence text = "Oops! No recipes match your search!";
						int duration = Toast.LENGTH_SHORT;
						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
						return;
					}
					checkedTagIndices1 = new boolean[displayRCPs.size()];
					Dialog tagsDialog = onCreateDeleteRCPsDialog(savedInstanceState);
					tagsDialog.show();
				}
			});

			return rootView;
		}

		public Dialog onCreateDeleteRCPsDialog(Bundle savedInstanceState) {
			final int num_of_rcps = displayRCPs.size();
			final ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();  // Where we track the selected items
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			final CharSequence[] items = new CharSequence[num_of_rcps]; 
			for(int i = 0; i < num_of_rcps; i++)
			{
				items[i] = displayRCPs.get(i).getName();
			}			    
			builder.setTitle(R.string.rm_rcps_choose)
			// Specify the list array, the items to be selected by default (null for none),
			// and the listener through which to receive callbacks when items are selected
			.setMultiChoiceItems(items, checkedTagIndices1,
					new DialogInterface.OnMultiChoiceClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which,
						boolean isChecked) {
					if (isChecked) {
						// If the user checked the item, add it to the selected items
						mSelectedItems.add(which);
					} else if (mSelectedItems.contains(which)) {
						// Else, if the item is already in the array, remove it 
						mSelectedItems.remove(Integer.valueOf(which));
					}
				}
			})
			// Set the action buttons
			.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK, so save the mSelectedItems results somewhere
					// or return them to the component that opened the dialog
					Recipe[] tags = new Recipe[num_of_rcps];
					for(int i=0; i < num_of_rcps; i++)
						tags[i] = displayRCPs.get(i);
					selectedRCPs.clear();
					for(int i = 0; i < mSelectedItems.size(); i++)
					{
						selectedRCPs.add(tags[Integer.parseInt(mSelectedItems.get(i).toString())]);
					}
					for(Recipe killme : selectedRCPs){
						NavigationDrawer.db.deleteRecipe(killme);
					}
					Toast toast = Toast.makeText(getActivity(), "Selected recipes deleted.", Toast.LENGTH_SHORT);
					toast.show();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.popBackStack();
				}
			})
			.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					for(int i = 0; i < checkedTagIndices1.length; i++)
					{
						checkedTagIndices1[i] = false;
					}
				}
			});
			return builder.create();
		}

		public Dialog onCreateTagsDialog(Bundle savedInstanceState) {

			final ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();  // Where we track the selected items
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// Set the dialog title
			builder.setTitle(R.string.cookbook_choosetags_button)
			// Specify the list array, the items to be selected by default (null for none),
			// and the listener through which to receive callbacks when items are selected
			.setMultiChoiceItems(R.array.tags_array, checkedTagIndices,
					new DialogInterface.OnMultiChoiceClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which,
						boolean isChecked) {
					if (isChecked) {
						// If the user checked the item, add it to the selected items
						mSelectedItems.add(which);
					} else if (mSelectedItems.contains(which)) {
						// Else, if the item is already in the array, remove it 
						mSelectedItems.remove(Integer.valueOf(which));
					}
				}
			})
			// Set the action buttons
			.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK, so save the mSelectedItems results somewhere
					// or return them to the component that opened the dialog
					Resources res = getResources();
					String[] tags = res.getStringArray(R.array.tags_array); //TODO CHANGE THIS THINGGG
					selectedTags.clear();
					for(int i = 0; i < mSelectedItems.size(); i++)
					{
						selectedTags.add(tags[Integer.parseInt(mSelectedItems.get(i).toString())]);
					}
				}
			})
			.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					for(int i = 0; i < checkedTagIndices.length; i++)
					{
						checkedTagIndices[i] = false;
					}
				}
			});
			return builder.create();
		}

		//methods to satisfy class
		public void onClick(View arg0) {

		}

		public boolean onCreateOptionsMenu(Menu menu) {
			return true;
		}

	}

	/**
	 * Displaying the list of recipe search results
	 */
	public static class RecipeListFragment extends ListFragment {
		
		int mCurCheckPosition = 0;

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);


			RecipeAdapter adapter = new RecipeAdapter(getActivity(), R.layout.recipe_list_item, testRecipes);
			// Populate list with arrayList of rcps
			setListAdapter(adapter);
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);

			outState.putInt("curChoice", mCurCheckPosition);
		}

		// If the user clicks on an item in the list then the
		// onListItemClick() method is called. It calls a helper function in
		// this case.

		public void onListItemClick(ListView l, View v, int position, long id) {
			showDetails(position);
		}

		// Helper function to show the details of a selected item, either by
		// displaying a fragment in-place in the current UI, or starting a whole
		// new activity in which it is displayed.

		void showDetails(int index) {
			rcpPosition = index;
			Fragment f;
			f = new RecipeDisplayFragment();
			FragmentManager fragmentManager = getFragmentManager();
			//fragmentManager.beginTransaction().replace(R.id.content_frame, f).commit();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();         
			fragmentTransaction.replace(R.id.content_frame, f);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
	}

	public static class ViewAllFragment extends ListFragment {
		int mCurCheckPosition = 0;

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);


			RecipeAdapter adapter = new RecipeAdapter(getActivity(), R.layout.recipe_list_item, NavigationDrawer.db.getAllRecipes());
			// Populate list with arrayList of rcps
			setListAdapter(adapter);
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);

			outState.putInt("curChoice", mCurCheckPosition);
		}

		// If the user clicks on an item in the list then the
		// onListItemClick() method is called. It calls a helper function in
		// this case.

		public void onListItemClick(ListView l, View v, int position, long id) {
			showDetails(position);
		}

		// Helper function to show the details of a selected item, either by
		// displaying a fragment in-place in the current UI, or starting a whole
		// new activity in which it is displayed.

		void showDetails(int index) {
			rcpPosition = index;
			Fragment f;
			f = new RecipeDisplayFragment();
			FragmentManager fragmentManager = getFragmentManager();
			//fragmentManager.beginTransaction().replace(R.id.content_frame, f).commit();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();         
			fragmentTransaction.replace(R.id.content_frame, f);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
	}
	
	/**
	 * Fragment that displays individual recipes
	 *
	 */
	public static class RecipeDisplayFragment extends Fragment {
		private ImageView imgIcon;
		private TextView txtTitle;
		private TextView cookTime;
		private TextView serveSize;
		// Create a new instance of RecipeDisplayFragment, initialized to show the
		// text at 'index'.
		private ListView ingredientsList;
		//These are for the done dialog
		private ArrayList<Ingredient> selectedIngredients; //String of ingredients selected from dialog
		private boolean[] checkedIndices; //keep track of which tags user checked in dialog
		private int num_of_ingredients; // TODO CURRENTLY DUMMY VARIABLE used in done dialog
		private ArrayList<Ingredient> ingredientsRAList;
		private ArrayList<String> ingDisplay;

		public static RecipeDisplayFragment newInstance(int index) {
			RecipeDisplayFragment f = new RecipeDisplayFragment();
			Bundle args = new Bundle();
			args.putInt("index", index);
			f.setArguments(args);
			return f;	
		}

		public int getShownIndex() {
			return getArguments().getInt("index", 0);
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {    
			super.onCreate(savedInstanceState);
			View rootView = inflater.inflate(R.layout.recipe_display_layout, container, false);
			displayedRecipe = testRecipes.get(rcpPosition);
			num_of_ingredients = displayedRecipe.getIngredients().size();
			ingredientsRAList = new ArrayList<Ingredient>();
			ingDisplay = new ArrayList<String>();
			ArrayList<String> directionsRAList = new ArrayList<String>();

			//inserting values
			imgIcon = (ImageView)rootView.findViewById(R.id.rcp_image);
			//TODO Set the Recipe image here!!
			if(!displayedRecipe.getPic().equals(""))
			{
				imgIcon.setImageBitmap(BitmapFactory.decodeFile(displayedRecipe.getPic()));
			} else {
				imgIcon.setImageResource(R.drawable.default_cookease_icon);
			}
			
			txtTitle = (TextView)rootView.findViewById(R.id.rcpTitle_txt);
			txtTitle.setText(displayedRecipe.getName());
			cookTime = (TextView)rootView.findViewById(R.id.recipe_time);
			if(displayedRecipe.getTime() != null)
				cookTime.setText("Cooking Time: " + displayedRecipe.getTime());
			else
				cookTime.setText("Cooking Time: However long you want!");
			
			serveSize = (TextView)rootView.findViewById(R.id.recipe_serve);
			if(displayedRecipe.getServings() != null)
				serveSize.setText(displayedRecipe.getServings());
			//else
				//serveSize.setText("Serves: Uknown");

			for(int i = 0; i < displayedRecipe.getIngredients().size(); i++)
			{
				ingDisplay.add(displayedRecipe.getIngredients().get(i).toString());
				ingredientsRAList.add(displayedRecipe.getIngredients().get(i));
			}
			for(int i = 0; i < displayedRecipe.getInstructions().size(); i++)
			{
				directionsRAList.add((i+1) + ". " + displayedRecipe.getInstructions().get(i));
			}

			//Code to display the custom listview.
			DisplayAdapter ingredientsAdapter = new DisplayAdapter(getActivity(), R.layout.simple_list_item, ingDisplay);
			ingredientsList = (ListView) rootView.findViewById(R.id.rcpIngredients_list);       
			ingredientsList.setAdapter(ingredientsAdapter);
			DisplayAdapter directionsAdapter = new DisplayAdapter(getActivity(), R.layout.simple_list_item, directionsRAList);
			ingredientsList = (ListView) rootView.findViewById(R.id.rcpInstructions_list);       
			ingredientsList.setAdapter(directionsAdapter);

			//Done button details
			Button tagsbutton = (Button) rootView.findViewById(R.id.done_button);
			//instantiate variables
			selectedIngredients = new ArrayList<Ingredient>();
			checkedIndices = new boolean[displayedRecipe.getIngredients().size()+2];
			tagsbutton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Dialog doneDialog = onCreateDoneDialog(savedInstanceState);
					doneDialog.show();
					Button neutralButton = ((AlertDialog) doneDialog).getButton(DialogInterface.BUTTON_NEUTRAL);
					neutralButton.setOnClickListener(new View.OnClickListener() {
					    @Override
					    public void onClick(View onClick) {                 

					    }
					});
				}
			});
			return rootView;
		}

		/**
		 * dialog pop-up for user to select tags
		 * @param savedInstanceState
		 * @return
		 */
		public Dialog onCreateDoneDialog(final Bundle savedInstanceState) {
			final ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();  // Where we track the selected items
			//items that show up as the ingredients for checking off
			final CharSequence[] items = new CharSequence[num_of_ingredients + 2]; 
			for(int i = 0; i < num_of_ingredients; i++)
			{
				items[i] = (CharSequence) displayedRecipe.getIngredients().get(i).getName();
			}
			//adding the 'remove from pantry' and 'add to shopping list' checkboxes
			items[num_of_ingredients] = "Add to Shopping List";
			items[num_of_ingredients + 1] = "Remove from Pantry";

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// Set the dialog title
			builder.setTitle(R.string.cookbook_done_button)
			// Specify the list array, the items to be selected by default (null for none),
			// and the listener through which to receive callbacks when items are selected
			.setMultiChoiceItems(items, checkedIndices,
					new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which,
						boolean isChecked) {
					if (isChecked) {
						// If the user checked the item, add it to the selected items
						//ingredientsToAdd.add(recipe.getingredient(which))
						mSelectedItems.add(which);
					} else if (mSelectedItems.contains(which)) {
						// Else, if the item is already in the array, remove it 
						mSelectedItems.remove(Integer.valueOf(which));
					}
				}
			})
			.setNeutralButton(R.string.apply_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK, so save the mSelectedItems results somewhere
					// or return them to the component that opened the dialog
					selectedIngredients.clear();
					int selectedIndex;
					boolean shoppingList = false, rmPantry = false;

					for(int i = 0; i < mSelectedItems.size(); i++)
					{
						selectedIndex = Integer.parseInt(mSelectedItems.get(i).toString());
						if(selectedIndex < num_of_ingredients)
							selectedIngredients.add(ingredientsRAList.get(selectedIndex));
						else if (selectedIndex == num_of_ingredients)
							shoppingList = true;
						else
							rmPantry = true;
					}

					if(shoppingList == true)
					{
						for (Ingredient i : selectedIngredients) {
							i.setNeed(true);
							NavigationDrawer.db.insertIngredient(i, false);
						}
					}
					if(rmPantry == true)
					{
						for (Ingredient i : selectedIngredients) {
							i.setHave(false);
							NavigationDrawer.db.insertIngredient(i, false);
						}
					}
				}
			})
			// Set the action buttons
			//Remove from pantry button
			.setPositiveButton(R.string.done_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK, so save the mSelectedItems results somewhere
					// or return them to the component that opened the dialog
					selectedIngredients.clear();
					int selectedIndex;
					boolean shoppingList = false, rmPantry = false;

					for(int i = 0; i < mSelectedItems.size(); i++)
					{
						selectedIndex = Integer.parseInt(mSelectedItems.get(i).toString());
						if(selectedIndex < num_of_ingredients)
							selectedIngredients.add(ingredientsRAList.get(selectedIndex));
						else if (selectedIndex == num_of_ingredients)
							shoppingList = true;
						else
							rmPantry = true;
					}

					if(shoppingList == true)
					{
						for (Ingredient i : selectedIngredients) {
							i.setNeed(true);
							NavigationDrawer.db.insertIngredient(i, false);
						}
					}
					if(rmPantry == true)
					{
						for (Ingredient i : selectedIngredients) {
							i.setHave(false);
							NavigationDrawer.db.insertIngredient(i, false);
						}
					}

					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.popBackStack();
				}
			})
			//Cancel Button
			.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					for(int i = 0; i < num_of_ingredients; i++)
					{
						checkedIndices[i] = false;
					}
				}
			});
			return builder.create();
		}


		public void onClick(View arg0) {
			//switch(view.getId()){
			//case R.id.findrecipes_button:

			//break;
		}

		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			//getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}
}

