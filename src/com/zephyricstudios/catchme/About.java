package com.zephyricstudios.catchme;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.widget.TextView;

public class About extends Activity implements Endable {
	
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
		group.setActAdapter(new ActivityAdapter());
		group.setRunning(this);
		
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
			group.setActAdapter(new ActivityAdapter());
    		this.registerReceiver(localTextReceiver, filter);
    		navigated = false;
    	}
	}
	
	@Override
	public void end() {
		navigated = true;
		this.unregisterReceiver(localTextReceiver);
	}
}
