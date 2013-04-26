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


public class MainPage extends Activity implements OnClickListener, Endable {
	
	RelativeLayout buttonMainSnitch, buttonMainSeeker, buttonMainConfused;
	TextView textMainSeeker, textMainSnitch, textMainConfused;
	Typeface light;
	
	String name;
	
	SharedPreferences sp;
	
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	boolean navigated;
	
	Group group;
	Game game;
	
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
        	Ref.group = group;
        }
        group.setActAdapter(new ActivityAdapter());
        group.setRunning(this);
        group.setInGame(false);
        
        if(Ref.game != null) {
        	game = Ref.game;
        } else {
        	game = new Game();
        	Ref.game = game;
        }
        
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
        navigated = false;
    }
    
    @Override
    protected void onDestroy() {
    	this.unregisterReceiver(localTextReceiver);
    	super.onDestroy();
    }

	@Override
	public void end() {
		navigated = true;
		this.unregisterReceiver(localTextReceiver);
		// Do not end activity here
	}
	
	public void onReturn() {
		this.registerReceiver(localTextReceiver, filter);
		navigated = false;
		group.setInGame(false);
	}
    
    @Override
    protected void onRestart() {
//    	this.group = Ref.group;
    	if(navigated) {
    		group.setActAdapter(new ActivityAdapter());  // It will have been replaced
    		onReturn();
    	}
    	super.onRestart();
    }
    
	public void onClick(View buttonChosen) {
		switch (buttonChosen.getId()) {
		case R.id.button_main_seeker:
			this.startActivity(new Intent(this, SeekerMainPage.class));
			this.end();
			break;
		case R.id.button_main_snitch:
			this.startActivity(new Intent(this, SnitchMainPage.class));
			this.end();
			break;
		case R.id.button_main_confused:
			this.startActivity(new Intent(this, ConfusedMenu.class));
			this.end();
			break;
		}
	}
}