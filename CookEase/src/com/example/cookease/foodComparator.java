package com.example.cookease;

import java.util.Comparator;

public class foodComparator implements Comparator<Ingredient> {

	@Override
	public int compare(Ingredient a, Ingredient b) {
		if(a.getHave() && !b.getHave()) {
			return -1;
		} else if(!a.getHave() && b.getHave()) {
			return 1;
		} else {
			return a.getName().compareToIgnoreCase(b.getName());
		}
	}
}
