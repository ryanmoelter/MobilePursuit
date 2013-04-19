package com.zephyricstudios.catchme;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
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
	public static final String GEOPOINT = "gp: ";
	public static final String GAME_START = "Start game. ";
	public static final String GAME_OVER = "Game over";
	
	public static Group group;
	
	
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

		alert.setTitle("Enter Your Name");
		
		if(firstTime) {
			alert.setMessage("Please enter your name. Don't worry; you can change it later.");
		} else {
			alert.setMessage("Please enter your name.");
		}
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(context);
		/*input.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				// TOD Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TOD Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TOD Auto-generated method stub
				
			}});*/
		input.setText(sp.getString(USERNAME_KEY, ""));
		alert.setView(input);

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
	
	public static void makeAlert(String title, String content, OnClickListener onPositive,
								 String positiveText, OnClickListener onNegative,
								 String negativeText, Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(content);
		
		alert.setPositiveButton(positiveText, onPositive);
		alert.setNegativeButton(negativeText, onNegative);
	}
	
	public static void joinGameAlert(final String name, final String number, final Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Join Game");
		alert.setMessage(name + " wants you to join their game.");
		
		alert.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int button) {
				Group.joinGame(context, SmsManager.getDefault(), number);
			}
		});
		
		alert.setNegativeButton("No, go away", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
	}
	
	// This is experimental, I have no idea how exactly to get a global BroadcastReceiver working
	/*public static BroadcastReceiver createBroadcastReceiver() {
		return new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Bundle bundle = intent.getExtras();
				
				String name, number, message;

				if (bundle != null) {
					Object[] pdusObj = (Object[]) bundle.get("pdus");
				    SmsMessage[] messages = new SmsMessage[pdusObj.length];
				    
				    // getting SMS information from Pdu.
				    for (int i = 0; i < pdusObj.length; i++) {
				        messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				    }

				    for (SmsMessage currentMessage : messages) {
				    	if(currentMessage.getDisplayMessageBody().contains(CATCH_ME)) {
				    		number = currentMessage.getDisplayOriginatingAddress();
				    		message = currentMessage.getDisplayMessageBody().replace(CATCH_ME, "");
				    		
				    		if(message.contains(IM_IN)){
				    			name = message.replace(IM_IN, "");
					    		group.receiveImIn(name, number);
					    		
					    	} else if(message.contains(IM_OUT)) {
					    		group.receiveImOut(number);
					    		
					    	} else if(message.contains(YOURE_IN)) {
					    		name = message.replace(YOURE_IN, "");
					    		Group.receiveYoureIn(name, number, context);
					    		
					    	} else if(message.contains(YOURE_OUT)) {
					    		group.getActAdapter().receiveYoureOut();
					    		
					    	} else if(message.contains(HES_IN)) {
					    		name = message.replace(HES_IN, "");
					    		
					    		group.receiveHesIn(name, number);
					    		
					    	} else if(message.contains(HES_OUT)) {
					    		group.receiveHesOut(number);
					    		
					    	} else if(message.contains(GEOPOINT)) {
					    		group.getActAdapter().receiveGeopoint(
					    				message.replace(GEOPOINT, ""));
					    		
					    	} else if(message.contains(GAME_START)) {
					    		group.getActAdapter().receiveGameStart(
					    				Integer.valueOf(message.replace(GAME_START, "")));
					    		
					    	} else if(message.contains(GAME_OVER)) {
					    		group.getActAdapter().receiveGameOver();
					    		
					    	}
				    		
				    		this.abortBroadcast();
				    	}
				        //currentMessage.getDisplayOriginatingAddress();		// has sender's phone number
				        //currentMessage.getDisplayMessageBody();				// has the actual message
				    }
				}
			}
		};
	}*/
}
