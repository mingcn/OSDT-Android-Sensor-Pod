/**
 * Receiver_Restart.java
 * 
 * Broadcast receiver to restart the DLP4RDT service.
 * 
 * @author Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.ntl.datalineprocessor4remotedt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver_Restart extends BroadcastReceiver {
	private String TAG = getClass().getSimpleName();
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		Log.d(TAG, "Broadcast receiver to RESTART DLP4RDT.");
		runService();
	}

	private synchronized void runService() {
		Intent serviceIntent;
		serviceIntent = new Intent(this.context,
				DataLineProcessor4RemoteDT_Service.class);
		this.context.startService(serviceIntent);
	}

}
