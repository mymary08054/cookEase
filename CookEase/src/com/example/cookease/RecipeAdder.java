package com.example.cookease;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//new RecipeAdder.execute(id);
//https://developer.android.com/reference/android/os/AsyncTask.html

public final class RecipeAdder {

	public static Integer AddRecipe(String... recipeID) {
	    URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    
	    //https://stackoverflow.com/questions/238547/how-do-you-programmatically-download-a-webpage-in-java
	    
	    int mode = 1;
	    int ingred = 0;
	    int instruc = 0;
	    int ind=0;
	    int ind2=1;
	    ArrayList<String> instructions = new ArrayList<String>();
	    ArrayList<String> ingredients = new ArrayList<String>();
	    String name = "def", picture="def", time="def";
	    int servings=1;
	    try {
	        url = new URL("http://allrecipes.com/Recipe-Tools/Print/Recipe.aspx?recipeID="+recipeID);
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));

	        while ((line = br.readLine()) != null) {
	        	
	        	if (mode==1) {
	    			ind = line.indexOf("\"customHeadline\"");
	    			if (ind != -1){
	    				ind = line.lastIndexOf("</div>", ind+1);
	    				ind2 = line.lastIndexOf(">", ind-1);
	    				name = line.substring(ind2+1,ind);
	    				mode = 2;
	    			}
	        	}
	        	if (mode==2){
	    			ind = line.indexOf("img src=");
	    			if (ind != -1){
	    				ind = line.indexOf("\"", ind+1);
	    				ind2 = line.indexOf("\"", ind+1);
	    				picture = line.substring(ind+1, ind2); //This is the URL to the picture.
	    				mode = 3;
	    			}
	        	}
	        	if (mode==3){
	        		ind = line.indexOf("Ready In: ");
	    			if (ind != -1){
	    				ind = line.indexOf(">", ind+1);
	    				ind2 = line.indexOf("<", ind+1);
	    				time = line.substring(ind+1, ind2);
	    				mode = 4;
	    			}
	        	}
	        	if (mode==4){
	    			ind = line.indexOf("Servings: ");
	    			if (ind != -1){
	    				ind = line.indexOf(">", ind+1);
	    				ind2 = line.indexOf("<", ind+1);
	    				servings = Integer.parseInt(line.substring(ind+1,ind2));
	    				mode = 5;
	    			}
	        	}
	        	if (mode==5){
	    			if (ingred == 0){
	    				ind = line.indexOf("Ingredients:");
	    			}
	    			if (ingred == 1){
	    				ind = 0;
	    				while (ind != -1){
	    					ind = line.indexOf("</div>", ind+1);
	    					ind2 = line.lastIndexOf(">", ind);
	    					ingredients.add(line.substring(ind2+1, ind));
	    				}
	    			}
	    			if (ind != -1){
	    				ingred = 1;
	    			}
	    			ind = line.indexOf("</table>");
	    			if (ind != -1 && ingred == 1){
	    				mode = 6;
	    				ingred = 0;
	    			}
	        	}
	    		if (mode==6){
	    			if (instruc == 0){
	    				ind = line.indexOf("Directions:");
	    			}
	    			if (instruc == 1){
	    				ind = 0;
	    				while (ind != -1){
	    					ind = line.indexOf("</td>", ind+1);
	    					ind2 = line.lastIndexOf(">", ind);
	    					instructions.add(line.substring(ind2+1, ind).replaceAll("\n",  " ")); //Get rid of the new lines contained in this.
	    				}
	    			}
	    			if (ind != -1){
	    				instruc = 1;
	    			}
	    			ind = line.indexOf("</table>");
	    			if (ind != -1 && instruc == 1){
	    				mode = 7;
	    				instruc = 0;
	    			}
	    		}
	            //System.out.println(line);
	        
	        }
	        ArrayList<String> tags = new ArrayList<String>();
	        tags.add("Imported Recipe");
	        //TODO: Download and save picture. Suggestion: 
	        //TODO: Actually insert this into the database.
	        ArrayList<Ingredient> realingredients = new ArrayList<Ingredient>();
	        String n;
	        String u;
	        String q;
	        for (String s : ingredients){
	        	ind = s.indexOf("/");
	        	if (ind != -1) {
	        		q = s.substring(0, ind+1);
	        	}else{
	        		ind = s.indexOf(" ");
	        		q = s.substring(0, ind);
	        	}
	        	s = s.substring(ind+1, s.length()); //Got the first part, trim it down;
	        	if (s.indexOf("cup") == -1 && s.indexOf("spoon") == -1 ){ //Cups, tablespoon, teaspoon. If we don't find those, it's a single unit, like eggs.
	        		u = "single";
	        		n = s; //No units? Rest of the line is the name.
	        	}else{
	        		u = s.substring(0, s.indexOf(" "));
	        		n = s.substring(s.indexOf(" ")+1, s.length()); //Units? Unit is the first word, everything after is the name
	        	}
	        	realingredients.add(new Ingredient(n,u,q));
	        }
	        
	        //At this stage, picture is the URL to the picture. getFile is supposed to download the picture and return the SDCard location.
	        Recipe newrec = new Recipe(name, getFile(picture), servings, true, realingredients, instructions, tags, time);
	        //That true is because I can't find a good way to tell if the allrecipes state number of servings or number of people. 
	        
	        boolean newthing = true;
	        for (Recipe k : NavigationDrawer.db.getAllRecipes()){
	        	newthing = true;
	        	if (k.equals(newrec)){
	        		newthing = false;
	        	}
	        }
        	if (newthing){
        		NavigationDrawer.db.insertRecipe(newrec);
        		//If the recipe is already saved, chances are we already know all of the ingredients.
		        for (Ingredient i : realingredients){ //For every new ingredient
		        	newthing = true;
		        	for (Ingredient j : NavigationDrawer.db.getAllIngredients()){ //See if it matches an existing ingredient
		        		if (j.equals(i)){
		        			newthing = false; //If it does, we don't want to add it.
		        		}
		        	}
		        	if (newthing){
		        		NavigationDrawer.db.insertIngredient(i);
		        	}
		        }
		        return 1;
	        }
	        
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
	            // nothing to see here
	        }
	    }
	    return 0;
	}
	
	public static String getFile(String url){
		return url; //The location where the image was saved.
		//https://stackoverflow.com/questions/5882005/how-to-download-image-from-any-web-page-in-java
	}
}
