package com.example.cookease;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public final class FoodDatabase {

	private SQLiteDatabase db;
	private FoodDatabaseHandler dbHandler;
	private final Context context;

	private boolean syncedr = false;
	private boolean syncedi = false;
	private ArrayList<Ingredient> lastingreds;
	private ArrayList<Recipe> lastrecips;
	
	/** The database version. */
	private static final int DB_VERSION = 7;
	/** The database name. */
	private static final String DB_NAME = "cookeasefood";
	
	private static final String DB_SPLITTER = "  "; //Just a straight doublespace. Changing this necessitates a DB wipe or some clever jiggerypokey.
	
	/** Table names. */
	private static final String INGRED_TABLE = "Ingredients";
	private static final String RECIPE_TABLE = "Recipes";

	// Table Columns names
	private static final String INGRED_ID = "id"; //Double duty for these four.
	private static final String INGRED_NAME = "name";
	private static final String INGRED_QUANTITY = "quant"; //Also this is a text for Ingredients and an int for Recipes.
	private static final String INGRED_UNITS = "units"; //Used as either string units for Ingredients or the people boolean (int in this) variable for Recipes.
	
	private static final String INGRED_HAVE = "have";
	private static final String INGRED_NEED = "need";
	private static final String INGRED_WANT = "want";
	private static final String INGRED_SHOW = "show";
	
	private static final String RECIPE_PIC = "pic";
	private static final String RECIPE_TAGS = "tags";
	private static final String RECIPE_TIME = "time";
	private static final String RECIPE_INSTRUC = "instruc";
	private static final String RECIPE_INGNAME = "ingname";
	private static final String RECIPE_INGUNIT = "ingunit";
	private static final String RECIPE_INGQUAN = "ingquan";
	
	//Theory: Serialize everything together. Use a unique identifier that we know does not exist in the strings to indicate boundaries.
	
	/** The list columns. */
	public static final String[] INGRED_COLS = { INGRED_ID, INGRED_NAME, INGRED_UNITS, INGRED_QUANTITY, INGRED_HAVE, INGRED_NEED, INGRED_WANT, INGRED_SHOW };
	public static final String[] RECIPE_COLS = { INGRED_ID, INGRED_NAME, RECIPE_PIC, INGRED_QUANTITY, RECIPE_TAGS, INGRED_UNITS, RECIPE_TIME, RECIPE_INSTRUC, RECIPE_INGNAME, RECIPE_INGUNIT, RECIPE_INGQUAN };

	//Pass it the app context.
	public FoodDatabase(final Context ctx) {
		context = ctx;
		dbHandler = new FoodDatabaseHandler(context, DB_NAME, null,
				DB_VERSION);
	}

	//Guess what these next three do.
	public void open(){
		try{
			db = dbHandler.getWritableDatabase();
		}catch (SQLiteException ex){
			db = dbHandler.getReadableDatabase();
		}
	}
	public void close(){
		db.close();
	}
	public void clearDatabase() {
		context.deleteDatabase(DB_NAME);
	}
	
	//Deletes by name. No pluralization checking, please only pass Ingredients that were pulled from the getIngredients arraylist.
	//Honestly, updates should be done by getting the Recipe/Ingredient object, nuking it, editing it, then re-inserting it.
	//There must be a better method, but I fear it would make a desync between the database and the arraylist of objects and the sync would be a performance drain just the same.
	public void deleteIngredient(final Ingredient i){
		syncedi = false;
		db.delete(INGRED_TABLE, "UPPER(" + INGRED_NAME + ")" + "=UPPER('" + i.getName() + "')", null);
	}
	public void deleteRecipe(final Recipe r){
		syncedr = false;
		db.delete(RECIPE_TABLE, "UPPER(" + INGRED_NAME + ")" + "=UPPER('" + r.getName() + "')", null);
	}
	
	public long insertIngredient(final Ingredient i){
		return insertIngredient(i, true);
	}
	public long insertRecipe(final Recipe r){
		return insertRecipe(r, true);
	}
	
	public long insertIngredient(final Ingredient i, boolean add) {
		ContentValues cvalues = new ContentValues();
		// assign values for each col
		cvalues.put(INGRED_NAME, i.getName());
		cvalues.put(INGRED_UNITS, i.getUnits());
		cvalues.put(INGRED_QUANTITY, i.getQuantity());
		if (i.getHave()) {
			cvalues.put(INGRED_HAVE, 1);
		}else {
			cvalues.put(INGRED_HAVE, 0);
		}
		if (i.getNeed()) {
			cvalues.put(INGRED_NEED, 1);
		}else {
			cvalues.put(INGRED_NEED, 0);
		}
		if (i.getWant()) {
			cvalues.put(INGRED_WANT, 1);
		}else {
			cvalues.put(INGRED_WANT, 0);
		}
		if (i.getShow()) {
			cvalues.put(INGRED_SHOW, 1);
		}else {
			cvalues.put(INGRED_SHOW, 0);
		}
		// add to course table in database
		if (!syncedi){
			lastingreds = getAllIngredients();
		}
		for (Ingredient in : lastingreds){
			if (in.equals(i)){ //Ignoring plurals they're the same.
				if (!add){ //If this is being updated, it might be intended to have Have false.
					i.setHave(true);
				}
				add = false;
				if (!in.getName().equals(i.getName())){ //If they're actually different, we need to look at the database based on the known one.
					i.setName(in.getName());
				}
			}
		}
		syncedi = false;
		if (add) {
			return db.insert(INGRED_TABLE, null, cvalues);
		}else {
			return (long)db.update(INGRED_TABLE, cvalues, "UPPER(" + INGRED_NAME + ")" + "=UPPER('" + i.getName() + "')", null);
		}
	}
	
	public long insertRecipe(final Recipe r, boolean add) {
		ContentValues cvalues = new ContentValues();
		// assign values for each col
		cvalues.put(INGRED_NAME, r.getName());
		cvalues.put(RECIPE_PIC, r.getPic());
		cvalues.put(INGRED_QUANTITY, r.getQuantity());
		cvalues.put(RECIPE_TIME, r.getTime());
		if (r.getPeople()) {
			cvalues.put(INGRED_UNITS, 1);
		}else{
			cvalues.put(INGRED_UNITS, 0);
		}
		ArrayList<String> instructs = r.getInstructions();
		String instructions = "";
		String ingtemp;
		for (int i=0; i<instructs.size(); i++){ //We need to get rid of any doublespaces that already exist in the string or we'll trash the database/instructionlist.
			ingtemp = instructs.get(i);
			while (ingtemp.indexOf("  ") != -1){
				ingtemp = ingtemp.replaceAll(DB_SPLITTER, " ");
			}
			instructions = instructions + ingtemp + DB_SPLITTER;
		}
		cvalues.put(RECIPE_INSTRUC, instructions);
		
		instructions = "";
		instructs = r.getTags(); //Just recycling code for tags.
		for (int i=0; i<instructs.size(); i++){
			ingtemp = instructs.get(i);
			while (ingtemp.indexOf("  ") != -1){
				ingtemp = ingtemp.replaceAll(DB_SPLITTER, " ");
			}
			instructions = instructions + ingtemp + DB_SPLITTER;
		}
		cvalues.put(RECIPE_TAGS, instructions);
		
		ArrayList<Ingredient> ingredients = r.getIngredients();
		String ingname = "";
		String ingunit = "";
		String ingquan = "";
		//The magic of string.split is that it'll split it by the "  "'s and then abandons trailing null spaces (""'s). So we don't need to worry about the trailing "  "'s.
		for (Ingredient ingreds : ingredients){
			ingtemp = ingreds.getName();
			while (ingtemp.indexOf("  ") != -1){
				ingtemp = ingtemp.replaceAll(DB_SPLITTER, " ");
			}
			ingname = ingname + ingtemp + DB_SPLITTER;
			
			ingtemp = ingreds.getUnits();
			while (ingtemp.indexOf("  ") != -1){
				ingtemp = ingtemp.replaceAll(DB_SPLITTER, " ");
			}
			ingunit = ingunit + ingtemp + DB_SPLITTER;
			
			ingtemp = ingreds.getQuantity();
			while (ingtemp.indexOf("  ") != -1){
				ingtemp = ingtemp.replaceAll(DB_SPLITTER, " ");
			}
			ingquan = ingquan + ingtemp + DB_SPLITTER;
		}
		cvalues.put(RECIPE_INGNAME, ingname);
		cvalues.put(RECIPE_INGUNIT, ingunit);
		cvalues.put(RECIPE_INGQUAN, ingquan);
		// add to course table in database

		if (!syncedr){
			lastrecips = getAllRecipes();
		}
		if (!syncedi){
			lastingreds = getAllIngredients();
		}
		boolean known = false;
		for (Recipe ro : lastrecips){
			if (ro.equals(r)){
				return -1;
			}
		}
		for (Ingredient in : r.getIngredients()){
			known = false;
			for (Ingredient out : lastingreds){
				if (out.equals(in)){
					known = true;
				}
			}
			if (!known){
				insertIngredient(new Ingredient(in.getName(), in.getUnits(), in.getQuantity()));
			}
		}
		syncedr = false;
		
		if (add){
			return db.insert(RECIPE_TABLE, null, cvalues);
		}else{
			return (long)db.update(RECIPE_TABLE, cvalues, "UPPER(" + INGRED_NAME + ")" + "=UPPER('" + r.getName() + "')", null);
		}
	}

	public ArrayList<Ingredient> getAllIngredients() {
		ArrayList<Ingredient> inglist = new ArrayList<Ingredient>();

		Cursor cursor = db.query(INGRED_TABLE, INGRED_COLS, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			//Basic theory: I'm not confident enough to change the SQL database to use booleans so I'm saving them as ints.
			Ingredient newingred = new Ingredient(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4) == 1, cursor.getInt(5) == 1, cursor.getInt(6) == 1);
			inglist.add(newingred);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		
		lastingreds = inglist;
		syncedi = true;
		return inglist;
	}
	
	public ArrayList<Recipe> getAllRecipes() {
		ArrayList<Recipe> reclist = new ArrayList<Recipe>();
		ArrayList<String> stemp;
		ArrayList<String> ttemp;
		ArrayList<String> stemp1;
		ArrayList<String> stemp2;
		ArrayList<String> stemp3;
		ArrayList<Ingredient> itemp;
		
		Cursor cursor = db.query(RECIPE_TABLE, RECIPE_COLS, null, null, null, null, null);
		cursor.moveToFirst();
		int i;
		while (!cursor.isAfterLast()) {
			//Basic theory: Can't directly save Ingredients, so their critical data is extracted, made serial to fit into a fixed number of sql columns, and then reassembled here.
			stemp1 = new ArrayList<String>(Arrays.asList(cursor.getString(8).split(DB_SPLITTER)));
			stemp2 = new ArrayList<String>(Arrays.asList(cursor.getString(9).split(DB_SPLITTER)));
			stemp3 = new ArrayList<String>(Arrays.asList(cursor.getString(10).split(DB_SPLITTER)));
			ttemp = new ArrayList<String>(Arrays.asList(cursor.getString(4).split(DB_SPLITTER)));
			itemp = new ArrayList<Ingredient>(); //I forgot the parenthesis for this one.
			for (i=0; i<stemp1.size(); i++){
				itemp.add(new Ingredient(stemp1.get(i), stemp2.get(i), stemp3.get(i)));
			}
			stemp = new ArrayList<String>(Arrays.asList(cursor.getString(7).split(DB_SPLITTER)));
			Recipe newingred = new Recipe(cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(5) == 1, itemp, stemp, ttemp, cursor.getString(6));
			reclist.add(newingred);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		lastrecips = reclist;
		syncedr = true;
		return reclist;
	}
	
	private static class FoodDatabaseHandler extends SQLiteOpenHelper {

		//These statements are long because I hate fun
		private static final String DB_CREATE1 = "CREATE TABLE " + INGRED_TABLE + "(" + INGRED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + INGRED_NAME + " TEXT," + INGRED_UNITS + " TEXT," + INGRED_QUANTITY + " TEXT," + INGRED_HAVE + " INTEGER," + INGRED_NEED + " INTEGER," + INGRED_WANT + " INTEGER," + INGRED_SHOW + " INTEGER" + ")";
		private static final String DB_CREATE2 = "CREATE TABLE " + RECIPE_TABLE + "(" + INGRED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + INGRED_NAME + " TEXT," + RECIPE_PIC + " TEXT," + INGRED_QUANTITY + " INTEGER," + RECIPE_TAGS + " TEXT," + INGRED_UNITS + " INTEGER," + RECIPE_TIME + " TEXT," + RECIPE_INSTRUC + " TEXT," + RECIPE_INGNAME + " TEXT," + RECIPE_INGUNIT + " TEXT," + RECIPE_INGQUAN + " TEXT" + ")";

		/**
		 * Creates a new database handler.
		 * 
		 * @param context
		 *            the app context
		 * @param name
		 *            the name of the database
		 * @param fct
		 *            the cursor dactory
		 * @param version
		 *            the version of the database
		 */
		public FoodDatabaseHandler(final Context context, final String name, final CursorFactory fct, final int version) {
			super(context, name, fct, version);
		}

		@Override
		public void onCreate(final SQLiteDatabase adb) {
			adb.execSQL(DB_CREATE1);
			adb.execSQL(DB_CREATE2);
		}

		// Upgrading database
		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
				final int newVersion) {
			// Drop older table if existed
			db.execSQL("DROP TABLE IF EXISTS " + INGRED_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + RECIPE_TABLE);
			// Create tables again
			onCreate(db);
		}
	}
}