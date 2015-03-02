package com.example.cookease;


import java.util.ArrayList;
import java.util.Locale;

import android.util.Log;

public class FoodDatabaseSearch {
	public static ArrayList<Recipe> Search(ArrayList<Recipe> r, ArrayList<Ingredient> p, ArrayList<String> t, String name){
		ArrayList<Recipe> recipes = new ArrayList<Recipe>(r);
		for (Recipe or : r){
			Log.w("FOODDB", "" + or);
			if ((!RecipeCheck(or.getIngredients(), p) || !TagCheck(or.getTags(), t)) || or.getName().toLowerCase(Locale.getDefault()).indexOf(name.toLowerCase()) == -1){ 
				//Must contain ALL ingredients and at least one tag.
				recipes.remove(or);
			}
		}
		return recipes;
	}
	
	public static ArrayList<Recipe> SearchAll(ArrayList<Recipe> r, ArrayList<String> t, String name){
		ArrayList<Recipe> recipes = new ArrayList<Recipe>(r);
		for (Recipe or : r){
			Log.w("FOODDB", "" + or);
			if (!TagCheck(or.getTags(), t) || or.getName().indexOf(name) == -1){ 
				recipes.remove(or);
			}
		}
		return recipes;
	}
	
	private static boolean RecipeCheck(ArrayList<Ingredient> r, ArrayList<Ingredient> p){
		int howmany = 0;
		int size = r.size();
		for (Ingredient or : r) {
			for (Ingredient op : p) {
				if (op.equals(or)) {
					if (op.getHave()){
						howmany++;
					} else {
						return false;
					}
				}
			}

		}
		if (howmany == size) {
			return true;
		}
		return false;
	}
	
	private static boolean TagCheck(ArrayList<String> r, ArrayList<String> t) {
		if (r.size() == 0 || (t.size() == 0 && r.size() == 0)){
			return true;
		}
//		for (String or : r) {
//			for (String op : t) {
//				if (op.equals(or)) {
//					return true;
//				}
//			}
//		}
//		return false;
		
		int howmany = 0; //number of tags that match
		int searchSize = t.size(); //number of tags that you selected 
		for (String or : r) { //for all tags in recipe
			for (String op : t) { //for all tags that you searched
				if (op.equals(or)) { //if they're equal
					howmany++; 
				}
			}
		}
		if (howmany == searchSize) {
			return true;
		}
		return false;
	}
}
