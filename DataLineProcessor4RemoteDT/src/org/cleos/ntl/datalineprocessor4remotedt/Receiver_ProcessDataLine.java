/**
 * Receiver_ProcessDataLine.java
 * 
 * Broadcast receiver to process the data that comes from the DataGather
 * First check if the service is unlock
 * if the service is lock, update the lock counter. If the counter is more than
 *  153, it will unlock everything.
 * If the service is unlock, it will start a new thread to call the DLP4RDT and 
 *  then finish. The new thread is to avoid the receiver wait while other
 *  broadcast is received.   
 * 
 * @author Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.ntl.datalineprocessor4remotedt;

import org.cleos.android.lib.Constants;
import org.cleos.android.lib.Utils;
import org.cleos.android.lib.Write2File;
import org.cleos.android.ntl.broadcasts.SendBroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Receiver_ProcessDataLine extends BroadcastReceiver {
	private String TAG = getClass().getSimpleName();
	private Context context;
	private String flagFile = Constants.LOCK_DLP_FLAG_FILE;
	private Write2File log = new Write2File(TAG, TAG + ".txt");

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		receivedCommand(intent.getExtras());
	}

	private synchronized void receivedCommand(Bundle extras) {
		if (!Utils.isLockService(this.context, flagFile)) {
			Utils.setZeroLockCounter(context);
			if (extras != null) {
				/**
				 * The message will have String name, it is the name of the SLC
				 * String dataLine, the complete line of data
				 */
				String slcName = extras.getString(Constants.SLC_NAME);
				String dataLine = extras.getString(Constants.DATALINE);

				if(dataLine.charAt(0) == '0')
					if(dataLine.charAt(1) == '0')
						if(dataLine.charAt(2) == '0')
							if(dataLine.charAt(3) == '1')
								if(dataLine.charAt(4) == '3')
									return;
				if(dataLine.charAt(0) == 'O')
					if(dataLine.charAt(1) == 'K')
						return;
				
				Log.i(TAG, "SLC name: " + slcName+". Data line: " + dataLine);
				//Log.d(TAG, "Data line: " + dataLine);
				// start a thread to bind and process the dataline
				
				//create a thread just in case the start service take more time
				new RunProcessDataLine(this.context, slcName, dataLine).start();

			} else {
				Log.e(TAG, "Error: No extra values (nothing was done).");
				log.writelnT("Error: No extra values (nothing was done).");
			}
		} else{
			Utils.incrementLockCounter(context);
			int numLocks = Utils.getLockCounter(context);
			Log.i(TAG, "The DLP4RDT service is LOCKED!!! The actual number of request is: "+numLocks);
			if(numLocks>=153){
				Log.i(TAG, "The DLP4RDT service looks to be locked. Unlocking DLP4RDT and DT.");
				//unlock DLP
				Utils.lockService(context, flagFile, false);
				Utils.setZeroLockCounter(context);
			}
		}

	}// end receivedCommand


}
