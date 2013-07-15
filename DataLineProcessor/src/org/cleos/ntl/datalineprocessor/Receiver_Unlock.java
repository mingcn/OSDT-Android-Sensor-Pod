/**
 * Receiver_Unlock.java
 * 
 * Broadcast receiver to unlock the DLP service
 * 
 * @author Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.ntl.datalineprocessor;

import org.cleos.android.lib.Constants;
import org.cleos.android.lib.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver_Unlock extends BroadcastReceiver {
	private String TAG = getClass().getSimpleName();
	private String flagFile = Constants.LOCK_DLP_FLAG_FILE;

	@Override
	public void onReceive(Context context, Intent intent) {
		// Log.d(TAG, "Broadcast received!");
		Utils.lockService(context, flagFile, false);
		Log.i(TAG,
				"The DLP service is lock? "
						+ Utils.isLockService(context, flagFile));
	}

}
