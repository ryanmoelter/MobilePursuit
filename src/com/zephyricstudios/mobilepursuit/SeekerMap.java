package com.zephyricstudios.mobilepursuit;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.zephyricstudios.mobilepursuit.MapsItemizedOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

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
import android.view.View;
import android.view.View.OnClickListener;

//Typeface
import android.graphics.Typeface;
import android.widget.TextView;
import android.widget.Toast;


public class SeekerMap extends MapActivity implements OnClickListener {
	
	ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>(); // used to dynamically store geopoints
	
	MapView mapView;                              // declaring these variables here (but not initializing them!)
	MyLocationOverlay myLocationOverlay;          // allows them to be referenced in multiple methods
	List<Overlay> mapOverlays;
	MapsItemizedOverlay itemizedoverlay;
	Drawable drawable;
	int markerCounter;
	int timerInterval;
	int secondCounter;
	Timer timer;
	
	SmsManager sm = SmsManager.getDefault();
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	
	// Typeface
	Typeface thin;
	TextView seekerTimer;
	
	String snitchNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seeker_map);
		mapView = (MapView) findViewById(R.id.mapview_seeker);
		mapView.setBuiltInZoomControls(true); 
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		drawable = this.getResources().getDrawable(R.drawable.map_marker);
        itemizedoverlay = new MapsItemizedOverlay(drawable, this);
		mapOverlays = mapView.getOverlays();
        mapOverlays.add(myLocationOverlay);
        markerCounter = 0;
        seekerTimer = (TextView)findViewById(R.id.seeker_timer);
        mapView.postInvalidate();
        timer = new Timer();
        timer.schedule(new SeekerTimerTask(), 0, 1000);
        
     // Typeface
        thin = Typeface.createFromAsset(getAssets(), "roboto_thin.ttf");
        seekerTimer.setTypeface(thin);
        
        Ref.activityState = Ref.SEEKERMAP;
        
        timerInterval = this.getIntent().getExtras().getInt(Ref.TIMER_INTERVAL_KEY);
        snitchNumber = this.getIntent().getExtras().getString(Ref.SNITCH_NUMBER_KEY);
        secondCounter = 0;
        localTextReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle bundle = intent.getExtras();

				if (bundle != null) {
				        Object[] pdusObj = (Object[]) bundle.get("pdus");
				        SmsMessage[] messages = new SmsMessage[pdusObj.length];
				        
				        // getting SMS information from Pdu.
				        for (int i = 0; i < pdusObj.length; i++) {
				                messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				        }

				        for (SmsMessage currentMessage : messages) {
				        	if(currentMessage.getDisplayMessageBody().contains("null")){
				    			CharSequence text = ("The snitch's last location could not be found.");
				    			int duration = Toast.LENGTH_SHORT;
				    			Toast toast = Toast.makeText(context, text, duration);
				    			toast.show();
				        		this.abortBroadcast();
				        	}else if(currentMessage.getDisplayMessageBody().contains(Ref.GEOPOINT)){
				        		String geoStringTemp = currentMessage.getDisplayMessageBody().replace(Ref.GEOPOINT, "");
				        		GeoPoint geoPointTemp = Ref.stringToGeoPoint(geoStringTemp); 	//add textview to display								
				        		addMarker(geoPointTemp); 												
				        		
				        		resetCounter(); //Reset the timer
				        		
				        		this.abortBroadcast();
				        	}else if(currentMessage.getDisplayMessageBody().contains(Ref.GAME_OVER)){
			        			Intent i = new Intent(context, GameOverPage.class);
			        			startActivity(i);
			        			this.abortBroadcast();
			        			finish();
			        		}
				               //currentMessage.getDisplayOriginatingAddress();		// has sender's phone number
				               //currentMessage.getDisplayMessageBody();				// has the actual message
				        }
				}
				
			}
        	
        };
        filter = new IntentFilter();
        filter.addAction(Ref.ACTION);
        this.registerReceiver(this.localTextReceiver, filter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_snitch_map, menu);
		return true;
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		// when our activity pauses, we want to remove listening for location updates
    	myLocationOverlay.disableMyLocation();
    	//this.unregisterReceiver(this.localTextReceiver);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		// when our activity resumes, we want to register for location updates
    	myLocationOverlay.enableMyLocation();
        Ref.activityState = Ref.SEEKERMAP;
        //this.registerReceiver(this.localTextReceiver, filter);

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onClick(View v) {
		// Auto-generated method stub
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if (keyCode == KeyEvent.KEYCODE_BACK) {

	         AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	            alertDialog.setTitle("Exit Game?");
	            alertDialog.setIcon(R.drawable.ic_launcher);

	            alertDialog.setMessage("Do you really want to go back? This will remove you from the game!");
	            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface dialog, int which) {
	            	  sendImOut();
	                  finish();
	                return;
	            } }); 
	            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface dialog, int which) {
	                  dialog.cancel();
	                return;
	            }}); 
	              alertDialog.show();

	         return true;
	     }
	     return super.onKeyDown(keyCode, event);
	 }
	
	public void addMarker(GeoPoint geoPointTemp){
		markerCounter++;
		//mapView.setBuiltInZoomControls(true); 
		itemizedoverlay.addOverlay(new OverlayItem(geoPointTemp, "Point " + markerCounter, null));
		mapOverlays.add(itemizedoverlay);
		mapView.postInvalidate();
	}
	
	class SeekerTimerTask extends TimerTask {

        @Override
        public void run() {
            SeekerMap.this.runOnUiThread(new Runnable() {

                public void run() {
                	if(secondCounter>=timerInterval){
                		secondCounter = timerInterval;
                	}
                	int countdownSeconds = timerInterval - secondCounter;
            		int displayMinutes = countdownSeconds / 60;
            		int displaySeconds = countdownSeconds % 60;
            		seekerTimer.setText(String.format("%d:%02d", displayMinutes, displaySeconds));
            		secondCounter++;
                }
            });
        }
    }
	
	public void resetCounter() {
		secondCounter = 0;
	}
	
	public void sendImOut() {
		sm.sendTextMessage(snitchNumber, null, Ref.IM_OUT, null, null);
	}
}

