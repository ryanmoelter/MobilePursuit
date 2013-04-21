package com.zephyricstudios.catchme;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainPage extends Activity implements OnClickListener {
	
	RelativeLayout buttonMainSnitch, buttonMainSeeker, buttonMainConfused;
	TextView textMainSeeker, textMainSnitch, textMainConfused;
	Typeface light;
	
	String name;
	
	SharedPreferences sp;
	
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	
	Group group;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        buttonMainSnitch = (RelativeLayout)findViewById(R.id.button_main_snitch);
        buttonMainSnitch.setOnClickListener(this);
        buttonMainSeeker = (RelativeLayout)findViewById(R.id.button_main_seeker);
        buttonMainSeeker.setOnClickListener(this);
        buttonMainConfused = (RelativeLayout)findViewById(R.id.button_main_confused);
        buttonMainConfused.setOnClickListener(this);
        
        //seekerArray = new ArrayList<Seeker>();
        if(Ref.group != null) {
        	group = Ref.group;
        } else {
        	group = new Group();
        }
        group.setActAdapter(new ActivityAdapter(group) {
    		@Override
    		public void end() {
    			MainPage.this.unregisterReceiver(localTextReceiver);
    			Ref.group = MainPage.this.group;
    			// Do not end activity here
    		}
    	});
        
        textMainSeeker = (TextView)findViewById(R.id.text_main_seeker);
        textMainSnitch = (TextView)findViewById(R.id.text_main_snitch);
        textMainConfused = (TextView)findViewById(R.id.text_main_confused);
        
        light = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
    	textMainSeeker.setTypeface(light);
    	textMainSnitch.setTypeface(light);
    	textMainConfused.setTypeface(light);
    	
    	sp = getSharedPreferences(Ref.STORED_PREFERENCES_KEY, MODE_PRIVATE);
    	
    	if(sp.getString(Ref.USERNAME_KEY, Ref.SHARED_PREFS_DEFAULT).equals(Ref.SHARED_PREFS_DEFAULT)){
    		Ref.changeName(this, true);
    	}
    	
    	localTextReceiver = group.getBroadcastReceiver();
        filter = new IntentFilter();
        filter.addAction(Ref.ACTION);
        this.registerReceiver(this.localTextReceiver, filter);
    }
    
    @Override
    protected void onDestroy() {
    	this.unregisterReceiver(localTextReceiver);
    	super.onDestroy();
    }
    
    @Override
    protected void onRestart() {
    	this.group = Ref.group;
    	this.registerReceiver(localTextReceiver, filter);
    	super.onRestart();
    }
    
	public void onClick(View buttonChosen) {
		Intent i = null;
		
		switch (buttonChosen.getId()) {
		case R.id.button_main_seeker:
			i = new Intent(this, SeekerMainPage.class);
			break;
		case R.id.button_main_snitch:
			i = new Intent(this, SnitchMainPage.class);
			//i.putParcelableArrayListExtra(Ref.SEEKER_ARRAY_KEY, seekerArray);
			break;
		case R.id.button_main_confused:
			i = new Intent(this, ConfusedMenu.class);
			break;
		}
		
		if(i != null) {
			Ref.group = this.group;
			this.unregisterReceiver(localTextReceiver);
			this.startActivity(i);
		}
	}
}