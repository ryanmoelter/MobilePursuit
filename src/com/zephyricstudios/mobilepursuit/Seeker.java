package com.zephyricstudios.mobilepursuit;

import java.util.ArrayList;

public class Seeker {
	
	String mName;
	String mNumber;
	
	public Seeker(String number, String name) {
		mName = name;
		mNumber = number;
	}
	
	public void deleteSeeker(ArrayList<Seeker> seekerArray, int index) {
		seekerArray.remove(index);
	}
	
	public static void createSeeker(ArrayList<Seeker> seekerArray, String number, String name) {
		seekerArray.add(new Seeker(number, name));
		
	}
	
	public String getName() {
		return mName;
	}
	
	public String getNumber() {
		return mNumber;
	}

}
