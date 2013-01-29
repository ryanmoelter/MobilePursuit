package com.zephyricstudios.mobilepursuit;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Typeface;

public class ConfusedMenu extends Activity implements OnClickListener {
	
	TextView howToPlay, changeName, about;
	Typeface light;
	

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
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_confused_menu, menu);
		return true;
	}

	@Override
	public void onClick(View chosen) {
		Intent i = null;
		boolean launch = true;
		if(chosen == howToPlay) {
			i = new Intent(this, Confused.class); //old name for how to play
			launch = true;
		} else if(chosen == about) {
			i = new Intent(this, About.class);
			launch = true;
		} else if(chosen == changeName) {
			
			//need to put this into function probably
			
			SharedPreferences sp = getSharedPreferences(Ref.STORED_PREFERENCES_KEY, MODE_PRIVATE);
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

    		alert.setTitle("Enter User Name");
    		alert.setMessage("Please enter your new name.");

    		// Set an EditText view to get user input 
    		final EditText input = new EditText(this);
    		alert.setView(input);

    		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			String value = input.getText().toString();
    			if(value != "") {
    				SharedPreferences sp = getSharedPreferences(Ref.STORED_PREFERENCES_KEY, MODE_PRIVATE);
    	    		Editor spEditor = sp.edit();
    				spEditor.putString(Ref.USERNAME_KEY, value);
    		  		spEditor.commit();
    			}
    		  }
    		});

    		alert.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
    		  public void onClick(DialogInterface dialog, int whichButton) {
    		    dialog.cancel();
    		  }
    		});

    		alert.show();
    		//end what we should functionize
    		
			launch = false;
		}
		
		if(launch) {
			this.startActivity(i);
		}
		
	}

}
