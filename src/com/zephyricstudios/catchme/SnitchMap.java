 package com.zephyricstudios.catchme;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.zephyricstudios.catchme.MapsItemizedOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Typeface;

public class SnitchMap extends MapActivity implements OnClickListener {
	
	ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>(); // used to dynamically store geopoints
	
	MapView mapView;
	MapController mapController;
	MyLocationOverlay myLocationOverlay;
	List<Overlay> mapOverlays;
	MapsItemizedOverlay itemizedoverlay;
	Drawable drawable;
	TextView snitchTimer;
	SmsManager sm = SmsManager.getDefault();
	ArrayList<Seeker> seekerArray;
	Timer timer;
	int timerInterval;
	int secondCounter;
	RelativeLayout buttonSnitchTagged;
	
	boolean mapExpanded, isGameOver;
	
	// Typeface
	Typeface thin, light;
	TextView textTagged;
	
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	
	//Texting
	String textContent;
	//Thread sendTexts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snitch_map);
		extractMapView();
		mapController = mapView.getController();
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapOverlays = mapView.getOverlays();
        mapOverlays.add(myLocationOverlay);
        snitchTimer = (TextView)findViewById(R.id.snitch_timer);
        seekerArray = this.getIntent().getExtras().getParcelableArrayList(Ref.SEEKER_ARRAY_KEY);
        secondCounter = 0;
        timerInterval = this.getIntent().getExtras().getInt(Ref.TIMER_INTERVAL_KEY);
        timer = new Timer();
        timer.schedule(new SnitchTimerTask(), 0, 1000);
        Ref.activityState = Ref.SNITCHMAP;
        buttonSnitchTagged = (RelativeLayout)findViewById(R.id.button_snitch_tagged);
        buttonSnitchTagged.setOnClickListener(this);
        mapExpanded = false;
        isGameOver = true;
        
        // Typeface
        thin = Typeface.createFromAsset(getAssets(), "roboto_thin.ttf");
        snitchTimer.setTypeface(thin);
        light = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
        textTagged = (TextView)findViewById(R.id.text_snitch_tagged);
        textTagged.setTypeface(light);
        
        localTextReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle bundle = intent.getExtras();

