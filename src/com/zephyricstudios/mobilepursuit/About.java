package com.zephyricstudios.mobilepursuit;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.Menu;
import android.widget.TextView;

public class About extends Activity {
	
	TextView zephyric, email, please, credits, mazen, mazenCredits, everyone, everyoneCredits;
	Typeface light, thin;

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
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_about, menu);
		return true;
	}

}
