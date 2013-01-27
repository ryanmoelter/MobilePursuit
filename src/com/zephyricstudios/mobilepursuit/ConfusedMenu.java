package com.zephyricstudios.mobilepursuit;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ConfusedMenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confused_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_confused_menu, menu);
		return true;
	}

}
