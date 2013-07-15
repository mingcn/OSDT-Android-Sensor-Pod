/**
 * Receiver_stop.java
 * 
 * Broadcast receiver to stop the DLP4RDT service
 * 
 * @author Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.ntl.datalineprocessor4remotedt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver_Stop extends BroadcastReceiver {
	private String TAG = getClass().getSimpleName();
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		Log.d(TAG, "Broadcast receiver to StopServiceT.");
		runService();
	}

	private synchronized void runService() {
		Intent serviceIntent = new Intent(this.context,
				DataLineProcessor4RemoteDT_Service.class);
		this.context.stopService(serviceIntent);
	}

}
