package com.zephyricstudios.catchme;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.graphics.Typeface;

public class ConfusedMenu extends Activity implements OnClickListener, Endable {
	
	TextView howToPlay, changeName, about;
	Typeface light;
	
	Group group;
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	boolean navigated;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confused_menu);
		
		light = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
		howToPlay = (TextView)findViewById(R.id.how_to_play);
		howToPlay.setTypeface(light);
		changeName = (TextView)findViewById(R.id.change_name);
		changeName.setTypeface(light);
		about = (TextView)findViewById(R.id.about);
		about.setTypeface(light);
		
		howToPlay.setOnClickListener(this);
		changeName.setOnClickListener(this);
		about.setOnClickListener(this);
		
		group = Ref.group;
		localTextReceiver = group.getBroadcastReceiver();
		group.setActAdapter(new ActivityAdapter());
		group.setRunning(this);
		
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
	public void onClick(View chosen) {
		switch(chosen.getId()) {
		case R.id.how_to_play:
			this.startActivity(new Intent(this, Confused.class)); //old name for how to play
			break;
		case R.id.about:
			this.startActivity(new Intent(this, About.class));
			break;
		case R.id.change_name:
			Ref.changeName(this, false);
			break;
		}
	}
	
	public void end() {
		navigated = true;
		this.unregisterReceiver(localTextReceiver);
	}
}
