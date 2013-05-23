package com.zephyricstudios.mobilepursuit;

import java.util.ArrayList;
import java.util.List;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
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

public class GlobalReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
	}
	
}