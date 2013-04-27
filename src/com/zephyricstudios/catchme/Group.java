package com.zephyricstudios.catchme;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class Group /*implements Parcelable*/ {
	private ArrayList<Seeker> people;
	private SeekerAdapter seekerAdapter;
	private boolean isSeekerAdapterNeeded = false, imRunner = true, inGame = false, joinedSomeone = false;
	private ActivityAdapter actAdapter;
	private BroadcastReceiver broadcastReceiver = createBroadcastReceiver();
	private static SmsManager sm = SmsManager.getDefault();
	private Endable running;
	private Context context;  // TODO make this a thing. Otherwise, you can't make alerts.
//	private String myName;
	
	/* TODO
	 *  This shouldn't be here probably, but we need to figure out a way to chain multiple
	 *  text message sendings. I feel like sleeping the main thread is a bad idea, and I just
	 *  feel like there's a better way to do things. Maybe make an array of runnables? Maybe
	 *  have all of the sending methods return a runnable, and just call a sendTexts method
	 *  that strings them all together? That actually could work... Or maybe there's a better
	 *  way than using runnables? Maybe have them return an array of numbers and contents? Or
	 *  have them return an array of textMessage objects?
	 *  Also, if/when this does happen, don't forget to put 500ms of delay between the messages.
	 */
	
	
	/*
	 * Constructors
	 *    These are the constructors. Right now there are a bunch of unused ones that I'm
	 *    keeping around in case we need them later, but I'll delete them if they never get
	 *    used in the game.
	 */
	public Group() {
		people = new ArrayList<Seeker>();
		imRunner = true;
	}
	
	public Group(Seeker person) {
		this.people = new ArrayList<Seeker>();
		this.people.add(person);
	}
	
	public Group(ArrayList<Seeker> people) {
		this.people = people;
		imRunner = !isThereARunner();
	}
	
	public Group(SeekerAdapter adapter) {
		people = new ArrayList<Seeker>();
		seekerAdapter = adapter;
		isSeekerAdapterNeeded = true;
	}
	
	public Group(ArrayList<Seeker> people, SeekerAdapter adapter) {
		this.people = people;
		this.seekerAdapter = adapter;
		isSeekerAdapterNeeded = true;
		imRunner = !isThereARunner();
	}
	
	public Group(ArrayList<Seeker> people, ActivityAdapter adapter) {
		this.people = people;
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
	
	
	/*
	 * Queries
	 *    These just return a value.
	 */
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
	
	public boolean imRunner() {
		return imRunner;
	}
	
	
	/*
	 * Commands
	 *    These are used to change the values of variables. These should also be used even in
	 *    methods in this class, as they sometimes change related values.
	 */
	public void clear() {
		people.clear();
		seekerAdapter = null;
		isSeekerAdapterNeeded = false;
		imRunner = true;
		inGame = false;
		actAdapter = null;
	}
	
	public void clearPeople() {
		people.clear();
		notifyAdapter();
	}
	
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
	
	public void setRunning(Endable endable) {
		running = endable;
	}
	
	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	
	/*
	 * Runner management
	 *    Really, this is a sub-category of Commands. It is all the commands related to runners
	 *    and managing who is the runner that don't involve texting.
	 */
	private boolean isThereARunner() {
		boolean result = false;
		for(Seeker person : people) {
			if(person.isRunner()) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	public void makeMeRunner() {
		imRunner = true;
	}
	
	public void makeMeNotRunner() {
		imRunner = false;
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
	
	private Seeker findRunner() {
		for(Seeker person : people) {
			if(person.isRunner()) {
				return person;
			}
		}
		return null; // This should never happen, since this is only called when
		             // you're a seeker
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
	
	
	/*
	 * Private Group Management
	 *    Another sub-category of commands. Contains all of the commands that pertain to the
	 *    non-runner part of managing a group.
	 */
	private void createPerson(String name, String number) {
		if(!personExists(number)) {
			people.add(new Seeker(name, number));
			notifyAdapter();
		}
	}
	
	public void removePerson(int index) {
		people.remove(index);
		notifyAdapter();
	}
	
	private void removePersonByNumber(String number) {
		for(int i = 0; i < people.size(); i++) {
			if(people.get(i).getNumber().equals(number)) {
				removePerson(i);
			}
		}
		return;
	}
	
	private boolean personExists(String number) {
		boolean exists = false;
		for(Seeker person : people) {
			if(person.getNumber().equals(number)) {
				exists = true;
				break;
			}
		}
		return exists;
	}
	
	private String findNameByNumber(String number) {
		for(Seeker person : people) {
			if(person.getNumber().equals(number)) {
				return person.getName();
			}
		}
		return "Someone";
	}
	
	
	/*
	 * Miscellaneous
	 *    I couldn't think of a category to put these in. Feel free to find them a home.
	 */
	private void notifyAdapter() {  // This lets the ListView know that the data has changed
		if(isSeekerAdapterNeeded) {  // ...But only if there is a list view to notify
			seekerAdapter.notifyDataSetChanged();
		}
	}
	
	public void checkGameOver() {
		if(people.isEmpty()) {
			Ref.makeAlert("Game Over", "Your friends have all left the game.", 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							running.end();
						}
						
					}, "Okay", context);
		}
	}
	
	
	/*
	 * Receiving texts
	 *    The backbone of this whole app. contains ONLY methods called from the BroadcastReceiver
	 *    when it receives the specific messages.
	 */
	public void receiveImIn(final String name, final String number) {
		if(inGame) {
			if(imRunner) {  // Someone joined. Add them to the game
				Toast.makeText(context, name + " joined the group", Toast.LENGTH_LONG).show();
				sendImIn(number);
				sendImRunner(number);
				if(!people.isEmpty()) {
					sendHesIn(name, number); // Might as well have this running while the other one is
					sendTheyreIn(number);
					
					// Sleep so that the texts will send before creating a new person,
					// which avoids the new guy being included in them
					// TODO make a processing thing appear, so people aren't confused
					try {
						Thread.sleep(500 * people.size());  // Can we even do this on the main thread?
					} catch (InterruptedException e) {
						// Auto-generated catch block
						e.printStackTrace();
					}
				}
				createPerson(name, number);
				
			} else {  // Someone joined, but you're not the runner. Let the runner know
				      // they want in
				sendRunnerHesIn(name, number);
			}
			
		} else {
			Ref.makeAlert(name + " wants to play", name + " wants you to be the runner.",
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {  // Positive
//							createPerson(name, number);
							// ^ This isn't necessary, since the runner will return it with an I'm in
							sendImIn(number);
							sendImRunner(number);
//							makeMeRunner();   not necessary
							context.startActivity(new Intent(context, SnitchMainPage.class));
							Group.this.running.end();
						}
						
					}, "Okay", new DialogInterface.OnClickListener() { 
						
						@Override
						public void onClick(DialogInterface dialog, int which) {  // Negative
							Group.sendYoureOut(number);  // End their runner-less game
						}
						
					}, "No, go away", context);
		}
	}
	
	public void receiveImOut(String number) {
		// If you're in game and the runner, take them out of the list and let everyone know
		if(inGame && imRunner && personExists(number)) {
			Toast.makeText(context, findNameByNumber(number) + " has left the group",
						   Toast.LENGTH_LONG).show();
			removePersonByNumber(number);
			sendHesOut(number);
		} else {
			// Do nothing, since the seeker will handle everything
			// Also, if you're not in the game, neither are they anymore
		}
	}
	
	public void receiveYoureIn(String name, final String number) {
		// Make an alert. The text will differ slightly depending on whether you're
		// already in a group or not.
		if(inGame) {  // You're in a game
			Ref.makeAlert("You're Invited", 
					name + " wants you in their group. Accepting their invitation will "
						 + "abandon your current group.",
					new DialogInterface.OnClickListener() {  // Positive
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Leave current group, and then join the other
							joinGroup(number); // this has leaveGame() in it
						}
						
					}, "Okay, I'm in",
					new DialogInterface.OnClickListener() {  // Negative
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing. No hard feelings, mate
						}
						
					}, "No way, Jose", context);
			
		} else {  // You're not in a game
			Ref.makeAlert("You're Invited", 
					name + " wants you in their group.",
					new DialogInterface.OnClickListener() {  // Positive
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Join and stuff
							joinGroup(number);
						}
						
					}, "Okay, I'm in",
					new DialogInterface.OnClickListener() {  // Negative
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing. No hard feelings, mate
						}
						
					}, "No way, Jose", context);
		}
		
		// Old
		//actAdapter.receiveYoureIn(name, number, context);
	}
	
	public void receiveYoureOut() {
		// TODO Make this
		actAdapter.receiveYoureOut();
	}
	
	public void receiveHesIn(String name, String number) {
		if(inGame) {
			if(imRunner) {  // Someone is redirected by a seeker
				createPerson(name, number);
				sendImIn(number);
				sendImRunner(number);
			} else {  // Someone joined the game through the runner
				createPerson(name, number);
				Toast.makeText(context, name + " has joined the group", Toast.LENGTH_LONG).show();
			}
		} else {  // Someone thinks you're in their game, but you're not
			sendImOut(number);  // Let them know you're no longer in the game
		}
	}
	
	public void receiveHesOut(String number) {
		if(inGame) {
			if(imRunner) {  // An error occured
				
			} else {  // Someone left
				Toast.makeText(context, findNameByNumber(number) + " has left the group",
							   Toast.LENGTH_LONG).show();
				removePersonByNumber(number);
			}
		} else {  // Someone thinks you're in their game, but you're not
			sendImOut(number);  // Let them know you're not in their game
		}
	}
	
	public void receiveImRunner(String number) {
		if(inGame) {
			if(imRunner) {  // This is a glitch
				// Do nothing
			} else {  // There's a new runner. Make them the runner (in data)
				clearRunners();
				makeRunnerByNumber(number);
				Toast.makeText(context, findNameByNumber(number) + " is now the runner",
							   Toast.LENGTH_LONG).show();
			}
		} else {  // Someone thinks you're in their game, but you're not
			sendImOut(number);  // Let them know you're not in their game
		}
	}
	
	public void receiveYoureRunner(String number) {
		if(inGame) {
			if(imRunner) {  // Glitch
				// Do nothing
			} else {  // You're the new runner! Let everyone know, change UI
				clearRunners();
				makeMeRunner();
				actAdapter.updateUI();
				sendImRunner();
				Toast.makeText(context, "You are now the runner", Toast.LENGTH_LONG).show();
			}
		} else {  // Someone thinks you're in their game, but you're not
			sendYoureRunner(number);  // You can't be their runner
			sendImOut(number);  // Let them know you're not in their game
		}
	}
	
	public void receiveGameStart(int interval) {
		actAdapter.receiveGameStart(interval);
	}
	
	public void receiveGeopoint(String geoString) {
		actAdapter.receiveGeopoint(geoString);
	}
	
	public void receiveGameOver() {
		actAdapter.receiveGameOver();
	}
	
	
	/*
	 * Methods of sending texts
	 *    Working title haha. This contains wrappers for sending text messages that perform
	 *    other things than just sending the message, like changing values or sending multiple
	 *    different texts to people.
	 */
	public void joinGroup(String number) {
		if(inGame) {  // You're already in a game
			leaveGroup();
			makeMeNotRunner();
			sendImIn(number);
			actAdapter.updateUI();
		} else {  // You're not in a game
			makeMeNotRunner();
			sendImIn(number);
			context.startActivity(new Intent(context, SnitchMainPage.class));
			running.end();
		}
		joinedSomeone = true;
	}
	
	public void leaveGroup() {
		sendImOut();
		try {
			Thread.sleep(500 * people.size());
		} catch (InterruptedException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		this.clear();
	}
	
	
	/*
	 * Sending texts
	 *    These are just easy ways to keep texting consistent. Call on these to send your
	 *    text messages.
	 */
	public void sendImIn(String destinationNum) {
		SharedPreferences sp = context.getSharedPreferences(Ref.STORED_PREFERENCES_KEY,
															Context.MODE_PRIVATE);
		String name = sp.getString(Ref.USERNAME_KEY, "Someone");
		
		sendText(destinationNum, Ref.IM_IN + name);
		
		// Add this if we want to merge groups
		/*if(imRunner && !people.isEmpty()) {
			sendTheyreIn(destinationNum);
		}*/
	}
	
	public void sendImOut() {
		if(inGame) {
			if(imRunner) {
				sendYoureRunner(people.get(0).getNumber()); // Make sure there's a runner
															// left in the game
				sendImOut(people.get(0).getNumber());
			} else {
				sendImOut(findRunner().getNumber());
			}
		}
	}
	
	public static void sendImOut(String number) {
		sendText(number, Ref.IM_OUT);
	}
	
	private void sendHesOut(String number) {
		sendEveryoneTexts(Ref.HES_OUT + number);
	}
	
	private void sendTheyreIn(String number) {
		ArrayList<String> contents = new ArrayList<String>();
		
		for(Seeker person : people) {
			contents.add(Ref.HES_IN + person.getName() + " " + person.getNumber());
		}
		
		sendTexts(number, contents);
	}
	
	private void sendHesIn(String name, String number) {
		sendEveryoneTexts(Ref.HES_IN + name + " " + number);
	}
	
	private void sendRunnerHesIn(String name, String number) {
		sendText(Ref.HES_IN + name + " " + number, findRunner().getNumber());
	}
	
	public void sendYoureOut(int index) {
		sendText(people.get(index).getNumber(), Ref.YOURE_OUT);
	}
	
	public static void sendYoureOut(String number) {
		sendText(number, Ref.YOURE_OUT);
	}
	
	private void sendImRunner() {
		sendEveryoneTexts(Ref.IM_RUNNER);
	}
	
	private static void sendImRunner(String number) {
		sendText(number, Ref.IM_RUNNER);
	}
	
	public static void sendYoureRunner(String number) {
		sendText(number, Ref.YOURE_RUNNER);
	}
	
	public void sendYoureRunner(int index) {
		sendText(people.get(index).getNumber(), Ref.YOURE_RUNNER);
	}
	
	public void sendGameStart(int interval) {
		sendEveryoneTexts(Ref.GAME_START + interval);
	}
	
	public void sendGameOver() {
		sendEveryoneTexts(Ref.GAME_OVER);
	}
	
	public void sendGeopoint(String geopointString) {
		sendEveryoneTexts(Ref.GEOPOINT + geopointString);
	}
	
	
	/*
	 * Texting
	 *    The methods that actually send the texts. Please don't mess with these, but feel
	 *    free to add a few more if necessary.
	 */
	private static void sendText(final String number, final String content) {
		new Thread() {
			public void run() {
				sm.sendTextMessage(number, null, Ref.CATCH_ME + content, null, null);
			}
		}.start();
	}
	
	private static void sendTexts(final String number, final ArrayList<String> contents) {
		new Thread() {
			public void run() {
				for(int i = 0; i < contents.size(); i++) {
					sm.sendTextMessage(number, null, Ref.CATCH_ME + contents.get(i), null, null);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	private void sendEveryoneTexts(final String content) {
		new Thread() {
			public void run() {
				for(Seeker person : people) {
					sm.sendTextMessage(person.getNumber(), null, Ref.CATCH_ME + content, null, null);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	// We can probably delete these
//	private static void sendTexts(final ArrayList<String> numbers, final String content) {
//		new Thread() {
//			public void run() {
//				for(String number : numbers) {
//					sm.sendTextMessage(number, null, Ref.CATCH_ME + content, null, null);
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						// Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}.start();
//	}
	
//	private static void sendTexts(final ArrayList<String> numbers, final ArrayList<String> contents) {
//		new Thread() {
//			public void run() {
//				for(int i = 0; i < numbers.size(); i++) {
//					sm.sendTextMessage(numbers.get(i), null, Ref.CATCH_ME + contents.get(i), null, null);
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						// Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}.start();
//	}
	
	
	/*
	 * BroadcastReceiver creation
	 *    Creates a BroadcastReceiver which ONLY parses information and then sends it to the
	 *    appropriate receiving function.
	 */
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
					    		group.receiveYoureIn(name, number);
					    		
					    	} else if(message.contains(Ref.YOURE_OUT)) {
					    		group.receiveYoureOut();
					    		
					    	} else if(message.contains(Ref.HES_IN)) {
					    		String[] array = message.replace(Ref.HES_IN, "").split(" ");
					    		name = array[0];
					    		number = array[1];
					    		group.receiveHesIn(name, number);
					    		
					    	} else if(message.contains(Ref.HES_OUT)) {
					    		group.receiveHesOut(number);
					    		
					    	} else if(message.contains(Ref.IM_RUNNER)) {
					    		group.receiveImRunner(number);
					    		
					    	} else if(message.contains(Ref.YOURE_RUNNER)) {
					    		group.receiveYoureRunner(number);
					    		
					    	} else if(message.contains(Ref.GEOPOINT)) {
					    		group.receiveGeopoint(message.replace(Ref.GEOPOINT, ""));
					    		
					    	} else if(message.contains(Ref.GAME_START)) {
					    		group.receiveGameStart(
					    				Integer.valueOf(message.replace(Ref.GAME_START, "")));
					    		
					    	} else if(message.contains(Ref.GAME_OVER)) {
					    		group.receiveGameOver();
					    		
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
	
	
	
	/*
	 * This is how we would pass this thing between screens using an intent.
	 * 
	 * ...I'm just going to use a static group in Ref.java instead of working on this for now.
	 */
	
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

    // this is used to regenerate your object. All Parcelables must have a CREATOR
    // that implements these two methods
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
