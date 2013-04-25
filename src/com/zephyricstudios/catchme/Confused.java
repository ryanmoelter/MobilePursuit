package com.zephyricstudios.catchme;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

// Typeface
import android.graphics.Typeface;
import android.widget.TextView;

public class Confused extends Activity {

	Typeface thin, light;
	TextView title, objectiveTitle, startTitle, gameTitle, endTitle;
	
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	boolean navigated;
	Group group;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confused);
        
        // Typeface
        thin = Typeface.createFromAsset(getAssets(), "roboto_thin.ttf");
        title = (TextView)findViewById(R.id.text_confused_title);
        title.setTypeface(thin);
        
        light = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
        objectiveTitle = (TextView)findViewById(R.id.text_objective_title);
        objectiveTitle.setTypeface(light);
        startTitle = (TextView)findViewById(R.id.text_start_title);
        startTitle.setTypeface(light);
        gameTitle = (TextView)findViewById(R.id.text_game_title);
        gameTitle.setTypeface(light);
        endTitle = (TextView)findViewById(R.id.text_end_title);
        endTitle.setTypeface(light);
        
        group = Ref.group;
        group.setActAdapter(makeActivityAdapter(group));
        
        localTextReceiver = group.getBroadcastReceiver();
        filter = new IntentFilter();
        filter.addAction(Ref.ACTION);
        this.registerReceiver(this.localTextReceiver, filter);
        navigated = false;
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	this.unregisterReceiver(localTextReceiver);
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();
    	if(navigated) {
			group.setActAdapter(this.makeActivityAdapter(group));
    		this.registerReceiver(localTextReceiver, filter);
    		navigated = false;
    	}
    }
    
    public void onNavigate() {
		navigated = true;
		this.unregisterReceiver(localTextReceiver);
	}
	
	// Make the ActivityAdapter here so we can reconstruct it in onRestart(),
	// since it will have been replaced by the ones in the game screens.
    public ActivityAdapter makeActivityAdapter(Group group) {
		return new ActivityAdapter(group) {
			@Override
    		public void end() {
    			Ref.group = Confused.this.group;
    			Confused.this.onNavigate();
    			// Do not end activity here
    		}
		};
	}
}