/**
 * BootUpReceiver.java
 * 
 * When the phone reboots or boots, this code is executed to start the DataGather
 * 
 * @author Gesuri Ramirez
 * @date June 2012
 */

package org.cleos.android.ntl.datagather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootUpReceiver extends BroadcastReceiver {

	public static final String TAG = "BootUpReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d(TAG, "Inside : BootUpReceiver: onReceive");

		Intent i = new Intent(context, DataGather_Service.class);

		Log.d(TAG, "Before creating Intent");

		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Log.d(TAG, "Before starting Activity");
		context.startService(i);

		Log.d(TAG, "After starting Activity");

	}

}
