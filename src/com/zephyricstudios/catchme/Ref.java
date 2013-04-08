package com.zephyricstudios.catchme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;

import com.google.android.maps.GeoPoint;

public class Ref {
	
	static final int MAIN = 115;
	static final int SEEKERMAIN = 215;
	static final int SNITCHMAIN = 315;
	static final int SEEKERWAITING = 267;
	static final int SNITCHMAP = 354;
	static final int SEEKERMAP = 254;
	static final int CONFUSED = 415;
	
	static final int SETTINGS_PAGE_RESULT_CODE = 2045;
	
	static final String SEEKER_ARRAY_KEY = "seeker array";
	static final String SEEKER_NUMBERS_KEY = "seeker numbers";
	static final String SEEKER_NAMES_KEY = "seeker names";
	static final String TIMER_INTERVAL_KEY = "interval";
	static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	static final String STORED_PREFERENCES_KEY = "StoredPrefs";
	static final String SHARED_PREFS_DEFAULT = "nope";
	static final String USERNAME_KEY = "username";
	static final String SNITCH_NUMBER_KEY = "snitch number";
	
	//Texting codes
	static final String IM_IN =".I'm in. ";
	static final String IM_OUT = ".I'm out";
	static final String YOURE_OUT = ".You're out";
	static final String GEOPOINT = ".gp: ";
	static final String GAME_START = ".Start game. ";
	static final String GAME_OVER = ".Game over";
	
	static int activityState;
	
	public static GeoPoint stringToGeoPoint(String geoString){
		String testString = geoString.replace("-", "").replace(",", "");
		int latitudeInt;
		int longitudeInt;
		String latitudeString;
		String longitudeString;
		GeoPoint geoPoint;
		if((testString.length()==14 || testString.length()==15 || testString.length()==16 || testString.length()==17)
		&& (true)){											//add logic to check for only number characters
			int commaIndex = geoString.indexOf(",");
			latitudeString = geoString.substring(0, (commaIndex));
			longitudeString = geoString.substring((commaIndex+1));
			latitudeInt = java.lang.Integer.parseInt(latitudeString);
			longitudeInt = java.lang.Integer.parseInt(longitudeString);
			geoPoint = new GeoPoint(latitudeInt, longitudeInt);
			return geoPoint;
		} else{
			return null;
		}
	}
	
	// Having an issue with the getSharedPreferences() call
	public static void changeName(final Context context, boolean firstTime) {
		SharedPreferences sp = context.getSharedPreferences(Ref.STORED_PREFERENCES_KEY, Context.MODE_PRIVATE);
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("Enter User Name");
		alert.setMessage("Please enter your new name.");

		// Set an EditText view to get user input 
		final EditText input = new EditText(context);
		alert.setView(input);

		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String value = input.getText().toString();
			if(value != "") {
				SharedPreferences sp = context.getSharedPreferences(Ref.STORED_PREFERENCES_KEY, Context.MODE_PRIVATE);
	    		Editor spEditor = sp.edit();
				spEditor.putString(Ref.USERNAME_KEY, value);
		  		spEditor.commit();
			}
		  }
		});
		
		String negativeText = "Nevermind";
		if(!firstTime) {
		alert.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    dialog.cancel();
		  }
		});
		}

		alert.show();
	}
}
