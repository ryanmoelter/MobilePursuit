package com.zephyricstudios.catchme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class ActivityAdapter {
	
	private Group group;
//	private Context context;
	
	
	// Constructors
	public ActivityAdapter(Group group) {
		this.group = group;
	}
	
//	public ActivityAdapter(Group group, Context context) {
//		this.group = group;
//		this.context = context;
//	}
	
	
	// Receiving
	public void receiveImIn(String name, String number) {
		// Remember, Group already handles sending they're in and he's in and adding
		// the new guy to people.
	}
	
	public void receiveImOut(String name) {
		// This only matters on the SnitchMap
	}
	
	public void receiveYoureIn(String name, final String number, final Context context) {
		// Default - Make a dialog for pages other than SnitchMainPage, assume no game has
		//           been started.
		
		Ref.makeAlert("You're invited", name + " wants you to join their game.",
				new DialogInterface.OnClickListener() { // Positive
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						group.sendImIn(context, number);
						Intent i = new Intent(context, SnitchMainPage.class);
						context.startActivity(i);
						ActivityAdapter.this.end();
						
					}
				}, "Okay", 
				new DialogInterface.OnClickListener() { // Negative
					
					@Override
					public void onClick(DialogInterface dialog, int which) {}
					
				}, "No, go away", context);
		
//		if(group.getImRunner()) {
//			Ref.makeAlert("You're invited", name + " wants you to join their game. This "
//					+ "will abandon the current group.",
//					new DialogInterface.OnClickListener() { // Positive
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							group.sendImIn(context, number);
//							group.sendImOut();
//							Intent i = new Intent(context, SnitchMainPage.class);
//							context.startActivity(i);
//							ActivityAdapter.this.end();
//							
//						}
//					}, "Okay", 
//					new DialogInterface.OnClickListener() { // Negative
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							
//						}
//					}, "No, go away", context);
//		} else {
//			Ref.makeAlert("You're invited", name + " wants you to join their game.",
//					new DialogInterface.OnClickListener() { // Positive
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							group.sendImIn(context, number);
//							Intent i = new Intent(context, SnitchMainPage.class);
//							context.startActivity(i);
//							ActivityAdapter.this.end();
//							
//						}
//					}, "Okay", 
//					new DialogInterface.OnClickListener() { // Negative
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							
//						}
//					}, "No, go away", context);
//		}
	}
	
	public void receiveYoureOut() {}
	
	public void receiveGeopoint(String Geopoint) {}
	
	public void receiveGameStart(int interval) {}
	
	public void receiveGameOver() {}
	
	
	// Miscellaneous
	public void end() {}
	
}
