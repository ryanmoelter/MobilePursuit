package com.zephyricstudios.catchme;



/*
 * This allows the Group class to work with the UI and maps and things
 */
public class ActivityAdapter {
	
//	private Group group;
//	private Context context;
	
	
	// Constructors
	public ActivityAdapter() {
		
	}
	
//	public ActivityAdapter(Group group) {
//		this.group = group;
//	}
	
//	public ActivityAdapter(Group group, Context context) {
//		this.group = group;
//		this.context = context;
//	}
	
	
	// Receiving
	public void receiveYoureOut() {}
	
	public void receiveGeopoint(String geoString) {}
	
	public void receiveGameStart(int interval) {}
	
	public void receiveGameOver() {}
	
	
	// For updating the UI due to a change of runner status
	public void updateUI() {}
	
	
	// Miscellaneous
//	public void end() {}
	
}
