package org.cleos.adroid.ondevicesensors2dt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class GPS2DT_Service extends Service {

	private String TAG = getClass().getSimpleName();
	private String name = "GPS";
	private GPS2DT_Thread thread;
	private String ipp;
	private int delay = 250;

	// Keys for maintaining UI states after rotation.
	private static final String KEY_FINE = "use_fine";
	private static final String KEY_BOTH = "use_both";

	// for the GPS
	private LocationManager mSM;

	private boolean setup() {

		mSM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final boolean gpsEnabled = mSM
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!gpsEnabled) {
			// Build an alert dialog here that requests that the user enable
			// the location services, then when the user clicks the "OK" button,
			// call enableLocationSettings()
			// new EnableGpsDialogFragment().show(getSupportFragmentManager(),
			// "enableGpsDialog");
			msgToast("No GPS activated, go to setting to activate it.");
			//callLocalizationSettings();
			return false;
		}
		return true;
	}
	
	private void callLocalizationSettings(){
		Intent intent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.ipp = intent.getExtras().getString("IPP");
		this.delay = intent.getExtras().getInt("DELAY");
		if (setup())
			runService();
		return (START_NOT_STICKY);
	}

	@Override
	public void onDestroy() {
		thread.abort();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		msgToast(name + " Service Terminated");
	}

	private void runService() {
		Notification note = new Notification(R.drawable.ic_launcher, "The "
				+ name + " service is running", System.currentTimeMillis());
		Intent i = new Intent(this, MainActivity.class);

		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

		note.setLatestEventInfo(this, name + " Sensor", "Now Running!", pi);
		note.flags |= Notification.FLAG_NO_CLEAR;

		msgToast(name + " Services started on runService()");
		startForeground(1339, note);

		spawThread();
	}

	private void spawThread() {
		Log.i(TAG, "spawThread");
		String[] chNames = { "Latitude", "Longitude", "Altitud", "Accuracy" };

		String[] dTypes = { "float64", "float64", "float64", "float64" };

		String[] units = { "Degrees", "Degrees", "Meters", "Meters" };

		String[] MIMEs = { "application/octet-stream",
				"application/octet-stream", "application/octet-stream",
				"application/octet-stream" };

		thread = new GPS2DT_Thread(name, this, ipp, mSM);
		thread.setChNames(chNames);
		thread.setdTypes(dTypes);
		thread.setMIMEs(MIMEs);
		thread.setUnits(units);
		thread.setDelay(delay);
		thread.start();
	}

	private void msgToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


}
