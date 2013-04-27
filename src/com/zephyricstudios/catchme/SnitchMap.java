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

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Typeface;

public class SnitchMap extends MapActivity implements OnClickListener, Endable {
	
	ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>(); // used to dynamically store geopoints
	
	MapView mapView;
	MapController mapController;
	MyLocationOverlay myLocationOverlay;
	List<Overlay> mapOverlays;
	MapsItemizedOverlay itemizedoverlay;
	Drawable drawable;
	
	TextView snitchTimer;
	Timer timer;
	int secondCounter, displayMinutes, displaySeconds, countdownSeconds, timerInterval;
	
	RelativeLayout buttonSnitchTagged;
	
	boolean mapExpanded, navigating;
	
	// Typeface
	Typeface thin, light;
	TextView textTagged;
	
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	
	Group group;
	Game game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snitch_map);
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(false);
		
		mapController = mapView.getController();
		
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		
		mapOverlays = mapView.getOverlays();
        mapOverlays.add(myLocationOverlay);

        secondCounter = 0;
        snitchTimer = (TextView)findViewById(R.id.snitch_timer);
        
        timer = new Timer();
        timer.schedule(new SnitchTimerTask(), 0, 1000);
        
        buttonSnitchTagged = (RelativeLayout)findViewById(R.id.button_snitch_tagged);
        buttonSnitchTagged.setOnClickListener(this);
        
        mapExpanded = false;
        navigating = false;
        
        // Typeface
        thin = Typeface.createFromAsset(getAssets(), "roboto_thin.ttf");
        snitchTimer.setTypeface(thin);
        light = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
        textTagged = (TextView)findViewById(R.id.text_snitch_tagged);
        textTagged.setTypeface(light);
        
        if(Ref.game == null) {
        	game = new Game();
        	Ref.game = game;
        } else {
        	game = Ref.game;
        }
        group = Ref.group;
        timerInterval = game.getInterval();
        
        group.setActAdapter(new ActivityAdapter());
        group.setRunning(this);
        group.setContext(this);
        
        localTextReceiver = group.getBroadcastReceiver();
        
        filter = new IntentFilter();
        filter.addAction(Ref.ACTION);
        this.registerReceiver(this.localTextReceiver, filter);
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
	public void end() {
		navigating = true;
		finish();
	}
	
	@Override
	protected void onDestroy() {
		myLocationOverlay.disableMyLocation();
		this.unregisterReceiver(this.localTextReceiver);
		timer.cancel();
		if(!navigating) {
			group.leaveGroup();
		}
		super.onDestroy();
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
        	if(secondCounter >= game.getInterval()){
        		if(myLocationOverlay.getMyLocation() == null) {
        			group.sendGeopoint(null);
        		} else {
        			group.sendGeopoint(String.valueOf(myLocationOverlay.getMyLocation()));
        		}
        		
        		secondCounter = 0;
        	}
        	countdownSeconds = game.getInterval() - secondCounter;
    		displayMinutes = countdownSeconds / 60;
    		displaySeconds = countdownSeconds % 60;
    		
            SnitchMap.this.runOnUiThread(new Runnable() {
                public void run() {
            		snitchTimer.setText(String.format("%d:%02d", displayMinutes, displaySeconds));
            		secondCounter++;
                }
            });
        }
	}
	
	public void endGame() {
		group.sendGameOver();
		startActivity(new Intent(this, GameOverPage.class));
		end();
	}
	
	public void showEndGameAlert() {
		Ref.makeAlert("End Game?", "Do you want to end the game?", 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						endGame();
					}
					
				}, "Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {}
					
				}, "Nevermind", this);
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