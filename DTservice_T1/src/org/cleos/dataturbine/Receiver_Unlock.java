/**
 * Receiver_Unlock.java
 * 
 * Broadcast receiver to unlock the DT service
 * 
 * @author Gesuri Ramirez
 * @date July 2012
 */

package org.cleos.dataturbine;

import org.cleos.android.lib.Constants;
import org.cleos.android.lib.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver_Unlock extends BroadcastReceiver {
	private String TAG = getClass().getSimpleName();
	private String flagFile = Constants.LOCK_RBNB_SERVICE_FLAG_FILE;

	@Override
	public synchronized void onReceive(Context context, Intent intent) {
		Utils.lockService(context, flagFile, false);
		Log.d(TAG,
				"The DT service is lock? "
						+ Utils.isLockService(context, flagFile));
	}

}
