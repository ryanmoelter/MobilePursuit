package com.zephyricstudios.catchme;

import android.os.Parcel;
import android.os.Parcelable;

public class Seeker implements Parcelable {
	
	private String mName, mNumber;
	private boolean isRunner;
	
	public Seeker(String name, String number) {
		mName = name;
		mNumber = number;
		isRunner = false;
	}
	
	// Queries
	public String getName() {
		return mName;
	}
	
	public String getNumber() {
		return mNumber;
	}
	
	public boolean isRunner() {
		return isRunner;
	}
	
	// Setters
	public void makeRunner() {
		isRunner = true;
	}
	
	public void makeNotRunner() {
		isRunner = false;
	}

	
	/*public static void createSeeker(String number, String name, ArrayList<Seeker> array, SeekerAdapter adapter) {
		array.add(new Seeker(number, name));
		adapter.notifyDataSetChanged();
	}
	
	public static void createSeeker(String number, String name, ArrayList<Seeker> array) {
		array.add(new Seeker(number, name));
	}
	
	public static void deleteSeeker(int index, ArrayList<Seeker> array, SeekerAdapter adapter) {
		array.remove(index);
		adapter.notifyDataSetChanged();
	}
	
	public static void deleteSeekerByNum(String number, ArrayList<Seeker> array) {
		for(int i = 0; i < array.size(); i++) {
			if(array.get(i).getNumber().equals(number) ) {
				array.remove(i);
			}
		}
		return;
	}
	
	public static String getSeekerNameByNum(String number, ArrayList<Seeker> array) {
		for(int i = 0; i < array.size(); i++) {
			if(array.get(i).getNumber().equals(number) ) {
				return array.get(i).getName();
			}
		}
		return "Error";
	}*/
	
	/* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeString(mNumber);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Seeker> CREATOR = new Parcelable.Creator<Seeker>() {
        public Seeker createFromParcel(Parcel in) {
            return new Seeker(in);
        }

        public Seeker[] newArray(int size) {
            return new Seeker[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Seeker(Parcel in) {
        mName = in.readString();
        mNumber = in.readString();
    }

}
