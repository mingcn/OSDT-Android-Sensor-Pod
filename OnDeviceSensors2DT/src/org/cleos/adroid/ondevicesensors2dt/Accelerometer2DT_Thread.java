package org.cleos.adroid.ondevicesensors2dt;

import org.cleos.android.lib.Write2File;
import org.cleos.android.rbnb.SimpleFlusher;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class Accelerometer2DT_Thread extends Thread implements SensorEventListener {
	
	private String TAG = getClass().getSimpleName();
	private boolean abort_ = false;
	private SensorManager mSM;
	private Sensor accelerometer;
	private Context context;
	
	private SimpleFlusher sf;
	
	private String name;
	private Write2File log = new Write2File("Accelerometer", TAG, true);
	
	private int delay = 250; // in mS

	// for filtering the accelerometer data
	float[] gravity = new float[3];
	float[] linear_acceleration = new float[3];
	private String[] data = new String[6];
	

	public Accelerometer2DT_Thread(String name, Context context, String ipp, SensorManager mSM, Sensor mS){
		this.name = name;
		this.context = context;
		//this.addressAndPort = ipp;
		this.mSM = mSM;
		this.accelerometer = mS;
		
		sf = new SimpleFlusher(name, context, ipp, log);
		
		mSM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void setDelay(int delay_mS){ //
		this.delay = delay_mS;
	}
	
	@Override
	public void run() {
		super.run();
		sf.createConnections();
		while (true) {
			synchronized (this) {
				if (abort_) {
					Log.e(TAG, "Loop terminated!******************************");
					kill();
					break;
				}
			}
			sendData();
			sleep(delay);
		}
		mSM.unregisterListener(this);
		Log.i(TAG, "Accelerometer thread finished with unregisterListener.");
	}
	
	public synchronized void abort(){
		abort_ = true;
	}
	
	private void sendData() {
		sf.flushData(data);
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// In this example, alpha is calculated as t / (t + dT),
		// where t is the low-pass filter's time-constant and
		// dT is the event delivery rate.
		final float alpha = (float) 0.8;
		// Isolate the force of gravity with the low-pass filter.
		gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
		gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
		gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
		// Remove the gravity contribution with the high-pass filter.
		linear_acceleration[0] = event.values[0] - gravity[0];
		linear_acceleration[1] = event.values[1] - gravity[1];
		linear_acceleration[2] = event.values[2] - gravity[2];
		
		data[0] = Float.toString(gravity[0]);
		data[1] = Float.toString(gravity[1]);
		data[2] = Float.toString(gravity[2]);
		data[3] = Float.toString(linear_acceleration[0]);
		data[4] = Float.toString(linear_acceleration[1]);
		data[5] = Float.toString(linear_acceleration[2]);
	}
	
	private void sleep(int mS) {
		try {
			super.sleep(mS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public void kill() {
		sf.kill();
	}
	
	public void setChNames(String[] chNames) {
		sf.setChNames(chNames);
	}

	public void setdTypes(String[] dTypes) {
		sf.setdTypes(dTypes);
	}

	public void setUnits(String[] units) {
		sf.setUnits(units);
	}

	public void setMIMEs(String[] mIMEs) {
		sf.setMIMEs(mIMEs);
	}

	
}
