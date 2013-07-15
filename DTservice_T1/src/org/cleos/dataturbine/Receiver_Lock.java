package org.cleos.dataturbine;

import org.cleos.android.lib.Constants;
import org.cleos.android.lib.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver_Lock extends BroadcastReceiver {
	private String TAG = getClass().getSimpleName();
	private String flagFile = Constants.LOCK_RBNB_SERVICE_FLAG_FILE;

	@Override
	public synchronized void onReceive(Context context, Intent intent) {
		//Log.d(TAG, "Broadcast received!");
		Utils.lockService(context, flagFile, true);
		Log.d(TAG, "The DT service is lock? " + Utils.isLockService(context,flagFile));
	}

}
