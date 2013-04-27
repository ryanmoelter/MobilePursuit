package com.zephyricstudios.catchme;

import java.util.ArrayList;
import java.util.List;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
// Typeface
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.TextView;
// Contact Picker
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;  

public class SeekerMainPage extends Activity implements OnClickListener, Endable {

	Button contactsButton;
	RelativeLayout startButton;
	EditText box;
	static final int CONTACT_PICKER_RESULT = 1001;
	String num = "";
	String runnerNumber = "";
	SmsManager sm = SmsManager.getDefault();
	Drawable drawable;
	
	// Typeface only
	Typeface light;
	TextView textTitle, textOr, textStartButton;
	
	Group group;
	BroadcastReceiver localTextReceiver;
	IntentFilter filter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_main_page);
        this.getResources().getDrawable(R.drawable.ic_launcher);
        
        //creates the contact picker that accesses numbers on the phone
        contactsButton = (Button)findViewById(R.id.snitch_contact_picker);
        contactsButton.setOnClickListener(this);
        startButton = (RelativeLayout)findViewById(R.id.start_button);
        startButton.setOnClickListener(this);
        box = (EditText)findViewById(R.id.snitch_num);
        
        
        // Typeface Stuff
        light = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
        textTitle = (TextView)findViewById(R.id.text_seeker_title);
        textTitle.setTypeface(light);
        textOr = (TextView)findViewById(R.id.text_seeker_or);
        textOr.setTypeface(light);
        textStartButton = (TextView)findViewById(R.id.text_seeker_continue);
        textStartButton.setTypeface(light);
        contactsButton.setTypeface(light);
        box.setTypeface(light);
        
        group = Ref.group;
        group.setActAdapter(new ActivityAdapter());
        group.setRunning(this);
        group.setContext(this);
        
        localTextReceiver = group.getBroadcastReceiver();
        filter = new IntentFilter();
        filter.addAction(Ref.ACTION);
        this.registerReceiver(this.localTextReceiver, filter);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	SeekerMainPage.this.unregisterReceiver(localTextReceiver);
    }

	@Override
	public void end() {
		finish();
	}
   
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == RESULT_OK) {  
            switch (requestCode) {  
            case CONTACT_PICKER_RESULT:
                final EditText phoneInput = (EditText)findViewById(R.id.snitch_num);
                Cursor cursor = null;  
                String phoneNumber = "";
                List<String> allNumbers = new ArrayList<String>();
                int phoneIdx = 0;
                try {  
                    Uri result = data.getData();  
                    String id = result.getLastPathSegment();  
                    cursor = getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=?", new String[] { id }, null);  
                    phoneIdx = cursor.getColumnIndex(Phone.DATA);
                    if (cursor.moveToFirst()) {
                        while (cursor.isAfterLast() == false) {
                            phoneNumber = cursor.getString(phoneIdx);
                            allNumbers.add(phoneNumber);
                            cursor.moveToNext();
                        }
                    } else {
                        //no results actions
                    }  
                } catch (Exception e) {  
                   //error actions
                } finally {  
                    if (cursor != null) {  
                        cursor.close();
                    }
                    final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SeekerMainPage.this);
                    builder.setTitle("Choose a number");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            runnerNumber = items[item].toString();
                            //selectedNumber = selectedNumber.replace("-", "");
                            //num = selectedNumber;
                            phoneInput.setText(runnerNumber);
                        }
                    });
                    AlertDialog alert = builder.create();
                    if(allNumbers.size() > 1) {
                        alert.show();
                    } else {
                        runnerNumber = phoneNumber.toString();
                        //selectedNumber = selectedNumber.replace("-", "");
                        //num = selectedNumber;
                        phoneInput.setText(runnerNumber);
                    }

                    if (phoneNumber.length() == 0) {  
                        //no numbers found actions
                    }  
                }  
                break;  
            }  
        } else {
           //activity result error actions
        }  
    }

    public String convertToOnlyNumbers(String number) {
    	return number.replace("(", "")
					 .replace(")", "")
					 .replace(" ", "")
					 .replace("-", "")
					 .replace("+", "");
    }
    
    public boolean checkIfRealNumber(String number) {
    	int length = convertToOnlyNumbers(number).length();
    	
    	if(length == 7) {
    		Toast.makeText(this, "Please add an area code", Toast.LENGTH_SHORT).show();
    	}
    	else if(length >= 10 && length <= 13) {
    		return true;
    	} else {
    		Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
    	}
    	return false;
    }
    
	public void onClick(View chosen) {
		switch(chosen.getId()) {
		case R.id.snitch_contact_picker:
			Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
			startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
			break;
			
		case R.id.start_button:
			runnerNumber = box.getText().toString();
			
			if(checkIfRealNumber(runnerNumber)) {
				runnerNumber = convertToOnlyNumbers(runnerNumber);
				group.joinGroup(runnerNumber);
			}
			break;
		}
	}
}