/**
 * RBNBConnectHelper.java
 * 
 * Create the connection to the DT
 * If something fail, the service will be lock
 * 
 * @author Peter Shin, Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.ntl.datalineprocessor4remotedt.lib;

import org.cleos.android.lib.Utils;
import org.cleos.android.lib.Write2File;
import org.cleos.ntl.datalineprocessor4remotedt.DataLineProcessor_x;

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
	private DataLineProcessor_x dlp_x;

	public void connectToDT(Context c, DataLineProcessor_x dlp_x,
			Write2File log, Source src, String ipp, String name) {
		this.context = c;
		this.dlp_x = dlp_x;
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
				Utils.lockService(context, false);
			} catch (SAPIException e) {
				Log.e(TAG, "SAPIException, no connection to " + ipp + " Name: "
						+ name, e);
				// Log.i(TAG,"Error on opening the connection to RBNB server. ");
				log.writelnT("RBNBConnectHelper. Error on opening the connection to RBNB server. "
						+ e.getMessage());
			}
			return;
		}

	}// end class ConnectToDT

}// end class RBNBHelper
