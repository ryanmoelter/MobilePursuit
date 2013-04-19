package com.zephyricstudios.catchme;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class Group /*implements Parcelable*/ {
	private ArrayList<Seeker> people;
	private SeekerAdapter seekerAdapter;
	private boolean isSeekerAdapterNeeded, imRunner;
	private ActivityAdapter actAdapter;
	private BroadcastReceiver broadcastReceiver;
	
	
	// Constructors
	public Group() {
		people = new ArrayList<Seeker>();
		seekerAdapter = null;
		isSeekerAdapterNeeded = false;
		actAdapter = null;
		imRunner = true;
		broadcastReceiver = createBroadcastReceiver();
	}
	
	public Group(ArrayList<Seeker> people) {
		this.people = people;
		seekerAdapter = null;
		isSeekerAdapterNeeded = false;
		actAdapter = null;
		imRunner = !isThereARunner();
		broadcastReceiver = createBroadcastReceiver();
	}
	
	public Group(SeekerAdapter adapter) {
		people = new ArrayList<Seeker>();
		seekerAdapter = adapter;
		isSeekerAdapterNeeded = true;
		actAdapter = null;
		broadcastReceiver = createBroadcastReceiver();
	}
	
	public Group(ArrayList<Seeker> people, SeekerAdapter adapter) {
		this.people = people;
		this.seekerAdapter = adapter;
		isSeekerAdapterNeeded = true;
		imRunner = !isThereARunner();
		broadcastReceiver = createBroadcastReceiver();
	}
	
	public Group(ArrayList<Seeker> people, ActivityAdapter adapter) {
		this.people = people;
		seekerAdapter = null;
		isSeekerAdapterNeeded = false;
		actAdapter = adapter;
		imRunner = !isThereARunner();
	}
	
	public Group(ArrayList<Seeker> people, SeekerAdapter seekerAdapter, ActivityAdapter actAdapter) {
		this.people = people;
		this.seekerAdapter = seekerAdapter;
		isSeekerAdapterNeeded = true;
		this.actAdapter = actAdapter;
		imRunner = !isThereARunner();
	}
	
	
	// Queries
	public ArrayList<Seeker> getPeople() {
		return people;
	}
	
	public SeekerAdapter getSeekerAdapter() {
		return seekerAdapter;
	}
	
	public ActivityAdapter getActAdapter() {
		return actAdapter;
	}
	
	public BroadcastReceiver getBroadcastReceiver() {
		return broadcastReceiver;
	}
	
	
	// Setters and removers
	public void setSeekerAdapter(SeekerAdapter adapter) {
		this.seekerAdapter = adapter;
		isSeekerAdapterNeeded = true;
	}
	
	public void disableSeekerAdapter() {
		seekerAdapter = null;
		isSeekerAdapterNeeded = false;
	}
	
	public void setActAdapter(ActivityAdapter adapter) {
		this.actAdapter = adapter;
	}
	
	public void replacePeople(ArrayList<Seeker> people) {
		this.people = people;
	}
	
	
	// Miscellaneous
	private void notifyAdapter() {
		if(isSeekerAdapterNeeded) {
			seekerAdapter.notifyDataSetChanged();
		}
	}
	
	private static void sendText(final SmsManager sm, final String number, final String content) {
		new Thread() {
			public void run() {
				sm.sendTextMessage(number, null, content, null, null);
			}
		}.start();
	}
	
	private static void sendTexts(final SmsManager sm, final ArrayList<String> numbers, final String content) {
		new Thread() {
			public void run() {
				for(String number : numbers) {
					sm.sendTextMessage(number, null, content, null, null);
					try {
						wait(500);
					} catch (InterruptedException e) {
						// Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	
	// Runner management
	private boolean isThereARunner() {
		boolean result = false;
		for(Seeker seeker : people) {
			if(seeker.isRunner()) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	public void makeMeRunner() {
		imRunner = true;
	}
	
	public void makeRunner(int index) {
		people.get(index).makeRunner();
		notifyAdapter();
	}
	
	public void makeRunnerByNumber(String number) {
		for(Seeker person : people) {
			if(person.getNumber() == number) {
				person.makeRunner();
				break;
			}
		}
	}
	
	public void removeRunner(int index) {
		people.get(index).makeNotRunner();
	}
	
	public void removeRunnerByNumber(String number) {
		for(Seeker person : people) {
			if(person.getNumber() == number) {
				person.makeNotRunner();
			}
		}
	}
	
	public void clearRunners() {
		for(Seeker person : people) {
			person.makeNotRunner();
		}
	}
	
	
	// Private Group management
	private void createPerson(String name, String number) {
		people.add(new Seeker(number, name));
		notifyAdapter();
	}
	
	private void removePerson(int index) {
		people.remove(index);
		notifyAdapter();
	}
	
	private void removePersonByNumber(String number) {
		for(int i = 0; i < people.size(); i++) {
			if(people.get(i).getNumber().equals(number) ) {
				removePerson(i);
			}
		}
		return;
	}
	
	private String getNameByNum(String number) {
		for(Seeker person : people) {
			if(person.getNumber().equals(number) ) {
				return person.getName();
			}
		}
		return "Error";
	}
	
	
	// Public Group management -- Receive
	public void receiveImIn(String name, String number) {
		createPerson(name, number);
		actAdapter.receiveImIn(name, number);
	}
	
	public void receiveImOut(String number) {
		removePersonByNumber(number);
	}
	
	public void receiveYoureIn(String name, String number, final Context context) {
		if(imRunner) {
			
		} else {
			Ref.makeAlert("You're invited", name + " wants you to join their game",
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent i = new Intent(context, SnitchMainPage.class);
							context.startActivity(i);
							Group.this.getActAdapter().end();
							
						}
					}, "Okay", 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					}, "No, go away", context);
		}
		//Ref.joinGameAlert(name, number, context);
		// TODO make text change when you have a group with you
	}
	
	public void receiveYoureOut() {
		actAdapter.receiveYoureOut();
	}
	
	public void receiveHesIn(String name, String number) {
		createPerson(name, number);
	}
	
	public void receiveHesOut(String number) {
		removePersonByNumber(number);
	}
	
	public void receiveNewRunner(String number) {
		makeRunnerByNumber(number);
	}
	
	
	
	
	// Public Group management -- Send
	public void sendImOut(SmsManager sm) {
		ArrayList<String> numbers = new ArrayList<String>();
		
		for(Seeker person : people) {
			if(person.isRunner()) {
				numbers.add(person.getNumber());
			}
		}
		
		sendTexts(sm, numbers, Ref.CATCH_ME + Ref.IM_OUT);
	}
	
	
	// Public static Group management -- Send
	public static void sendImIn(Context context, SmsManager sm, String destinationNum) {
		SharedPreferences sp = context.getSharedPreferences(Ref.STORED_PREFERENCES_KEY, Context.MODE_PRIVATE);
		String name = sp.getString(Ref.USERNAME_KEY, "Someone");
		sendText(sm, destinationNum, Ref.CATCH_ME + Ref.IM_IN + name);
	}
	
	public static void joinGame(Context context, SmsManager sm, String destinationNum) {
		sendImIn(context, sm, destinationNum);
	}
	
	// BroadcastReceiver creation
	// Creates a BroadcastReceiver which ONLY parses information and then sends it to the
	//   appropriate function in the group class
	public BroadcastReceiver createBroadcastReceiver() {
		return new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle bundle = intent.getExtras();
				
				String name, number, message;

				if (bundle != null) {
					Object[] pdusObj = (Object[]) bundle.get("pdus");
				    SmsMessage[] messages = new SmsMessage[pdusObj.length];
				    
				    // getting SMS information from Pdu.
				    for (int i = 0; i < pdusObj.length; i++) {
				        messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				    }

				    for (SmsMessage currentMessage : messages) {
				    	if(currentMessage.getDisplayMessageBody().contains(Ref.CATCH_ME)) {
				    		number = currentMessage.getDisplayOriginatingAddress();
				    		message = currentMessage.getDisplayMessageBody()
				    				   .replace(Ref.CATCH_ME, "");
				    		Group group = Group.this;
				    		
				    		if(message.contains(Ref.IM_IN)){
				    			name = message.replace(Ref.IM_IN, "");
					    		group.receiveImIn(name, number);
					    		
					    	} else if(message.contains(Ref.IM_OUT)) {
					    		group.receiveImOut(number);
					    		
					    	} else if(message.contains(Ref.YOURE_IN)) {
					    		name = message.replace(Ref.YOURE_IN, "");
					    		Group.receiveYoureIn(name, number, context);
					    		
					    	} else if(message.contains(Ref.YOURE_OUT)) {
					    		group.getActAdapter().receiveYoureOut();
					    		
					    	} else if(message.contains(Ref.HES_IN)) {
					    		String[] array = message.replace(Ref.HES_IN, "").split(" ");
					    		name = array[0];
					    		number = array[1];
					    		group.receiveHesIn(name, number);
					    		
					    	} else if(message.contains(Ref.HES_OUT)) {
					    		group.receiveHesOut(number);
					    		
					    	} else if(message.contains(Ref.GEOPOINT)) {
					    		group.getActAdapter().receiveGeopoint(
					    				message.replace(Ref.GEOPOINT, ""));
					    		
					    	} else if(message.contains(Ref.GAME_START)) {
					    		group.getActAdapter().receiveGameStart(
					    				Integer.valueOf(message.replace(Ref.GAME_START, "")));
					    		
					    	} else if(message.contains(Ref.GAME_OVER)) {
					    		group.getActAdapter().receiveGameOver();
					    		
					    	} else {
					    		Toast.makeText(context,
					    				"Please update this app so you can play with your friends",
					    				Toast.LENGTH_LONG).show();
					    	}
				    		
				    		this.abortBroadcast();
				    	}
				        //currentMessage.getDisplayOriginatingAddress();		// has sender's phone number
				        //currentMessage.getDisplayMessageBody();				// has the actual message
				    }
				}
			}
		};
	}
	
	
	
	//
	//I'm just going to use a static group in Ref.java instead of working on this
	//
	
	/* everything below here is for implementing Parcelable */

    /*// 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
    	Bundle bundle = new Bundle();
    	bundle.putParcelableArrayList("people", people);
    	out.writeTypedList(people);
    	
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Group(Parcel in) {
    	
    } */
}
