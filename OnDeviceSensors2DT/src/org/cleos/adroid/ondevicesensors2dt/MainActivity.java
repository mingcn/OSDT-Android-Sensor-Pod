package org.cleos.adroid.ondevicesensors2dt;

import java.util.List;

import android.app.Activity;
//import android.content.Context;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
//import android.hardware.Sensor;
//import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private String TAG = getClass().getSimpleName();

	private Button sendAccDataButton;
	private Button stopSendAccDataButton;
	private Button sendPressDataButton;
	private Button stopSendPressDataButton;
	private Button sendTempDataButton;
	private Button stopSendTempDataButton;
	private Button sendGPSDataButton;
	private Button stopSendGPSDataButton;

	private String ipp = "192.168.1.23:3333";
	private String textHint = "Default ipp: " + ipp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		EditText text = (EditText) findViewById(R.id.ippET);
		text.setHint(textHint);
		sendAccDataButton = (Button) findViewById(R.id.sendAccDataButton);
		stopSendAccDataButton = (Button) findViewById(R.id.stopSendAccDataButton);
		sendPressDataButton = (Button) findViewById(R.id.sendPressDataButton);
		stopSendPressDataButton = (Button) findViewById(R.id.stopSendPressDataButton);
		sendTempDataButton = (Button) findViewById(R.id.sendTempDataButton);
		stopSendTempDataButton = (Button) findViewById(R.id.stopSendTempDataButton);
		sendGPSDataButton = (Button) findViewById(R.id.sendGPSDataButton);
		stopSendGPSDataButton = (Button) findViewById(R.id.stopSendGPSDataButton);
		buttons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void buttons() {
		
		// Accelerometer
		sendAccDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendAccData(getIpp(), getDelay());
			}
		});

		stopSendAccDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Stopping send data.");
				stopSendAccData();
			}
		});
		
		
		
		// Pressure
		sendPressDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendPressData(getIpp(), getDelay());
			}
		});

		stopSendPressDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Stopping send data.");
				stopSendPressData();
			}
		});
		
		
		// Temperature
		sendTempDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendTempData(getIpp(), getDelay());
			}
		});

		stopSendTempDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Stopping send data.");
				stopSendTempData();
			}
		});
		
		// GPS
		sendGPSDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendGPSData(getIpp(), getDelay());
			}
		});

		stopSendGPSDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Stopping send data.");
				stopSendGPSData();
			}
		});
		
		enableButtons();
			
	
	}
	
    @Override
    protected void onResume() {
		super.onResume();
		enableButtons();
	}
    
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	private void enableButtons() {
	
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Accelerometer
				if(!existSensor(Sensor.TYPE_ACCELEROMETER)){
					sendAccDataButton.setEnabled(false);
					stopSendAccDataButton.setEnabled(false);
				}
				
				// Pressure
				if(!existSensor(Sensor.TYPE_PRESSURE)){
					sendPressDataButton.setEnabled(false);
					stopSendPressDataButton.setEnabled(false);
				}
				
				// Temperature
				if(!existSensor(Sensor.TYPE_TEMPERATURE)){
					sendTempDataButton.setEnabled(false);
					stopSendTempDataButton.setEnabled(false);
				}
				
				// GPS
				if(!gpsEnable()){
					sendGPSDataButton.setEnabled(false);
					stopSendGPSDataButton.setEnabled(false);
				}
				else {
					sendGPSDataButton.setEnabled(true);
					stopSendGPSDataButton.setEnabled(true);
				}
				
			}
		});
			
			
	}
	
	private boolean gpsEnable(){
		LocationManager mSM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final boolean gpsEnabled = mSM
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!gpsEnabled) {
			return false;
		}
		return true;
	}

	private boolean existSensor(int sensorType) {
		SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (sm.getDefaultSensor(sensorType) != null)
			return true;
		else return false;
		
	}

	private String getIpp(){
		final EditText simpleEditText = (EditText) findViewById(R.id.ippET);
		String ippTE = simpleEditText.getText().toString();
		if (ippTE.length() < 7) {
			Log.i(TAG, "Incorrect IP, usign default ip.");
			ippTE = ipp;
		}
		return ippTE;
	}
	
	private int getDelay(){
		final EditText delayNumber = (EditText) findViewById(R.id.DelayText);
		String delayStr = delayNumber.getText().toString();
		int delay = 250;
		if(delayStr.length()>=1) delay = Integer.parseInt(delayStr);
		return delay;
	}

	// Accelerometer
	private void sendAccData(String ipp, int delay) {
		stopSendAccData();
		Intent i = new Intent(this, Accelerometer2DT_Service.class);
		i.putExtra("IPP", ipp);
		i.putExtra("DELAY", delay);
		startService(i);
	}

	private void stopSendAccData() {
		Intent i = new Intent(this, Accelerometer2DT_Service.class);
		stopService(i);
	}

	// Pressure
	private void sendPressData(String ipp, int delay) {
		stopSendPressData();
		Intent i = new Intent(this, Pressure2DT_Service.class);
		i.putExtra("IPP", ipp);
		i.putExtra("DELAY", delay);
		startService(i);
	}

	private void stopSendPressData() {
		Intent i = new Intent(this, Pressure2DT_Service.class);
		stopService(i);
	}
	
	// Temperature
	private void sendTempData(String ipp, int delay) {
		stopSendTempData();
		Intent i = new Intent(this, Temperature2DT_Service.class);
		i.putExtra("IPP", ipp);
		i.putExtra("DELAY", delay);
		startService(i);
	}

	private void stopSendTempData() {
		Intent i = new Intent(this, Temperature2DT_Service.class);
		stopService(i);
	}
	
	// GPS
	private void sendGPSData(String ipp, int delay) {
		stopSendGPSData();
		Intent i = new Intent(this, GPS2DT_Service.class);
		i.putExtra("IPP", ipp);
		i.putExtra("DELAY", delay);
		startService(i);
	}

	private void stopSendGPSData() {
		Intent i = new Intent(this, GPS2DT_Service.class);
		stopService(i);
	}
}