package com.zephyricstudios.catchme;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainPage extends Activity implements OnClickListener {
	
	RelativeLayout buttonMainSnitch, buttonMainSeeker, buttonMainConfused;
	TextView textMainIAm, textMainSeeker, textMainSnitch, textMainConfused;
	Typeface light;
	String name;
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	//boolean bool;
	ArrayList<Seeker> seekerArray;
	
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
        
        seekerArray = new ArrayList<Seeker>();
        
        textMainIAm = (TextView)findViewById(R.id.text_main_i_am);
        textMainSeeker = (TextView)findViewById(R.id.text_main_seeker);
        textMainSnitch = (TextView)findViewById(R.id.text_main_snitch);
        textMainConfused = (TextView)findViewById(R.id.text_main_confused);
        
        light = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
    	textMainIAm.setTypeface(light);
    	textMainSeeker.setTypeface(light);
    	textMainSnitch.setTypeface(light);
    	textMainConfused.setTypeface(light);
    	
    	SharedPreferences sp = getSharedPreferences(Ref.STORED_PREFERENCES_KEY, MODE_PRIVATE);
    	//Editor spEditor = sp.edit();  //use these two lines anywhere I want to use/edit shared prefs
    	
    	if(sp.getString(Ref.USERNAME_KEY, Ref.SHARED_PREFS_DEFAULT).equals(Ref.SHARED_PREFS_DEFAULT)){
    		Ref.changeName(this, true);
    	}
    	
    	localTextReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
				    Object[] pdusObj = (Object[]) bundle.get("pdus");
				    SmsMessage[] messages = new SmsMessage[pdusObj.length];
				    
				    // getting SMS information from Pdu.
				    for (int i = 0; i < pdusObj.length; i++) {
				            messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				    }

				    for (SmsMessage currentMessage : messages) {
				    	if(currentMessage.getDisplayMessageBody().contains(Ref.IM_IN)){ 
				    		ArrayList<Seeker> seekerList = new ArrayList<Seeker>();
				    		Seeker.createSeeker(currentMessage.getDisplayOriginatingAddress(),
				    							currentMessage.getDisplayMessageBody().replace(Ref.IM_IN, ""),
				    							seekerList);
				    		this.abortBroadcast();
				    		
				    		createGame(seekerList);
				    		//TODO The problem's in createGame
				    	}
				    	//currentMessage.getDisplayOriginatingAddress();		// has sender's phone number
				    	//currentMessage.getDisplayMessageBody();				// has the actual message
				    }
				}
			}
        };
        filter = new IntentFilter();
        filter.addAction(Ref.ACTION);
        this.registerReceiver(this.localTextReceiver, filter);
    	
    }
    
    public void createGame(ArrayList<Seeker> seekerList) {
    	
    	//boolean bool = false;
    	seekerArray = seekerList;
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Game Invite");
		alert.setMessage(seekerArray.get(0).getName() + " wants you to be the runner.");

		

		alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
				startSnitch();
			}
		});
		
		alert.setNegativeButton("No, go away", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		
		alert.show();
		

		/*if(bool) {
			Intent i = new Intent(this, SnitchMainPage.class);
			i.putParcelableArrayListExtra(Ref.SEEKER_ARRAY_KEY, seekerArray);
			this.startActivity(i);
		}*/
    }
    
    public void startSnitch() {
    	Intent i = new Intent(this, SnitchMainPage.class);
		i.putParcelableArrayListExtra(Ref.SEEKER_ARRAY_KEY, seekerArray);
		this.startActivity(i);
    }
    
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_page, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.menu_change_name:
	    	Ref.changeName(this, false);
	        return true;
	    // TODO: For testing purposes - DELETE
	    case R.id.menu_seeker_map:
	    	Intent i = new Intent(this, SeekerMap.class);
	    	i.putExtra(Ref.TIMER_INTERVAL_KEY, 30).putExtra(Ref.SNITCH_NUMBER_KEY, "8587749493");
	    	this.startActivity(i);
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	} */
    
    @Override
    public void onPause() {
    	super.onPause();
    	this.unregisterReceiver(this.localTextReceiver);
    }
    
    public void onStop() {
    	super.onStop();
    	//this.unregisterReceiver(this.localTextReceiver);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	this.registerReceiver(this.localTextReceiver, filter);
    }
    
	public void onClick(View buttonChosen) {
		Intent i = null;
		
		switch (buttonChosen.getId()) {
		case R.id.button_main_seeker:
			i = new Intent(this, SeekerMainPage.class);
			break;
		case R.id.button_main_snitch:
			i = new Intent(this, SnitchMainPage.class);
			i.putParcelableArrayListExtra(Ref.SEEKER_ARRAY_KEY, seekerArray);
			break;
		case R.id.button_main_confused:
			i = new Intent(this, ConfusedMenu.class);
			break;
		}
		
		if(i != null) {
			this.startActivity(i);
		}
	}
}