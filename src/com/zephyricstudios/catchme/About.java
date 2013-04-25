package com.zephyricstudios.catchme;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.widget.TextView;

public class About extends Activity {
	
	TextView zephyric, email, please, credits, mazen, mazenCredits, everyone, everyoneCredits;
	Typeface light, thin;
	
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	boolean navigated;
	Group group;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		light = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
		thin = Typeface.createFromAsset(getAssets(), "roboto_thin.ttf");
		
		zephyric = (TextView)findViewById(R.id.zephyric_studios);
		zephyric.setTypeface(thin);
		email = (TextView)findViewById(R.id.email);
		email.setTypeface(light);
		credits = (TextView)findViewById(R.id.credits);
		credits.setTypeface(light);
		mazen = (TextView)findViewById(R.id.mazen);
		mazen.setTypeface(light);
		everyone = (TextView)findViewById(R.id.everyone);
		everyone.setTypeface(light);
		
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
    			Ref.group = About.this.group;
    			About.this.onNavigate();
    			// Do not end activity here
    		}
		};
	}
}
