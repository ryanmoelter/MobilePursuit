package com.zephyricstudios.mobilepursuit;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.KeyEvent;
import android.view.Menu;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface;
import android.widget.ListView;
import com.zephyricstudios.mobilepursuit.SeekerAdapter;

public class SnitchMainPage extends Activity implements OnClickListener{
	
	RelativeLayout start;
	RelativeLayout settings;
	int timerInterval;
	
	// Typeface
	Typeface light;
	
	SmsManager sm = SmsManager.getDefault();
	
	//Seeker Object Stuff
	ListView seekerList;
	ArrayList<Seeker> seekerArray;
	SeekerAdapter adapter;
	
	TextView title, startText, textSnitchSettings, textSending;
	
	Boolean intervalSettingsVisible;
	
	RelativeLayout snitchSettingsButton, sendingLayout;
	LinearLayout intervalSettings;
	Button btnInterval15s, btnInterval30s, btnInterval45s, btnInterval60s;
	
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snitch_main_page);
        
        //Set up start and settings button
        start = (RelativeLayout)findViewById(R.id.snitch_start_button);
        start.setOnClickListener(this);
        settings = (RelativeLayout)findViewById(R.id.snitch_settings_button);
        settings.setOnClickListener(this);
        
        //Seeker Object Stuff
        seekerArray = new ArrayList<Seeker>();
        
        adapter = new SeekerAdapter(this, R.layout.list_item, seekerArray, this, light);
        
        seekerList = (ListView)findViewById(R.id.seeker_list);
        seekerList.setAdapter(adapter);
        
        // Typeface
        light = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
        title = (TextView)findViewById(R.id.text_snitch_title);
        title.setTypeface(light);
        startText = (TextView)findViewById(R.id.text_snitch_start);
        startText.setTypeface(light);
        textSending = (TextView)findViewById(R.id.text_sending);
        textSending.setTypeface(light);
        
        snitchSettingsButton = (RelativeLayout)findViewById(R.id.snitch_settings_button);
        snitchSettingsButton.setOnClickListener(this);
        
        intervalSettings = (LinearLayout)findViewById(R.id.interval_settings_layout);
        intervalSettings.setVisibility(View.GONE);
        intervalSettingsVisible = false;
        btnInterval15s = (Button)findViewById(R.id.button_15s);
        btnInterval30s = (Button)findViewById(R.id.button_30s);
        btnInterval45s = (Button)findViewById(R.id.button_45s);
        btnInterval60s = (Button)findViewById(R.id.button_60s);
        btnInterval15s.setOnClickListener(this);
        btnInterval30s.setOnClickListener(this);
        btnInterval45s.setOnClickListener(this);
        btnInterval60s.setOnClickListener(this);
        btnInterval15s.setTypeface(light);
        btnInterval30s.setTypeface(light);
        btnInterval45s.setTypeface(light);
        btnInterval60s.setTypeface(light);
        
        textSnitchSettings = (TextView)findViewById(R.id.text_snitch_settings);
        
        sendingLayout = (RelativeLayout)findViewById(R.id.loading_layout);
        
        timerInterval = 30;
        
        CmiycJavaRes.activityState = CmiycJavaRes.SNITCHMAIN;
        
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
				        	if(currentMessage.getDisplayMessageBody().contains("@!#seekerJoin;seekerName:")){ 
				        		Seeker.createSeeker(currentMessage.getDisplayOriginatingAddress(),
				        				currentMessage.getDisplayMessageBody().replace("@!#seekerJoin;seekerName:", ""),
				        				seekerArray, adapter);
				        		this.abortBroadcast();
				        		
				        		/*if(!seekerEntered1){
				        			seeker1.setVisibility(View.VISIBLE);
				        			name1 = currentMessage.getDisplayMessageBody().replace("@!#seekerJoin;seekerName:", "");
				        			seekerName1.setText(name1);
				       				seekerNumber1 = currentMessage.getDisplayOriginatingAddress();
				       				seekerEntered1 = true;
				       				this.abortBroadcast();
				       			} else if(!seekerEntered2){
				       				seeker2.setVisibility(View.VISIBLE);
				       				name2 = currentMessage.getDisplayMessageBody().replace("@!#seekerJoin;seekerName:", "");
				       				seekerName2.setText(name2);
				       				seekerNumber2 = currentMessage.getDisplayOriginatingAddress();
				       				seekerEntered2 = true;
				       				this.abortBroadcast();
				       			} else if(!seekerEntered3){
				       				seeker3.setVisibility(View.VISIBLE);
				       				name3 = currentMessage.getDisplayMessageBody().replace("@!#seekerJoin;seekerName:", "");
			        				seekerName3.setText(name3);
			        				seekerNumber3 = currentMessage.getDisplayOriginatingAddress();
			        				seekerEntered3 = true;
			        				this.abortBroadcast();
			        			} else if(!seekerEntered4){
			        				seeker4.setVisibility(View.VISIBLE);
			        				name4 = currentMessage.getDisplayMessageBody().replace("@!#seekerJoin;seekerName:", "");
			        				seekerName4.setText(name4);
			        				seekerNumber4 = currentMessage.getDisplayOriginatingAddress();
				        			seekerEntered4 = true;
				        			this.abortBroadcast();
				        		} else if(!seekerEntered5){
				        			seeker5.setVisibility(View.VISIBLE);
				        			name5 = currentMessage.getDisplayMessageBody().replace("@!#seekerJoin;seekerName:", "");
				        			seekerName5.setText(name5);
				        			seekerNumber5 = currentMessage.getDisplayOriginatingAddress();
				       				seekerEntered5 = true;
				       				this.abortBroadcast();
				       			} */
				       		} 
				        		//currentMessage.getDisplayOriginatingAddress();		// has sender's phone number
				        		//currentMessage.getDisplayMessageBody();				// has the actual message
				        }
				}
				
			}
        	
        };
        filter = new IntentFilter();
        filter.addAction(CmiycJavaRes.ACTION);
        this.registerReceiver(this.localTextReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_snitch_main_page, menu);
        return true;
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	this.unregisterReceiver(this.localTextReceiver);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	CmiycJavaRes.activityState = CmiycJavaRes.SNITCHMAIN;
    	this.registerReceiver(this.localTextReceiver, filter);
    }
    
    public void onClick(View v){
    	Intent i;
    	if(v.equals(findViewById(R.id.snitch_start_button))) {
    		if(!seekerArray.isEmpty()
    				/*seekerEntered1 || seekerEntered2 || seekerEntered3 || seekerEntered4 || seekerEntered5*/){
    			i = new Intent(this, SnitchMap.class);
    			String textContent = "@!#seekerConfirm;int:" + timerInterval;
    			
    			sendingLayout.setVisibility(View.VISIBLE);
    			
    			for(int index = 0; seekerArray.size() < index; index++) {
    				sm.sendTextMessage(seekerArray.get(index).getNumber(), null, textContent, null, null);
    			}
    			/*if(seekerEntered1){
    				sm.sendTextMessage(seekerNumber1, null, textContent, null, null);
    				seekerNumbers.add(seekerNumber1);
    				seekerNames.add(name1);
    			}
    			if(seekerEntered2){
    				sm.sendTextMessage(seekerNumber2, null, textContent, null, null);
    				seekerNumbers.add(seekerNumber2);
    				seekerNames.add(name2);
    			}
    			if(seekerEntered3){
    				sm.sendTextMessage(seekerNumber3, null, textContent, null, null);
    				seekerNumbers.add(seekerNumber3);
    				seekerNames.add(name3);
    			}
    			if(seekerEntered4){
    				sm.sendTextMessage(seekerNumber4, null, textContent, null, null);
    				seekerNumbers.add(seekerNumber4);
    				seekerNames.add(name4);
    			}
    			if(seekerEntered5){
    				sm.sendTextMessage(seekerNumber5, null, textContent, null, null);
    				seekerNumbers.add(seekerNumber5);
    				seekerNames.add(name5);
    			}*/
    			//CmiycJavaRes.activityState = CmiycJavaRes.SNITCHMAP;
    			//i.putStringArrayListExtra(CmiycJavaRes.SEEKER_NUMBERS_KEY, seekerNumbers);
    			//i.putStringArrayListExtra(CmiycJavaRes.SEEKER_NAMES_KEY, seekerNames);
    			i.putParcelableArrayListExtra(CmiycJavaRes.SEEKER_ARRAY_KEY, seekerArray);
    			i.putExtra(CmiycJavaRes.TIMER_INTERVAL_KEY, timerInterval);
    			this.startActivity(i);
    			finish();
    		} else{
    			Context context = getApplicationContext();
    			CharSequence text = "At least one seeker is required to continue";
    			int duration = Toast.LENGTH_SHORT;

    			Toast toast = Toast.makeText(context, text, duration);
    			toast.show();
    		}
    	} else if(v.getId() == R.id.item_delete) {
    		int position = (int)Integer.valueOf((String)v.getTag());
    		Seeker.deleteSeeker(position, seekerArray, adapter);
    	}
    	
    	/*else if(v.equals(deleteSeeker1)){
			seekerName1.setText("Waiting...");
			seekerEntered1 = false;
			seeker1.setVisibility(View.GONE);
    	} else if(v.equals(deleteSeeker2)){
    		seekerName2.setText("Waiting...");
			seekerEntered2 = false;
			seeker2.setVisibility(View.GONE);
    	} else if(v.equals(deleteSeeker3)){
    		seekerName3.setText("Waiting...");
			seekerEntered3 = false;
			seeker3.setVisibility(View.GONE);
    	} else if(v.equals(deleteSeeker4)){
    		seekerName4.setText("Waiting...");
			seekerEntered4 = false;
			seeker4.setVisibility(View.GONE);
    	} else if(v.equals(deleteSeeker1)){
    		seekerName5.setText("Waiting...");
    		seekerEntered5 = false;
			seeker5.setVisibility(View.GONE);
    	} */ 
    	
    	else if(v.equals(snitchSettingsButton)){
    		intervalSettings.setVisibility(View.VISIBLE);
    		intervalSettingsVisible = true;
    	} else if(v.equals(btnInterval15s)){
    		timerInterval = 15;
    		intervalSettings.setVisibility(View.GONE);
            intervalSettingsVisible = false;
            textSnitchSettings.setText(timerInterval + " Seconds");
    	} else if(v.equals(btnInterval30s)){
    		timerInterval = 30;
    		intervalSettings.setVisibility(View.GONE);
            intervalSettingsVisible = false;
            textSnitchSettings.setText(timerInterval + " Seconds");
    	} else if(v.equals(btnInterval45s)){
    		timerInterval = 45;
    		intervalSettings.setVisibility(View.GONE);
            intervalSettingsVisible = false;
            textSnitchSettings.setText(timerInterval + " Seconds");
    	} else if(v.equals(btnInterval60s)){
    		timerInterval = 60;
    		intervalSettings.setVisibility(View.GONE);
            intervalSettingsVisible = false;
            textSnitchSettings.setText(timerInterval + " Seconds");
    	}
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if (keyCode == KeyEvent.KEYCODE_BACK) {

	         if(intervalSettingsVisible){
	        	 intervalSettings.setVisibility(View.GONE);
	             intervalSettingsVisible = false;
	         } else{
	        	finish();
	         }

	         return true;
	     }
	     return super.onKeyDown(keyCode, event);
	 }
}
