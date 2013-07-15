/**
 * RBNB_Service_Receiver.java
 * 
 * Broadcast receiver to restart or stop the DT service and server.
 * When start or stop first check if the service is lock, if not the service 
 *  is locked. 
 * 
 * @author Gesuri Ramirez
 * @date July 2012
 */

package org.cleos.dataturbine;

import org.cleos.android.lib.Constants;
import org.cleos.android.lib.Utils;
import org.cleos.android.lib.Write2File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RBNB_Service_Receiver_Restart_RBNB extends BroadcastReceiver {
	private String TAG = "RBNB_Service_Receiver_Restart_RBNB";
	private byte calledFrom = Constants.ACTIVITY;
	Context context;
	private Write2File log = new Write2File("RBNB", TAG + ".txt");

	@Override
	synchronized public void onReceive(Context context, Intent intent) {
		this.context = context;
		Log.i(TAG, "Broadcast receiver to DT service.");

		receivedCommand(intent.getExtras());
	}

	private synchronized void runService() {
		new RunDTThread(this.context, this.calledFrom).start();
	}

	private class RunDTThread extends Thread {
		private Context contx;
		private byte cFrom;

		public RunDTThread(Context contx, byte cFrom) {
			this.contx = contx;
			this.cFrom = cFrom;
		}

		@Override
		public void run() {
			Intent serviceIntent;
			serviceIntent = new Intent(this.contx, DTservice_T1_Service.class);
			serviceIntent.putExtra(Constants.CALLED_FROM, cFrom);
			this.contx.startService(serviceIntent);
		}
	}

	private synchronized void killService() {
		Intent serviceIntent;
		serviceIntent = new Intent(this.context, DTservice_T1_Service.class);
		this.context.stopService(serviceIntent);
		Log.i(TAG, "Service is being killed");
	}

	private synchronized void receivedCommand(Bundle extras) {
		byte action = Constants.RESTART;
		if (!isLockRBNBService()) {
			lockTheService();
			if (extras != null) {
				this.calledFrom = extras.getByte(Constants.CALLED_FROM);
				action = extras.getByte(Constants.ACTION);
				log.writelnT("Service will START and was called from "
						+ Constants.getString(calledFrom));

				switch (action) {
				case Constants.START:
					runService();
					break;
				case Constants.STOP:
					killService();
					break;
				case Constants.RESTART:
					runService();
					break;
				}

			}// end if(extras != null)
			else {
				Log.i(TAG,
						"No extra values for the service (nothing was done).");
				log.writelnT("No extra values for the service (nothing was done).");
			}
		} else
			Log.i(TAG, "The DT service is LOCKED!");

	}// end receivedCommand

	private synchronized boolean isLockRBNBService() {
		int lock = Utils.readAIntInLocalFile(context,
				Constants.LOCK_RBNB_SERVICE_FLAG_FILE);
		if (lock == Constants.LOCK)
			return true;
		else
			return false;
	}

	private synchronized void lockTheService() {
		Utils.writeAIntInLocalFile(context,
				Constants.LOCK_RBNB_SERVICE_FLAG_FILE, Constants.LOCK);
		Log.i(TAG, "The service is locked!");
	}

}
