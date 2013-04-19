package com.zephyricstudios.catchme;

import android.content.Context;

public class ActivityAdapter {
	
	private Group group;
	private Context context;
	
	public ActivityAdapter(Group group) {
		this.group = group;
	}
	
	public ActivityAdapter(Group group, Context context) {
		this.group = group;
		this.context = context;
	}
	
	public void receiveImIn(String name, String number) {
		group.receiveImIn(name, number);
	}
	
	public void receiveYoureIn() {}
	
	public void receiveYoureOut() {}
	
	public void receiveGeopoint(String Geopoint) {}
	
	public void receiveGameStart(int interval) {}
	
	public void receiveGameOver() {}
	
	public void end() {}
	
}
