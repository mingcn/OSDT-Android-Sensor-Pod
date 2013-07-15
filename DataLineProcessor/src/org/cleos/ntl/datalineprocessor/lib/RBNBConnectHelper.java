/**
 * RBNBConnectHelper.java
 * 
 * Create the connection to the DT
 * If something fail, the service will be lock and the DT server will be
 *  restarted
 * 
 * @author Peter Shin, Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.ntl.datalineprocessor.lib;

import org.cleos.android.lib.Constants;
import org.cleos.android.lib.Utils;
import org.cleos.android.lib.Write2File;
import org.cleos.android.ntl.broadcasts.SendBroadcast;

import android.content.Context;
import android.util.Log;

import com.rbnb.sapi.SAPIException;
import com.rbnb.sapi.Source;

public class RBNBConnectHelper {
	Context context;
	String TAG = getClass().getSimpleName();
	String ipp = "localhost:3333";
	String name = "";
	Source dtSrc = null;
	private Write2File log;

	public void connectToDT(Context c, Write2File log, Source src, String ipp,
			String name) {
		this.context = c;
		this.log = log;
		this.dtSrc = src;
		this.ipp = ipp;
		this.name = name;
		new ConnectToDT().start();
	}

	public class ConnectToDT extends Thread {

		@Override
		public void run() {
			try {
				dtSrc.OpenRBNBConnection(ipp, name);
				// if there is no error then unlock DLP service
				Utils.wait(5000);
				Utils.lockService(context, Constants.LOCK_DLP_FLAG_FILE, false);
			} catch (SAPIException e) {
				Log.e(TAG, "SAPIException, no connection to " + ipp + " Name: "
						+ name, e);
				// Log.i(TAG,"Error on opening the connection to RBNB server. ");
				log.writelnT("RBNBConnectHelper. Error on opening the connection to RBNB server. "
						+ e.getMessage());
				Log.i(TAG,
						"****Calling the broadcast to restart the RBNB server!!!!");
				// lock this service
				Utils.lockService(context, Constants.LOCK_DLP_FLAG_FILE, true);
				// call tp restart RBNB
				SendBroadcast.restartRBNB(context);

			}
		}

	}// end class ConnectToDT

}// end class RBNBHelper
