package com.example.cookease;
public class Ingredient implements Comparable<Ingredient>{
	private String name="";
	private String units="";
	private String quantity="";
	private boolean have=false;
	private boolean need=false;
	private boolean want=false;
	private boolean show=false;
	
	//Actually REALLY damn important that name, units, or quantity never contain an empty "". Liable to break the extraction from the database.
	public Ingredient(String n, String u){
		name = n;
		units = u;
		quantity = "1";
	}
	
	public Ingredient(String n, String u, String q){
		name = n;
		units = u;
		quantity = q;
	}
	
	//TODO: Changed the boolean from 'n' to differentiate from String n
	public Ingredient(String n, String u, String q, boolean h, boolean bn, boolean w) {
		name = n;
		units = u;
		quantity = q;
		have = h;
		need = bn;
		want = w;
	}
	
	public Ingredient(String n, String u, String q, boolean h, boolean bn, boolean w, boolean sh) {
		name = n;
		units = u;
		quantity = q;
		have = h;
		need = bn;
		want = w;
		show = sh;
	}
	
	public String getName(){
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	public String getUnits(){
		return units;
	}
	
	public String getQuantity(){
		return quantity;
	}
	
	public boolean getHave(){
		return have;
	}
	
	public boolean getNeed(){
		return need;
	}
	
	public boolean getWant() {
		return want;
	}
	
	public boolean getShow() {
		return show;
	}
	
	public String toString(){
		String out = quantity + " ";
		String nameAn = "a";
		String unitAn = "a";
		if (name.startsWith("a") || name.startsWith("e") || name.startsWith("i") || name.startsWith("o")) {
			nameAn += "n";
		}
		if (units.startsWith("a") || units.startsWith("e") || units.startsWith("i") || units.startsWith("o")) {
			unitAn += "n";
		}

		if (units.equals("single")) {
			if (quantity.equals("1")) {
				out += name;
				return out;
			}
			if (quantity.contains("/")) {
				if (quantity.indexOf("/") == 1) {
					out += "of " + nameAn + " " + name;
					return out;
				}
				out += name;
				return out;
			} else {
				out += name;
				return out;
			}

		}
		if (quantity.equals("1")) {
			out += units + " of " + name;
			return out;
		}
		if (quantity.contains("/")) {
			if (quantity.indexOf("/") == 1) {
				out += "of " + unitAn + " " + units + " of " + name;
				return out;
			}
			out += units + "s of " + name;
			return out;
		} else {
			out += units + "s of " + name;
			return out;
		}
	}
	
	public void setName(String n){
		name = n;
	}
	
	public void setNeed(boolean t){
		need = t;
	}
	
	public void setHave(boolean t){
		have = t;
	}
	
	public void setWant(boolean t) {
		want = t;
	}
	
	public void setShow(boolean t) {
		show = t;
	}
	
	public void setQuantity(String q){
		quantity = q;
	}
	
	//TODO: Added parenthesis around all the if statements for the code to run.
	public boolean equals(Ingredient other){
		String a = other.getName().toLowerCase();
		String b = this.getName().toLowerCase();
		if (a.equals(b)){
			return true;
		}
		if (a.length() == 0 || b.length() == 0){
			return false;
		}
		if (a.length() < b.length()){ //This ensures that b is always the shorter one.
			b = other.getName().toLowerCase();
			a = this.getName().toLowerCase();
		}
		String temp = b.substring(0, b.length()-1)+"ies"; //y to ies. cherry to cherries.
		if (a.equals(temp)){ 
			return true;
		}
		temp = b.substring(0, b.length()-1)+"ves"; //f to ves. hoof to hooves. Why would we need this?
		if (a.equals(temp)){ 
			return true;
		}
		temp = b+"s";
		if (a.equals(temp)){ 
			return true;
		}
		temp = b+"es";
		if (a.equals(temp)){ 
			return true;
		}
		temp = b+"ies";
		if (a.equals(temp)){ 
			return true;
		}
		return false;
	}
	
	public int compareTo(Ingredient other){
		if (this.equals(other)){
			return 0;
		}else{
			return this.getName().compareTo(other.getName());
		}
	}
}