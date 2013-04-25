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

public class ConfusedMenu extends Activity implements OnClickListener {
	
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
		group.setActAdapter(makeActivityAdapter(group));
		
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
    			Ref.group = ConfusedMenu.this.group;
    			ConfusedMenu.this.finish();
    		}
		};
	}
}