				if (bundle != null) {
				    Object[] pdusObj = (Object[]) bundle.get("pdus");
				    SmsMessage[] messages = new SmsMessage[pdusObj.length];
				    
				    // getting SMS information from Pdu.
				    for(int i = 0; i < pdusObj.length; i++) {
				        messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				    }

				    for (SmsMessage currentMessage : messages) {
				    	if(currentMessage.getDisplayMessageBody().contains(Ref.IM_OUT)){
				    		//Context cont = getApplicationContext();
				    		Seeker.deleteSeekerByNum(currentMessage.getDisplayOriginatingAddress(),
				    				seekerArray);
				    		CharSequence text = (Seeker.getSeekerNameByNum(
				    				currentMessage.getDisplayOriginatingAddress(), seekerArray)
				    				+ " has left the game.");
				    		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				    		toast.show();
				    		
				        	this.abortBroadcast();
				        	SnitchMap.this.checkSeekerArrayEmpty();
				        } else if(currentMessage.getDisplayMessageBody().contains(Ref.IM_IN)) {
				        	Seeker.createSeeker(currentMessage.getDisplayOriginatingAddress(),
				        			currentMessage.getMessageBody().replace(Ref.IM_IN, ""),
				        			seekerArray);
				        	Toast.makeText(SnitchMap.this,
				        			currentMessage.getMessageBody().replace(Ref.IM_IN, "") + " has joined the game",
				        			Toast.LENGTH_SHORT);
				        }
				           //currentMessage.getDisplayOriginatingAddress();		// has sender's phone number
				           //currentMessage.getDisplayMessageBody();			// has the actual message
				    }
				}
			}
        };
        
        filter = new IntentFilter();
        filter.addAction(Ref.ACTION);
        this.registerReceiver(this.localTextReceiver, filter);
        
        /*sendTexts = new Thread() {
        	public void run() {
        		for(int index = 0; seekerArray.size() > index; index++) {
    				sm.sendTextMessage(seekerArray.get(index).getNumber(), null, textContent, null, null);
    				
    			}
        	}
        };*/
        
	}
	
	public void checkSeekerArrayEmpty() {
		if(seekerArray.isEmpty()) {
			isGameOver = false;
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	        alertDialog.setTitle("Game over");
	        alertDialog.setIcon(R.drawable.ic_launcher);

	        alertDialog.setMessage("The seekers have all left the game.");
	        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int which) {
	        		finish();
	        		return;
	        	}
	        });
	        alertDialog.show();
		}
	}
	
	public void sendTexts() {
		// This might be making it stop sending texts a little in to the game.
		new Thread(new Runnable() {
			public void run() {
				for(int index = 0; seekerArray.size() > index; index++) {
    				sm.sendTextMessage(seekerArray.get(index).getNumber(), null, textContent, null, null);
    			}
			}
		}).start();
		/*for(int index = 0; seekerArray.size() > index; index++) {
			sm.sendTextMessage(seekerArray.get(index).getNumber(), null, textContent, null, null);
		} */
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_snitch_map, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.menu_find_me:
	    	centerOnMe();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		// when our activity pauses, we want to remove listening for location updates
    	//myLocationOverlay.disableMyLocation();
		//this.unregisterReceiver(this.localTextReceiver);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		myLocationOverlay.disableMyLocation();
		this.unregisterReceiver(this.localTextReceiver);
		timer.cancel();
		if(isGameOver) {
			textContent = Ref.GAME_OVER;
			sendTexts();
		}
		super.onDestroy();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		// when our activity resumes, we want to register for location updates
    	myLocationOverlay.enableMyLocation();
    	//Ref.activityState = Ref.SNITCHMAP;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onClick(View buttonClicked) {
		switch(buttonClicked.getId()) {
		case R.id.button_snitch_tagged:
			showEndGameAlert();
			break;
		case R.id.seeker_timer:
			centerOnMe();
			break;
		case R.id.mapview:
			if(mapExpanded) {
				expandMap();
			} else {
				compressMap();
			}
			mapExpanded = !mapExpanded;
			break;
		}
	}
	
	public void extractMapView(){                           // extracts mapview from layout in order to add overlays
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);               // enables zoom
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {

			showEndGameAlert();

	        return true;
	     }
	     return super.onKeyDown(keyCode, event);
	 }
	
	class SnitchTimerTask extends TimerTask {

        @Override
        public void run() {
            SnitchMap.this.runOnUiThread(new Runnable() {

                public void run() {
                	if(secondCounter >= timerInterval){
                		//put in texting
                		textContent = Ref.GEOPOINT + String.valueOf(myLocationOverlay.getMyLocation());
                		sendTexts();
                		
                		/*if(seekerArray != null) {
                			for(int j = 0 ; j < seekerArray.size(); j++){
                				String textContent = Ref.GEOPOINT + String.valueOf(myLocationOverlay.getMyLocation());
                				sm.sendTextMessage(seekerArray.get(j).getNumber(), null, textContent, null, null);
                			}
                		}*/
                		
                		secondCounter = 0;
                	}
                	int countdownSeconds = timerInterval - secondCounter;
            		int displayMinutes = countdownSeconds / 60;
            		int displaySeconds = countdownSeconds % 60;
            		snitchTimer.setText(String.format("%d:%02d", displayMinutes, displaySeconds));
            		secondCounter++;
                }
            });
        }
	}
	
	public void endGame() {
		Intent i = new Intent(this, GameOverPage.class);
		startActivity(i);
		finish();
	}
	
	public void showEndGameAlert() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("End game?");
        alertDialog.setIcon(R.drawable.ic_launcher);

        alertDialog.setMessage("Do you want to end the game?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) {
        		textContent = Ref.GAME_OVER;
        		//sendTexts();
        		
        		/*for(int j = 0; j < seekerArray.size(); j++) {
        			sm.sendTextMessage(seekerArray.get(j).getNumber(), null, Ref.GAME_OVER, null, null);
        		}*/
        		//myLocationOverlay.disableMyLocation();
        		//timer.cancel();
        		
        		endGame();
        		return;
        	}
        }); 
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) {
        		dialog.cancel();
        		return;
            }
        }); 
        alertDialog.show();
	}
	
	public void centerOnMe() {
		if(myLocationOverlay.getMyLocation() != null) {
			mapController.animateTo(myLocationOverlay.getMyLocation());
		} else {
			Toast.makeText(this, "Your location could not be found", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void expandMap() {
		Toast.makeText(this, "Expanded!", Toast.LENGTH_SHORT).show();
	}
	
	public void compressMap() {
		Toast.makeText(this, "Collapsed!", Toast.LENGTH_SHORT).show();
	}

}