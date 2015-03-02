package com.example.cookease;

import java.util.ArrayList;

//TODO Removed some syntax errors.
public class Recipe implements Comparable<Recipe>{
	private String name="";
	private String picture="";
	private String time="";
	private ArrayList<String> tags = new ArrayList<String>();
	private int quantity=0;
	private boolean people=false; //false means single units, like a brownie Recipe says it makes quantity brownies, not serves quantity people. So true means that the Recipe serves quantity people.
	//TODO Changed all 'ingredient' to 'Ingredient' to match class Ingredient
	private ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	private ArrayList<String> instructions = new ArrayList<String>();
	
	public Recipe(String n, String p){
		name = n;
		picture = p;
	}
	
	public Recipe(String n, String p, int q, boolean pe, ArrayList<Ingredient> ig, ArrayList<String> is, ArrayList<String> t, String ti){
		name=n;
		picture=p;
		quantity=q;
		people = pe;
		ingredients = ig;
		instructions = is;
		tags = t;
		time = ti;
	}
	
	public void setTime(String ti){
		time = ti;
	}
	
	public String getTime(){
		return time;
	}

	public void setIngredients(ArrayList<Ingredient> i){
		ingredients=i;
	}
	
	public void setInstructions(ArrayList<String> i){
		instructions=i;
	}
	
	public ArrayList<Ingredient> getIngredients(){
		return ingredients;
	}
	
	public ArrayList<String> getInstructions(){
		return instructions;
	}
	
	public ArrayList<String> getTags(){
		return tags;
	}
	
	public String getName(){
		return name;
	}
	
	public String getPic(){
		return picture;
	}
	
	public void setPic(String p) {
		picture = p;
	}
	
	public String getServings(){
		if (people){
			return "Serves " + Integer.toString(quantity) + " people";
		}else{
			return "Makes " + Integer.toString(quantity);
		}
	}
	
	public int getQuantity(){
		return quantity;
	}
	
	public boolean getPeople(){
		return people;
	}
	
	public void setServings(int q, boolean p){
		people=p;
		quantity=q;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean equals(Recipe other){
		String a = other.getName().toLowerCase();
		String b = this.getName().toLowerCase();
		return a.equals(b);
	}
	
	public int compareTo(Recipe other){
		if (this.equals(other)){
			return 0;
		}else{
			return this.getName().compareTo(other.getName());
		}
	}
}
