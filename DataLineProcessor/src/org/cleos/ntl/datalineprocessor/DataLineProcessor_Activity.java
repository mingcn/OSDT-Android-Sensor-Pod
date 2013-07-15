/**
 * DataLineProcessor_Activity.java
 * 
 * Activity to start and stop the DataLineProcesoor service
 * 
 * @author Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.ntl.datalineprocessor;

import org.cleos.ntl.datalineprocessor.DataLineProcessor_Service.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class DataLineProcessor_Activity extends Activity {
	private String TAG = getClass().getSimpleName();

	DataLineProcessor_Service mService;
	boolean mBound = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			Log.i(TAG,
					"onStop(), the service was bound and is trying to unbound");
			unbindService(mConnection);
			mBound = false;
		}
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(TAG, "onServiceConnected");
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	public void startService(View v) {
		// new TestThread().start();
		Intent i = new Intent(this, DataLineProcessor_Service.class);
		startService(i);
	}

	public void stopService(View v) {
		Log.i(TAG, "Stopping service");
		Intent serviceIntent = new Intent(this, DataLineProcessor_Service.class);
		stopService(serviceIntent);
	}

}// end class DataLineProcessor