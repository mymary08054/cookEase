package com.example.cookease;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NavigationDrawer extends Activity {
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    protected static FoodDatabase db;
    
    private CharSequence drawerTitle;
    private CharSequence title;
    private String[] tabTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        title = drawerTitle = getTitle();
        tabTitles = getResources().getStringArray(R.array.tab_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, tabTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, 
         		R.string.drawer_open, R.string.drawer_close) {

        	public void onDrawerClosed(View view) {
                 getActionBar().setTitle(title);
                 invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
             }
 
             public void onDrawerOpened(View drawerView) {
                 getActionBar().setTitle(drawerTitle);
                 invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
             }
         };
        drawerLayout.setDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
        db = new FoodDatabase(getApplicationContext());
        db.open();
        if (db.getAllRecipes().size() == 0){
	        ArrayList<Ingredient> temp = new ArrayList<Ingredient>();
			ArrayList<String> ins = new ArrayList<String>();
			ArrayList<String> tags = new ArrayList<String>();
			temp.add(new Ingredient("egg", "single", "3"));
			temp.add(new Ingredient("cheese", "cup", "1/4"));
			ins.add("Whisk eggs, pour into frying pan.");
			ins.add("Let cook until bottom begins to firm.");
			ins.add("Add filling ingredients prepared as desired.");
			ins.add("Fold omelette over and continue cooking until golden brown, flipping occasionally.");
			tags.add("Breakfast");
			db.insertRecipe(new Recipe("Lazy Omelette", "", 1, true, temp, ins, new ArrayList<String>(), "15 Minutes"));
			temp.add(new Ingredient("bell pepper", "single", "1/4"));
			db.insertRecipe(new Recipe("Less Lazy Omelette", "", 1, true, temp, ins, new ArrayList<String>(), "20 Minutes"));
			temp.add(new Ingredient("Italian sausage link", "single", "1"));
			db.insertRecipe(new Recipe("Not Lazy Omelette", "", 1, true, temp, ins, new ArrayList<String>(), "25 Minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Flour", "cup", "2"));
			temp.add(new Ingredient("Sugar", "cup", "3/4"));
			temp.add(new Ingredient("Baking soda", "teaspoon", "1"));
			temp.add(new Ingredient("Salt", "teaspoon", "1"));
			temp.add(new Ingredient("Butter", "cup", "1/2"));
			temp.add(new Ingredient("Vanilla extract", "teaspoon", "1"));
			temp.add(new Ingredient("Eggs", "single", "2"));
			temp.add(new Ingredient("Chocolate Chips", "cup", "2"));
			ins.clear();
			ins.add("Sift and mix the flour, sugar, baking soda, and salt in a large bowl.");
			ins.add("Add the melted butter, vanilla extract, and eggs.");
			ins.add("Add the chocolate chips.");
			ins.add("Bake at 350 degrees for 10-12 minutes");
			tags.add("Vegetarian");
			tags.add("Kosher");
			tags.add("Dessert");
			db.insertRecipe(new Recipe("Chocolate Chip Cookies", "", 12, true, temp, ins, tags, "12 Minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Eggs", "single", "1"));
			temp.add(new Ingredient("Flour", "cup", "1"));
			temp.add(new Ingredient("Salt", "teaspoon", "1/2"));
			ins.clear();
			ins.add("In a medium sized bowl, combine flour and salt. Make a well in the flour, add the slightly beaten egg, and mix. Mixture should form a stiff dough. If needed, stir in 1 to 2 tablespoons water.");
			ins.add("On a lightly floured surface, knead dough for about 3 to 4 minutes. With a pasta machine or by hand roll dough out to desired thinness. Use machine or knife to cut into strips of desired width.");
			ins.add("Cook in boiling water for 2-3 minutes or until cooked");
			ins.add("Eat immediately while hot");
			tags.clear();
			tags.add("Vegetarian");
			tags.add("Kosher");
			tags.add("Dinner");
			db.insertRecipe(new Recipe("Homemade pasta Pasta", "", 3, true, temp, ins, tags, "20 Minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Cucumber", "single", "1"));
			temp.add(new Ingredient("Raisins", "cup", "1/4"));
			temp.add(new Ingredient("Carrot", "single", "1"));
			temp.add(new Ingredient("Chives", "single", "24"));
			temp.add(new Ingredient("Cream cheese", "ounce", "4"));
			ins.clear();
			tags.clear();
			tags.add("Vegetarian");
			tags.add("Dinner");
			tags.add("Lunch");
			ins.add("With a peeler, slice cucumber into 8 1/8-inch thick slices lengthwise. Cut each slice into 3 pieces crosswise.");
			ins.add("Place about 1 teaspoon shredded carrot onto the bottom edge of a cucumber slice; place about 1 teaspoon cream cheese onto the carrot, and press 2 or 3 raisins into the cream cheese. Roll the cucumber slice into a little sushi roll, starting at the filled end. Tie roll with a chive to secure. Repeat with remaining ingredients.");
			db.insertRecipe(new Recipe("Mini Cucumber Sushi Rolls", "", 3, true, temp, ins, tags, "30 Minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Lemons", "single", "2"));
			temp.add(new Ingredient("Milk", "cup", "2"));
			temp.add(new Ingredient("Condensed milk", "ounce", "14"));
			ins.clear();
			ins.add("In the freezer canister of an ice cream maker, combine lemon zest, lemon juice, milk and sweetened condensed milk. Freeze according to manufacturers' directions.");
			tags.clear();
			tags.add("Dessert");
			db.insertRecipe(new Recipe("Lemon Ice II", "", 1, true, temp, ins, tags, "2 hours 15 minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Peanut butter", "tablespoons", "2"));
			temp.add(new Ingredient("Bread", "slices", "2"));
			temp.add(new Ingredient("Cherries", "single", "2"));
			temp.add(new Ingredient("Raisins", "single", "4"));
			temp.add(new Ingredient("Pretzel sticks", "single", "4"));
			ins.clear();
			tags.clear();
			tags.add("Kosher");
			tags.add("Vegetarian");
			tags.add("Dairy-Free");
			tags.add("Dinner");
			tags.add("Lunch");
			tags.add("Vegan");
			ins.add("Spread peanut butter onto 1 bread slice; top with second bread slice to make a sandwich. Cut sandwich in half diagonally, creating 2 triangles.");
			ins.add("Arrange triangles on a plate. Use 1 cherry on each triangle as the 'nose'. Press 2 raisins on each triangle as 'eyes'. Slice 2 pretzel sticks into the top of each triangle for 'antlers'.");
			db.insertRecipe(new Recipe("Reindeer Sandwiches", "", 1, true, temp, ins, tags, "10 Minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Eggs", "single", "3"));
			temp.add(new Ingredient("Onions", "single", "2"));
			temp.add(new Ingredient("Tomato", "single", "2"));
			temp.add(new Ingredient("Salt", "pinch", "2"));
			temp.add(new Ingredient("Sugar", "pinch", "2"));
			temp.add(new Ingredient("Olive oil", "tablespoon", "3"));
			ins.clear();
			ins.add("Begin scrambling the eggs.");
			ins.add("Fry tomatoes and add onions, salt, and sugar.");
			ins.add("Add eggs to pan. Fry until the tomato juices seep into the egg.");
			tags.clear();
			tags.add("Lunch");
			tags.add("Dinner");
			db.insertRecipe(new Recipe("Stir-Fry", "", 4, true, temp, ins, tags, "20 Minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Blueberries", "ounce", "10"));
			temp.add(new Ingredient("Flour", "cup", "2"));
			temp.add(new Ingredient("Sugar", "cup", "1/4"));
			temp.add(new Ingredient("Baking powder", "teaspoon", "2 1/4"));
			temp.add(new Ingredient("Salt", "teaspoon", "1/2"));
			temp.add(new Ingredient("Eggs", "single", "1"));
			temp.add(new Ingredient("Milk", "cup", "2"));
			temp.add(new Ingredient("Butter", "cup", "1/4"));
			temp.add(new Ingredient("Maple Syrup", "dollop", "1"));
			ins.clear();
			ins.add("Sift flour, sugar, baking powder, and salt into a large bowl.");
			ins.add("Add the melted butter, eggs, and milk. Stir until uniform.");
			ins.add("Mix in the blueberries");
			ins.add("Spoon 1/3 of a cup of batter into a skillet for each pancake over a medium-sized flame.");
			ins.add("Cook 2-3 minutes per side.");
			ins.add("Top with maple syrup and enjoy.");
			tags.clear();
			tags.add("Breakfast");
			tags.add("Kosher");
			tags.add("Vegetarian");
			db.insertRecipe(new Recipe("Blueberry pancakes", "", 10, true, temp, ins, tags, "10 Minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Onion", "cup", "1"));
			temp.add(new Ingredient("Ground bison", "pound", "1"));
			temp.add(new Ingredient("Buns", "single", "4"));
			temp.add(new Ingredient("Salt", "teaspoon", "1/2"));
			temp.add(new Ingredient("Hamburger Bun", "single", "4"));
			temp.add(new Ingredient("Cheese", "ounces", "6"));
			temp.add(new Ingredient("Lettuce", "single", "1"));
			ins.clear();
			ins.add("Cook chopped onions over high heat until caramelized.");
			ins.add("Preheat frying pan and add salt.");
			ins.add("Chop lettuce and cheese while preheating.");
			ins.add("Grill 1/4 pound patties over medium-high heat for about 4 minutes each side.");
			ins.add("Assemble burger.");
			tags.clear();
			tags.add("Lunch");
			tags.add("Dinner");
			db.insertRecipe(new Recipe("Bison Burgers", "", 4, true, temp, ins, tags, "20 Minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Tomato", "pound", "2 1/4"));
			temp.add(new Ingredient("Cucumber", "single", "1"));
			temp.add(new Ingredient("Bell Pepper", "single", "1"));
			temp.add(new Ingredient("Sherry Vinegar", "cup", "1/4"));
			temp.add(new Ingredient("Garlic", "single", "1"));
			temp.add(new Ingredient("Olive oil", "cup", "1/2"));
			temp.add(new Ingredient("Egg", "single", "1"));
			tags.clear();
			tags.add("Dinner");
			tags.add("Kosher");
			tags.add("Vegetarian");
			ins.clear();
			ins.add("Take two cloves of garlic, peel and crush. Hardboil the egg, separate the white and yolk, finely chop it. Peel and chop cucumber and bell pepper.");
			ins.add("Place  the  tomatoes  in  a  heatproof  bowl,  add  enough  boiling  water  to  cover,  and  let  stand  for  20  seconds  or  until  the  skins  split.  Drain  and  rinse  under  cold  running  water  to  cool.  Gently  peel  off  the  skins.  Cut  the  tomatoes  in  half,  and  use  a  teaspoon  to  remove  the  cores  and  seeds.  Chop  the  flesh.");
			ins.add("Puree  the  tomato  flesh,  cucumber,  red  pepper,  vinegar,  garlic,  and  salt  and  pepper  to  taste  in  a  food  processor.  Pour  in  the  olive  oil  and  process  again.  Dilute  with  a  little  water  if  too  thick.  Transfer  the  soup  to  a  serving  bowl,  cover  with  plastic  wrap,  and  chill  for  at  least  1  hour. ");
			ins.add("Serve  the  gazpacho  with  bowls  of  the  tomatoes,  cucumber,  red  pepper,  egg  yolk  and  white,  and  a  cruet  of  olive  oil,  to  add  as  garnishes.");
			db.insertRecipe(new Recipe("Gazpacho", "", 4, true, temp, ins, tags, "1 1/2 Hours"));
			
			temp.clear();
			temp.add(new Ingredient("Garlic", "single", "2"));
			temp.add(new Ingredient("Salt", "teaspoon", "1/4"));
			temp.add(new Ingredient("Egg", "single", "2"));
			temp.add(new Ingredient("Lemon", "single", "1"));
			temp.add(new Ingredient("Olive oil", "cup", "1"));
			tags.clear();
			tags.add("Dinner");
			tags.add("Kosher");
			tags.add("Vegetarian");
			ins.clear();
			ins.add("Juice lemon, split and peel 8 cloves of garlic.");
			ins.add("Arrange  the  fish  pieces  in  a  shallow,  lightly  greased  baking  dish,  preferably  earthenware.  Sprinkle  with  salt  and  pepper. ");
			ins.add("Place  the  garlic  and  salt  in  a  mortar  and  mash  to  a  paste  (or  put  the  garlic  through  a  garlic  press,  then  mix  in  the  salt).  Transfer  to  a  processor,  add  the  egg  yolks  and  lemon  juice,  and  beat  a  few  seconds.  With  the  motor  running,  drizzle  in  the  oil  until  a  mayonnaise  is  formed. ");
			ins.add("Spoon  some  allioli  over  the  fish  (save  the  rest  for  some  other  purpose),  place  in  a  450°F  oven,  and  bake  10  minutes  to  each  inch  of  thickness.");
			db.insertRecipe(new Recipe("Baked Monkfish", "", 4, true, temp, ins, tags, "40 Minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Garlic", "single", "1"));
			temp.add(new Ingredient("Almond", "cup", "1"));
			temp.add(new Ingredient("Bell Pepper", "single", "2"));
			temp.add(new Ingredient("Sherry Vinegar", "tablespoon", "2"));
			temp.add(new Ingredient("Paprika", "teaspoon", "1"));
			temp.add(new Ingredient("Dried Oregano", "teaspoon", "1/2"));
			temp.add(new Ingredient("Olive Oil", "tablespoon", "3"));
			temp.add(new Ingredient("Ground beef", "pound", "2"));
			temp.add(new Ingredient("Salt", "teaspoon", "2"));
			temp.add(new Ingredient("Black pepper", "teaspoon", "1/2"));
			temp.add(new Ingredient("Manchego cheese", "ounce", "5"));
			temp.add(new Ingredient("Burger Bun", "single", "4"));
			tags.clear();
			tags.add("Dinner");
			tags.add("Lunch");
			ins.clear();
			ins.add("Build  a  fire  for  banked  grilling  in  an  outdoor  grill.  If  a  charcoal  grill,  let  the  coals  burn  until  they  are  covered  with  white  ash.  Spread  out  the  mound  of  coals  into  a  bank,  with  one  side  about  two  coals  deep,  and  the  other  side  of  the  slope  with  a  scattering  of  single  coals.  For  a  gas  grill,  preheat  the  grill  on  high.  Leave  one  side  on  high  and  turn  the  other  side  to  low.  In  both  cases,  you  will  have  two  areas  for  cooking,  one  hot  and  the  other  cooler. ");
			ins.add("To  make  the  romesco,  with  the  machine  running,  drop  the  garlic  through  the  feed  tube  of  a  food  processor  fitted  with  the  metal  chopping  blade  to  finely  chop  it.  Add  the  almonds,  red  peppers,  vinegar,  paprika,  and  oregano.  With  the  machine  running,  gradually  add  the  oil,  then  season  with  salt  and  pepper  to  taste.  Transfer  the  sauce  to  a  bowl.  (The  romesco  can  be  made  up  to  2  weeks  ahead,  covered,  and  refrigerated.  Bring  to  room  temperature  before  serving.) ");
			ins.add("Working  as  gently  and  quickly  as  possible  (overmixing  compacts  the  meat  and  makes  tough  burgers),  mix  the  ground  round,  salt,  and  pepper.  Lightly  form  into  4  patties  about  4  inches  wide.  Make  an  indentation,  about  2  inches  wide  and  ½  inch  deep,  in  the  center  of  each  burger  to  help  it  keep  its  shape  during  grilling. ");
			ins.add("Lightly  oil  the  cooking  grate.  Place  the  burgers  on  the  hot  area  of  the  grill.  Cover  and  cook,  turning  once,  until  the  outside  is  seared  with  grill  marks,  about  2  minutes  per  side.  Move  the  hamburgers  to  the  cooler  side  of  the  grill  and  cover.  Cook,  turning  once,  until  the  hamburgers  feel  somewhat  firm  but  not  resistant  when  pressed  in  the  center,  about  4  minutes  for  medium-rare.  If  using  a  meat  thermometer,  insert  it  horizontally  through  the  side  of  the  burger  to  reach  the  center;  it  should  read  125°F.  During  the  last  2  minutes,  top  each  burger  with  the  cheese,  and  place  the  buns  on  the  grill  to  toast  lightly  and  warm  through. ");
			ins.add("Place  a  burger  in  each  bun,  top  with  a  large  dollop  of  romesco  sauce,  and  serve  immediately.");
			db.insertRecipe(new Recipe("Spanish Burgers with Romesco and Manchego cheese", "", 4, true, temp, ins, tags, "40 Minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Butter", "tablespoon", "2"));
			temp.add(new Ingredient("Leek", "single", "2"));
			temp.add(new Ingredient("Flour", "teaspoon", "2"));
			temp.add(new Ingredient("Chicken stock", "cup", "1/2"));
			temp.add(new Ingredient("Cooked chicken", "cup", "2 1/2"));
			temp.add(new Ingredient("Salt", "teaspoon", "1/2"));
			temp.add(new Ingredient("Thyme", "teaspoon", "1"));
			temp.add(new Ingredient("Lemon", "single", "1"));
			temp.add(new Ingredient("Puff pastry", "pound", "1"));
			temp.add(new Ingredient("Egg", "single", "1"));
			tags.clear();
			tags.add("Dinner");
			ins.clear();
			ins.add("Thinly slice leeks. Debone and cop chicken. Squeeze lemon. Beat egg.");
			ins.add("Melt  the  butter  in  a  saucepan  over  low  heat.  Add  the  leeks  and  cook  for  5  minutes  until  tender.  Sprinkle  in  the  flour  and  stir.  Stir  in  the  stock  and  bring  to  a  boil,  stirring  often.  Remove  from  the  heat  and  stir  in  the  chicken,  thyme,  and  lemon  juice.  Season  well  with  salt  and  pepper.  Cover  with  plastic  wrap  and  let  cool. ");
			ins.add("Preheat  the  oven  to  425°F  (220°C).  Dampen  a  large  baking  sheet.  Roll  out  one  sheet  of  the  puff  pastry  on  a  lightly  floured  surface.  Trim  into  a  10  ×  6in  (30  ×  15cm)  rectangle.  Place  the  pastry  on  the  baking  sheet.  Roll  out  and  trim  the  remaining  pastry  to  10  ×  7in  (30  ×  18cm)  rectangle.  Lightly  dust  it  with  flour,  then  fold  in  half  lengthwise.  Make  cuts  ½  in  (1cm)  apart  along  the  folded  edge  to  within  1in  (2.5cm)  of  the  outer  edge. ");
			ins.add("Spoon  evenly  over  the  puff  pastry  base,  leaving  a  1in  (2.5cm)  border.  Dampen  the  edges  of  the  pastry  with  water.  Place  the  second  piece  of  pastry  on  top  and  press  the  edges  together  to  seal;  trim  off  the  excess.  Brush  with  beaten  egg.  Bake  for  25  minutes  or  until  golden-brown  and  crisp.  Cool  briefly,  then  slice  and  serve  hot.");
			ins.add("Spoon 1/3 of a cup of batter into a skillet for each pancake over a medium-sized flame.");
			db.insertRecipe(new Recipe("Chicken Jalousie", "", 4, true, temp, ins, tags, "45 Minutes"));
		
			temp.clear();
			temp.add(new Ingredient("Cucumber", "single", "1"));
			temp.add(new Ingredient("Greek yogurt", "cup", "1 1/2"));
			temp.add(new Ingredient("Garlic", "single", "1"));
			temp.add(new Ingredient("Dill", "tablespoon", "2"));
			temp.add(new Ingredient("Olive oil", "tablespoon", "2"));
			temp.add(new Ingredient("Red wine vinegar", "tablespoon", "1"));
			tags.clear();
			tags.add("Vegan");
			tags.add("Kosher");
			tags.add("Dinner");
			tags.add("Kosher");
			ins.clear();
			ins.add("Peel and coarsely grate cucumber. Crush three garlic cloves. Chop dill, substitute with mint if desired.");
			ins.add("Put  the  cucumber  in  a  bowl,  sprinkle  with  salt,  and  let  stand  for  30  minutes. ");
			ins.add("Rinse  the  cucumber  well  to  remove  the  excess  salt.  A  handful  at  a  time,  squeeze  out  the  excess  liquid,  putting  the  cucumber  in  a  bowl. ");
			ins.add("Add  the  yogurt  and  stir.  Add  the  garlic,  mint,  olive  oil,  and  vinegar  and  mix  well.  Cover  with  plastic  wrap  and  chill  until  needed.");
			db.insertRecipe(new Recipe("Tzatziki", "", 4, true, temp, ins, tags, "45 Minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Yogurt", "cup", "1"));
			temp.add(new Ingredient("Cucumber", "cup", "1"));
			temp.add(new Ingredient("Cilantro", "tablespoon", "2"));
			temp.add(new Ingredient("Mint", "tablespoon", "1"));
			temp.add(new Ingredient("Cumin powder", "tablespoon", "1/2"));
			tags.clear();
			tags.add("Vegetarian");
			tags.add("Dessert");
			tags.add("Kosher");
			ins.clear();
			ins.add("Dice cucumber, chop cilantro and mint.");
			ins.add("Stir  yogurt  in  a  bowl  until  smooth  and  creamy.  Stir  in  cucumber,  cilantro,  mint  and  cumin.  Season  with  salt  and  pepper. ");
			ins.add("Cover  and  chill  well  before  serving.  (Can  be  refrigerated  for  up  24  hours.)");
			db.insertRecipe(new Recipe("Cucumber Raita", "", 8, true, temp, ins, tags, "15 Minutes"));
			
			temp.clear();
			temp.add(new Ingredient("Bread slice", "single", "4"));
			temp.add(new Ingredient("Avocado", "single", "1"));
			temp.add(new Ingredient("Mushroom", "cup", "2"));
			temp.add(new Ingredient("Almond", "cup", "1/3"));
			temp.add(new Ingredient("Tomato", "single", "1"));
			temp.add(new Ingredient("Swiss cheese slice", "single", "4"));
			tags.clear();
			tags.add("Vegan");
			tags.add("Kosher");
			tags.add("Lunch");
			tags.add("Kosher");
			ins.clear();
			ins.add("Toast bread, slice avocado, mushrooms, almonds, and tomato.");
			ins.add("Preheat oven broiler. Lay the toasted bread out on a baking sheet. Top each slice of bread with 1/4 of the avocado, mushrooms, almonds, and tomato slices. Top each with a slice of Swiss cheese.");
			ins.add("Broil the open-face sandwiches until the cheese melts and begins to bubble, about 2 minutes. Serve the sandwiches warm.");
			db.insertRecipe(new Recipe("California Melt", "", 4, true, temp, ins, tags, "10 Minutes"));

			temp.clear();
			temp.add(new Ingredient("Pine nut", "cup", "1/4"));
			temp.add(new Ingredient("Quinoa", "cup", "1"));
			temp.add(new Ingredient("Lemon juice", "cup", "1/4"));
			temp.add(new Ingredient("Celery", "single", "2"));
			temp.add(new Ingredient("Onion", "single", "1/4"));
			temp.add(new Ingredient("Cayenne peper", "teaspoon", "1/4"));
			temp.add(new Ingredient("Ground cumin", "teaspoon", "1/2"));
			temp.add(new Ingredient("Bunch parsley", "single", "1"));
			tags.clear();
			tags.add("Vegan");
			tags.add("Kosher");
			tags.add("Lunch");
			tags.add("Kosher");
			ins.clear();
			ins.add("Chop celery, red onion, and parsley.");
			ins.add("Toast the pine nuts briefly in a dry skillet over medium heat. This will take about 5 minutes, and stir constantly as they will burn easily. Set aside to cool.");
			ins.add("In a saucepan, combine the quinoa, water and salt. Bring to a boil, then reduce heat to medium and cook until quinoa is tender and water has been absorbed, about 10 minutes. Cool slightly, then fluff with a fork.");
			ins.add("Transfer the quinoa to a serving bowl and stir in the pine nuts, lemon juice, celery, onion, cayenne pepper, cumin and parsley. Adjust salt and pepper if needed before serving.");
			db.insertRecipe(new Recipe("Lemony Quinoa", "", 1, true, temp, ins, tags, "25 Minutes"));
			
			
			temp.clear();
			temp.add(new Ingredient("Ham", "pound", "1/8"));
			temp.add(new Ingredient("Brown sugar", "ounce", "1 1/2"));
			temp.add(new Ingredient("Dinner rolls", "single", "2"));
			tags.clear();
			tags.add("Lunch");
			ins.clear();
			ins.add("Place the ham in a large pot or slow cooker, and fill with enough water to cover. Bring to a boil, then reduce the heat to low, and simmer for 8 to 10 hours. Remove the meat from the water, and allow to cool. If it has cooked long enough, it will fall into pieces as you pick it up.");
			ins.add("Pull the ham apart into shreds once it is cool enough to handle. It doesn't have to be tiny shreds. Place the shredded ham into a slow cooker. Stir in the mustard and brown sugar, cover, and set to Low. Cook just until heated. Serve on dinner rolls. ");
			db.insertRecipe(new Recipe("Harvey Ham Sandwiches", "", 2, true, temp, ins, tags, "10 Hours"));

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
    	Fragment f;
    	switch (position) {
    	case 0: 
    	{
    		f = new CookbookFragment();
    		break;
    	}
    	case 1:
    	{
    		f = new CookbookFragment.ViewAllFragment();
    		break;
    	}
    	case 2:
    	{
    		f = new PantryFragment();
    		break;
    	}
    	case 3:
    	{
    		f = new ShoppingListFragment();
    		break;
    	}
    	default: f = new CookbookFragment();
    	}
        FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();         
		fragmentTransaction.replace(R.id.content_frame, f);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

        // update selected item and title, then close the drawer
        drawerList.setItemChecked(position, true);
        setTitle(tabTitles[position]);
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getActionBar().setTitle(title);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
    	if (getFragmentManager().getBackStackEntryCount() <= 1) {
    		this.finish();
    	}
    	else if (getFragmentManager().getBackStackEntryCount() > 1){
            getFragmentManager().popBackStack();
        }
        else super.onBackPressed();
    }
    
    
	@Override
	public void onStop() {
		super.onStop();
		//db.close();
	}

}
