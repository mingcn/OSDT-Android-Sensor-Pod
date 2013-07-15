/**
 * DTservice_T1Activity.java
 * 
 * This starts and stops the foreground service to run and stop
 * RBNB server.
 * When this is launched check for messages on the intent in order
 * to start or stop the foreground service.
 * 
 * @author Gesuri Ramirez
 * @date July 2012
 */

package org.cleos.dataturbine;

import org.cleos.android.lib.Constants;
import org.cleos.android.lib.Write2File;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.View;

public class DTservice_T1Activity extends Activity {
	String TAG = "DTservice_T1Activity";
	Write2File log = new Write2File("RBNB", "RBNB_Service.txt", true);

	byte calledFrom = Constants.ACTIVITY;

	Intent serviceIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.serviceIntent = new Intent(this, DTservice_T1_Service.class);
	}

	public void start(View v) {
		runService();
	}

	public void stop(View v) {
		killService();
	}

	private void runService() {
		this.serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.serviceIntent.putExtra(Constants.CALLED_FROM, this.calledFrom);
		startService(this.serviceIntent);
	}

	private void killService() {
		stopService(this.serviceIntent);
		Log.i(TAG, "Service is being killed");
	}

}