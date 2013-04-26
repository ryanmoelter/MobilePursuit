package com.zephyricstudios.catchme;


// This will let us put all the settings, data, and methods of the actual game
// into one object, and add to and remove them as we go along in an easy way.
// Also, it will be an object stored in Ref, so it will stay the same through screens.
public class Game {
	
	private int timerInterval;
	
	public Game() {
		timerInterval = 30;
	}
	
	public int getInterval() {
		return timerInterval;
	}
	
	public void setInterval(int interval) {
		timerInterval = interval;
	}
}
