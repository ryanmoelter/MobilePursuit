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
import com.google.android.maps.OverlayItem;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

//Typeface
import android.graphics.Typeface;
import android.widget.TextView;
import android.widget.Toast;


public class SeekerMap extends MapActivity implements OnClickListener, Endable {
	
	ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>(); // used to dynamically store geopoints
	
	MapView mapView;
	MapController mapController;
	MyLocationOverlay myLocationOverlay;
	List<Overlay> mapOverlays;
	MapsItemizedOverlay itemizedOverlay, newestOverlay;
	
	int markerCounter, secondCounter, displayMinutes, displaySeconds, countdownSeconds;
	boolean findMe, navigating;
	
	Timer timer;
	
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	
	// Typeface
	Typeface thin;
	TextView seekerTimer;
	
	Group group;
	Game game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seeker_map);
		mapView = (MapView) findViewById(R.id.mapview_seeker);
		mapView.setBuiltInZoomControls(false); 
		myLocationOverlay = new MyLocationOverlay(this, mapView);
        itemizedOverlay = new MapsItemizedOverlay(
        					  this.getResources().getDrawable(
        					  R.drawable.map_marker_grey), this);
        newestOverlay = new MapsItemizedOverlay(
        					this.getResources().getDrawable(
        					R.drawable.map_marker), this);
		mapOverlays = mapView.getOverlays();
        mapOverlays.add(myLocationOverlay);
		mapController = mapView.getController();
		//mapController.setZoom(20);
		//mapController.animateTo(myLocationOverlay.getMyLocation());
        markerCounter = 0;
        seekerTimer = (TextView)findViewById(R.id.seeker_timer);
        mapView.postInvalidate();
        
        findMe = true;
        navigating = false;
        
        timer = new Timer();
        timer.schedule(new SeekerTimerTask(), 0, 1000);
        
     // Typeface
        thin = Typeface.createFromAsset(getAssets(), "roboto_thin.ttf");
        seekerTimer.setTypeface(thin);
        
        secondCounter = 0;
        
        group = Ref.group;
        
        group.setActAdapter(new ActivityAdapter() {
        	// put stuff in here
        	@Override
        	public void receiveGeopoint(String geoString) {
        		if(geoString.contains("null")) {
        			Toast.makeText(SeekerMap.this,
        						   "The runner's location could not be found",
        						   Toast.LENGTH_LONG).show();
        		} else {
            		addMarker(Ref.stringToGeoPoint(geoString));
        		}
        		resetCounter(); //Reset the timer
        	}
        	
        	@Override
        	public void receiveGameOver() {
//        		Ref.group = group;
//        		Ref.game = game;
    			startActivity(new Intent(SeekerMap.this, GameOverPage.class));
    			SeekerMap.this.end();
        	}
        	
        	
        });
        
        group.setRunning(this);
        
        game = Ref.game;
        
        localTextReceiver = group.getBroadcastReceiver();
        /*localTextReceiver = new BroadcastReceiver(){

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
				    			int duration = Toast.LENGTH_LONG;
				    			Toast toast = Toast.makeText(context, text, duration);
				    			toast.show();
				    			resetCounter();
				        		this.abortBroadcast();
				        	}else if(currentMessage.getDisplayMessageBody().contains(Ref.GEOPOINT)){
				        		String geoStringTemp = currentMessage.getDisplayMessageBody()
				        							   .replace(Ref.GEOPOINT, "");
				        		GeoPoint geoPointTemp = Ref.stringToGeoPoint(geoStringTemp);								
				        		
				        		addMarker(geoPointTemp);
				        		resetCounter(); //Reset the timer
				        		
				        		this.abortBroadcast();
				        	}else if(currentMessage.getDisplayMessageBody().contains(Ref.GAME_OVER)){
				        		navigating = false;
			        			Intent i = new Intent(context, GameOverPage.class);
			        			startActivity(i);
			        			this.abortBroadcast();
			        			finish();
			        		}
				        }
				}
				
			}
        	
        }; */
        filter = new IntentFilter();
        filter.addAction(Ref.ACTION);
        this.registerReceiver(this.localTextReceiver, filter);
        
        //startupCenterOnCurrent();
	}
	
	public void startupCenterOnCurrent() {
		// I'll try this once I make sure the rest is working
		/*new Thread() {
			public void run() {
				while(myLocationOverlay.getMyLocation() == null) {
					/*try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// Auto-generated catch block
						e.printStackTrace();
					}* /
				}
				SeekerMap.this.runOnUiThread(new Runnable {
					public void run() {
						centerOnCurrent();
						mapController.setZoom(20);
					}
				});				
			}
		}).start(); */
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// TODO startupCenterOnCurrent();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_seeker_map, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.menu_find_me:
	    	centerOnCurrent();
	    	return true;
	    case R.id.menu_find_runner:
	    	centerOnRunner();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onStop(){
		super.onStop();
    	myLocationOverlay.disableMyLocation();
	}
	
	@Override
	public void end() {
		navigating = true;
		finish();
	}
	
	@Override
	protected void onDestroy() {
		this.unregisterReceiver(this.localTextReceiver);
		if(!navigating) {
			group.sendImOut();
		}
		super.onDestroy();
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
    	myLocationOverlay.enableMyLocation();
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
			
			Ref.makeAlert("Leave Game?", "Do you want to abandon the game?",
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							group.leaveGroup();
							end();
						}
						
					}, "Yes",
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {}
						
					}, "Nevermind", this);

	        return true;
	     }
	     return super.onKeyDown(keyCode, event);
	 }
	
	public void addMarker(GeoPoint geoPointTemp){
		mapOverlays.clear();
		if(markerCounter != 0) {
			itemizedOverlay.addOverlay(newestOverlay.getItem(0));
			mapOverlays.add(itemizedOverlay); // This is in here because the phone has issues moving the map with
			                                  // a blank overlay on it.
		}
		markerCounter++;
		newestOverlay.clear();
		newestOverlay.addOverlay(new OverlayItem(geoPointTemp, "Point " + markerCounter, null));
		mapOverlays.add(myLocationOverlay);
		mapOverlays.add(newestOverlay);
		mapView.postInvalidate();
	}
	
	class SeekerTimerTask extends TimerTask {

        @Override
        public void run() {
        	if(secondCounter >= game.getInterval()){
        		secondCounter = game.getInterval();
        	}
        	countdownSeconds = game.getInterval() - secondCounter;
    		displayMinutes = countdownSeconds / 60;
    		displaySeconds = countdownSeconds % 60;
    		
            SeekerMap.this.runOnUiThread(new Runnable() {
                public void run() {
            		seekerTimer.setText(String.format("%d:%02d", displayMinutes, displaySeconds));
            		secondCounter++;
                }
            });
        }
    }
	
	public void resetCounter() {
		secondCounter = 0;
	}
	
	public void centerOnCurrent() {
		if(myLocationOverlay.getMyLocation() != null) {
			mapController.animateTo(myLocationOverlay.getMyLocation());
		} else {
			Toast toast = Toast.makeText(this, "Your location could not be found", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	public void centerOnRunner() {
		if(markerCounter != 0) {
			mapController.animateTo(newestOverlay.getItem(0).getPoint());
		} else {
			Toast toast = Toast.makeText(this, "The runner has not sent their location yet", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	public void onTimeClick(View v) {
		if(findMe) {
			centerOnCurrent();
		} else {
			centerOnRunner();
		}
		findMe = !findMe;
	}
}

