package com.zephyricstudios.catchme;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.ProgressBar;
import android.widget.TextView;

public class SeekerWaitingPage extends Activity {
    
	TextView waitingForSnitch;
//	private ProgressBar progressBar;
	static TextView defaultTextView;
	static ProgressBar progressBar;
	static RelativeLayout seekerMapButton;
	
	String snitchNumber;
	
	Typeface light;
	
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	
	SmsManager sm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seeker_waiting);
		
		light = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
		waitingForSnitch = (TextView)findViewById(R.id.waiting_for_snitch);
		waitingForSnitch.setTypeface(light);
		
		snitchNumber = this.getIntent().getExtras().getString(Ref.SNITCH_NUMBER_KEY);
		
		sm = SmsManager.getDefault();
		
		//CmiycJavaRes.activityState = CmiycJavaRes.SEEKERWAITING;
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
				        //	if(CmiycJavaRes.activityState == CmiycJavaRes.SEEKERWAITING){
				        		if(currentMessage.getDisplayMessageBody().contains(Ref.GAME_START)){
				        			Intent i = new Intent(context, SeekerMap.class);
				        			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        			//if(currentMessage.getDisplayMessageBody().contains("int:")){
				        				String timerIntervalString =
				        						currentMessage.getDisplayMessageBody().replace(Ref.GAME_START, "");
				        				int timerIntervalInt = Integer.parseInt(timerIntervalString);
				        				i.putExtra(Ref.TIMER_INTERVAL_KEY, timerIntervalInt).putExtra(Ref.SNITCH_NUMBER_KEY, snitchNumber);
				        				
				        			//}
				        			
				        			this.abortBroadcast();
				        			context.startActivity(i);
				        			finish();
				        		} else if(currentMessage.getDisplayMessageBody().contains(Ref.YOURE_OUT)) {
				        			this.abortBroadcast();
				        			youreOut();
				        		}
				        //	}
				        	
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
	
	@Override
	public void onPause(){
		super.onPause();
		//this.unregisterReceiver(this.localTextReceiver);
	}
	
	@Override
    public void onResume(){
    	super.onResume();
    	this.registerReceiver(this.localTextReceiver, filter);
    	Ref.activityState = Ref.SEEKERWAITING;
    	
    }
	
	@Override
	public void onStop() {
		super.onStop();
		this.unregisterReceiver(this.localTextReceiver);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	        alertDialog.setTitle("Leave Game?");
	        alertDialog.setIcon(R.drawable.ic_launcher);

	        alertDialog.setMessage("Do you want to leave the game?");
	        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int which) {
	        		sendImOut();
	                finish();
	                return;
	            }
	        }); 
	        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int which) {
	        		dialog.cancel();
	        		return;
	        	}
	        }); 
	        alertDialog.show();

	        return true;
	    }
		return super.onKeyDown(keyCode, event);
	}
	
	public void youreOut() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("You're out");
        alertDialog.setIcon(R.drawable.ic_launcher);

        alertDialog.setMessage("The runner has kicked you out of the game.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) {
                finish();
                return;
            }
        });
        alertDialog.show();
	}
	
	public void sendImOut() {
		sm.sendTextMessage(snitchNumber, null, Ref.IM_OUT, null, null);
	}

}
