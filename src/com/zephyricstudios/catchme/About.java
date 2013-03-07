package com.zephyricstudios.catchme;

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

}
