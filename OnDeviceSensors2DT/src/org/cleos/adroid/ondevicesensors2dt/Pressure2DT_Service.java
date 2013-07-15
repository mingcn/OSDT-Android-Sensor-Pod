package org.cleos.adroid.ondevicesensors2dt;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Pressure2DT_Service extends Service {

	private String TAG = getClass().getSimpleName();
	private String name = "Pressure";
	private SensorManager mSM;
	private Sensor sensor;
	private Pressure2DT_Thread thread;
	private String ipp;
	private int delay = 250;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.mSM = getSensorManager();
		this.sensor = sSensor(mSM);
		this.ipp = intent.getExtras().getString("IPP");
		this.delay = intent.getExtras().getInt("DELAY");
		if (sensor != null)
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
		startForeground(1338, note);

		spawThread();
	}

	private void spawThread() {
		Log.i(TAG, "spawThread");
		String[] chNames = { "pressure" };

		String[] dTypes = { "float64" };

		String[] units = { "mBar" };

		String[] MIMEs = { "application/octet-stream" };

		thread = new Pressure2DT_Thread(name, this, ipp, mSM, sensor);
		thread.setChNames(chNames);
		thread.setdTypes(dTypes);
		thread.setMIMEs(MIMEs);
		thread.setUnits(units);
		thread.setDelay(delay);
		thread.start();
	}

	private SensorManager getSensorManager() {
		return (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	}

	private Sensor sSensor(SensorManager sm) {
		if (sm.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
			return sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
		} else {
			String msg = "The " + name + " is no pressent in this device!";
			Log.e(TAG, msg);
			msgToast(msg);
			return null;
		}
	}

	private void msgToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}