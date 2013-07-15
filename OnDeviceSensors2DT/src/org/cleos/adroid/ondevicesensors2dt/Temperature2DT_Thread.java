package org.cleos.adroid.ondevicesensors2dt;

import org.cleos.android.lib.Write2File;
import org.cleos.android.rbnb.SimpleFlusher;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class Temperature2DT_Thread extends Thread implements SensorEventListener {
	
	private String TAG = getClass().getSimpleName();
	private boolean abort_ = false;
	private SensorManager mSM;
	private Sensor sensor;
	private Context context;
	
	private SimpleFlusher sf;
	
	private String name;
	private Write2File log = new Write2File("Temperature", TAG, true);
	
	private int delay = 250; // in mS

	
	private String[] data = new String[1];
	

	public Temperature2DT_Thread(String name, Context context, String ipp, SensorManager mSM, Sensor mS){
		this.name = name;
		this.context = context;
		this.mSM = mSM;
		this.sensor = mS;
		
		sf = new SimpleFlusher(name, context, ipp, log);
		
		mSM.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
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
		Log.i(TAG, "Temperature thread finished with unregisterListener.");
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
	  public final void onSensorChanged(SensorEvent event) {
	    float celciusOfTemperature = event.values[0];
	    data[0] = Float.toString(celciusOfTemperature);
	    // Do something with this sensor data.
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
