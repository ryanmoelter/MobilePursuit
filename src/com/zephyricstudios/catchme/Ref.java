package com.zephyricstudios.catchme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

public class Ref {
	
	public static final int SETTINGS_PAGE_RESULT_CODE = 2045;
	
	public static final String SEEKER_ARRAY_KEY = "seeker array";
	public static final String GROUP_KEY = "group";
	public static final String SEEKER_NUMBERS_KEY = "seeker numbers";
	public static final String SEEKER_NAMES_KEY = "seeker names";
	public static final String TIMER_INTERVAL_KEY = "interval";
	public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	public static final String STORED_PREFERENCES_KEY = "StoredPrefs";
	public static final String SHARED_PREFS_DEFAULT = "nope";
	public static final String USERNAME_KEY = "username";
	public static final String SNITCH_NUMBER_KEY = "snitch number";
	
	//Texting codes
	public static final String CATCH_ME = ".Catch Me. ";
	public static final String IM_IN ="I'm in. ";
	public static final String IM_OUT = "I'm out";
	public static final String YOURE_IN = "You're in. ";
	public static final String YOURE_OUT = "You're out";
	public static final String HES_IN = "He's in. ";
	public static final String HES_OUT = "He's out. ";
	public static final String YOURE_RUNNER = "You're runner";
	public static final String IM_RUNNER = "I'm runner";
	public static final String GEOPOINT = "gp: ";
	public static final String GAME_START = "Start game. ";
	public static final String GAME_OVER = "Game over";
	
	public static Group group;
	public static Game game;
	
	
	public static GeoPoint stringToGeoPoint(String geoString){
		String testString = geoString.replace("-", "").replace(",", "");
		int latitudeInt, longitudeInt;
		String latitudeString, longitudeString;
		GeoPoint geoPoint;
		if(testString.length() >= 14 && testString.length() <= 17) {
			int commaIndex = geoString.indexOf(",");
			
			latitudeString = geoString.substring(0, (commaIndex));
			longitudeString = geoString.substring((commaIndex+1));
			
			latitudeInt = java.lang.Integer.parseInt(latitudeString);
			longitudeInt = java.lang.Integer.parseInt(longitudeString);
			
			geoPoint = new GeoPoint(latitudeInt, longitudeInt);
			return geoPoint;
		} else {
			return null;
		}
	}
	
	public static void changeName(final Context context, boolean firstTime) {
		SharedPreferences sp = context.getSharedPreferences(Ref.STORED_PREFERENCES_KEY, Context.MODE_PRIVATE);
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("Enter Your Name");
		
		if(firstTime) {
			alert.setMessage("Please enter your name. Don't worry; you can change it later.");
		} else {
			alert.setMessage("Please enter your name.");
		}
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(context);
		input.setText(sp.getString(USERNAME_KEY, ""));
		alert.setView(input);
		
		// TODO Make dialog stay open if the name isn't valid
		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newName = input.getText().toString();
				if(newName.replace(" ", "") != "") {
					SharedPreferences sp = context.getSharedPreferences(Ref.STORED_PREFERENCES_KEY, Context.MODE_PRIVATE);
					Editor spEditor = sp.edit();
					spEditor.putString(Ref.USERNAME_KEY, newName);
					spEditor.commit();
				} else if(newName.contains(" ")) {
					Toast toast = Toast.makeText(context, "A space is not a name", Toast.LENGTH_SHORT);
	    			toast.show();
				} else if(newName == "") {
					Toast toast = Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT);
	    			toast.show();
				}
			}
		});
		
		if(!firstTime) {
			alert.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
		}

		alert.show();
	}
	
	//Use this to easily make an alert dialog
	public static void makeAlert(String title, String content, OnClickListener onPositive,
								 String positiveText, OnClickListener onNegative,
								 String negativeText, Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(content);
		
		alert.setPositiveButton(positiveText, onPositive);
		alert.setNegativeButton(negativeText, onNegative);
	}
	
	public static void makeAlert(String title, String content, OnClickListener onPositive,
			 String positiveText, Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(content);
		
		alert.setPositiveButton(positiveText, onPositive);
	}
}