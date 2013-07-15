/*
 Copyright 2012 University of California, San Diego

 Licensed under the Apache License, Version 1.1 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-1.1

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

/**
 * IOIOserial.java
 * 
 * It uses the IOIO library factory to interface the IOIO board.
 * The IOIO can be connected by USB or BT
 * 
 * Test for:
 *  connection with the IOIO board.
 *  3 TTL serial connections
 *  analog inputs
 *  analog voltage sensor
 *  analog temperature sensor
 *  analog humidity sensor
 *  onboard LED
 * 
 *  @author Gesuri Ramirez, Peter Shin
 *  @date July 2012
 */

package org.cleos.ioio.testboard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ioio.examples.IOIOserial.R;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Button;

public class IOIOserial extends IOIOActivity {
	private String TAG = getClass().getSimpleName();

	private TextView textRS1;
	private TextView textRS2;
	private TextView textRS3;
	private TextView textT;
	private TextView textH;
	private TextView textV;
	private TextView textIR;
	private TextView textFSH;
	private TextView textFST;
	private TextView textPressure;

	private ToggleButton toggleButton_;
	private ToggleButton crTButton, lfTButton;
	private Button updateButton;
	private Button sendCommand_1;
	private Button sendCommand_2;
	private Button sendCommand_3;

	private RxThread rxThread_1;
	private RxThread rxThread_2;
	private RxThread rxThread_3;

	// IOIO io's
	private DigitalOutput led_;
	private AnalogInput inT;
	private AnalogInput inH;
	private AnalogInput inV;
	private AnalogInput inIR;
	private AnalogInput inFSH;
	private AnalogInput inFST;
	private Uart uart1; // object for the UART (serial) port
	private Uart uart2; // object for the UART (serial) port
	private Uart uart3; // object for the UART (serial) port

	private DataOutputStream out1;
	private DataInputStream in1;
	private DataOutputStream out2;
	private DataInputStream in2;
	private DataOutputStream out3;
	private DataInputStream in3;
	
	private SensorManager mSensorManager;
	private Sensor mPressure;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	    // Get an instance of the sensor service, and use that to get an instance of
	    // a particular sensor.
	    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		
	}// end onCreate()

	private void sendData(DataOutputStream out, String cmd) {
		try {
			out.writeBytes(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		textRS1 = (TextView) findViewById(R.id.TextViewRx1);
		textRS2 = (TextView) findViewById(R.id.TextViewRx2);
		textRS3 = (TextView) findViewById(R.id.TextViewRx3);
		textT = (TextView) findViewById(R.id.TextViewT);
		textH = (TextView) findViewById(R.id.TextViewH);
		textV = (TextView) findViewById(R.id.TextViewV);
		textIR = (TextView) findViewById(R.id.TextViewIR);
		textFSH = (TextView) findViewById(R.id.TextViewFSH);
		textFST = (TextView) findViewById(R.id.TextViewFST);
		textPressure = (TextView) findViewById(R.id.TextViewPressure);

		toggleButton_ = (ToggleButton) findViewById(R.id.ToggleButton);
		updateButton = (Button) findViewById(R.id.updateButton);
		crTButton = (ToggleButton) findViewById(R.id.crTButton);
		lfTButton = (ToggleButton) findViewById(R.id.lfTButton);
		crTButton.setChecked(true);
		lfTButton.setChecked(true);

		sendCommand_1 = (Button) findViewById(R.id.sendCommand_1);
		sendCommand_2 = (Button) findViewById(R.id.sendCommand_2);
		sendCommand_3 = (Button) findViewById(R.id.sendCommand_3);
		
		SensorActivity SA = createSensorActivity();
	    mSensorManager.registerListener(SA, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
	    
	    /*SensorActivity PressureSensor = new SensorActivity();
		float readingPressure = PressureSensor.getPressure();
	    Toast.makeText(this, Float.toString(readingPressure), Toast.LENGTH_LONG).show();*/
	    
		setButtons();
		
		enableUi(false);
	}// end onResume()

	
	class SensorActivity extends Activity implements SensorEventListener{
		
		 float pressure;
		
		  @Override
		  public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		    // Do something here if sensor accuracy changes.
		  }

		  @Override
		  public final void onSensorChanged(SensorEvent event) {
		    pressure = event.values[0];
		    // Do something with this sensor data.
		  }
		  
		  public float getPressure()
		  {
			  return pressure;
		  }
		
	}
	
	protected SensorActivity createSensorActivity() {
		return new SensorActivity();
	}

	private void setButtons() {
		toggleButton_.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					led_.write(!toggleButton_.isChecked());
				} catch (ConnectionLostException e) {
					e.printStackTrace();
				}
			}
		});

		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					float readingT = inT.getVoltage();
					readingT = (float) ((readingT * 44.44444) - 61.11);
					setTextT(Float.toString(readingT));
					float readingH = inH.getVoltage();
					readingH = (float) ((readingH * 76.24) - 40.2);
					setTextH(Float.toString(readingH));
					float readingV = inV.getVoltage();
					readingV = (float) ((readingV - 2.5) / 0.0681) * -1;
					setTextV(Float.toString(readingV));
					float readingIR = inIR.getVoltage();
					readingIR = (float) (readingIR - 0.64);
					setTextIR(Float.toString(readingIR));
					float readingFSH = inFSH.getVoltage();
					//readingFSH = (float)(readingFSH - 1.98);
					setTextFSH(Float.toString(readingFSH));
					float readingFST = inFST.getVoltage();
					//readingFST = (float) (readingFST - 2.75);
					setTextFST(Float.toString(readingFST));
					
					SensorActivity PressureSensor = new SensorActivity();
					float readingPressure = PressureSensor.getPressure();
					setTextPressure(Float.toString(readingPressure));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		sendCommand_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText simpleEditText = (EditText) findViewById(R.id.command1);
				String cmd = simpleEditText.getText().toString();
				if (cmd.length() < 1) {
					cmd = "0R0";
					Log.i(TAG, "No command, usign default command: " + cmd);
				}
				Log.i(TAG, "Sending command: " + cmd);
				sendData(out1, cmd + getEndCommandLine());
			}
		});

		sendCommand_2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText simpleEditText = (EditText) findViewById(R.id.command2);
				String cmd = simpleEditText.getText().toString();
				if (cmd.length() < 1) {
					cmd = "0R0";
					Log.i(TAG, "No command, usign default command: " + cmd);
				}
				Log.i(TAG, "Sending command: " + cmd);
				sendData(out2, cmd + getEndCommandLine());
			}
		});

		sendCommand_3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText simpleEditText = (EditText) findViewById(R.id.command3);
				String cmd = simpleEditText.getText().toString();
				if (cmd.length() < 1) {
					cmd = "0R0";
					Log.i(TAG, "No command, usign default command: " + cmd);
				}
				Log.i(TAG, "Sending command: " + cmd);
				sendData(out3, cmd + getEndCommandLine());
			}
		});

	}

	public void onPause() {
		super.onPause();
		if (rxThread_1 != null)
			rxThread_1.abort();
		if (rxThread_2 != null)
			rxThread_2.abort();
		if (rxThread_3 != null)
			rxThread_3.abort();
		if (inT != null)
			inT.close();
		if (inH != null)
			inH.close();
		if (inV != null)
			inV.close();
		if (inIR != null)
			inIR.close();
		if (inFSH != null)
			inFSH.close();
		if (inFST != null)
			inFST.close();
		if (uart1 != null)
			uart1.close();
		if (uart2 != null)
			uart2.close();
		if (uart3 != null)
			uart3.close();
		
	    // Unregister listener
		//mSensorManager.unregisterListener(this);
	}

	private String getEndCommandLine() {
		StringBuffer endCmdLine = new StringBuffer("");
		if (crTButton.isChecked())
			endCmdLine.append((char) 13);
		if (lfTButton.isChecked())
			endCmdLine.append((char) 10);
		return endCmdLine.toString();
	}

	class Looper extends BaseIOIOLooper {

		@Override
		public void setup() throws ConnectionLostException {
			try {
				inT = ioio_.openAnalogInput(46);
				inH = ioio_.openAnalogInput(45);
				inV = ioio_.openAnalogInput(44);
				inIR = ioio_.openAnalogInput(33);
				inFSH = ioio_.openAnalogInput(34);
				inFST = ioio_.openAnalogInput(35);
				
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);

				// the port is opened in pins 5 (Rx) and 6 (Tx) and baud rate of
				// 9600
				uart1 = ioio_.openUart(5, 6, 9600, Uart.Parity.NONE,
						Uart.StopBits.ONE);
				uart2 = ioio_.openUart(3, 4, 9600, Uart.Parity.NONE,
						Uart.StopBits.ONE);
				uart3 = ioio_.openUart(10, 11, 9600, Uart.Parity.NONE,
						Uart.StopBits.ONE);

				// in and out streams
				out1 = new DataOutputStream(uart1.getOutputStream());
				in1 = new DataInputStream(uart1.getInputStream());

				out2 = new DataOutputStream(uart2.getOutputStream());
				in2 = new DataInputStream(uart2.getInputStream());

				out3 = new DataOutputStream(uart3.getOutputStream());
				in3 = new DataInputStream(uart3.getInputStream());

				enableUi(true);
			} catch (ConnectionLostException e) {
				enableUi(false);
				throw e;
			}

			// There are create and start threads to read the serial input (rx)
			rxThread_1 = new RxThread(1, in1);
			rxThread_2 = new RxThread(2, in2);
			rxThread_3 = new RxThread(3, in3);
			rxThread_1.start();
			rxThread_2.start();
			rxThread_3.start();
		}

		@Override
		public void loop() throws ConnectionLostException {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				enableUi(false);
				ioio_.disconnect();
			}
		}

		@Override
		public void disconnected() {
			enableUi(false);
		}

	}// end class Looper

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}

	private void enableUi(final boolean enable) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				toggleButton_.setEnabled(enable);
				updateButton.setEnabled(enable);
				crTButton.setEnabled(enable);
				lfTButton.setEnabled(enable);

				sendCommand_1.setEnabled(enable);
				sendCommand_2.setEnabled(enable);
				sendCommand_3.setEnabled(enable);

			}
		});
	}

	private void setTextT(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textT.setText(str);
			}
		});
	}

	private void setTextH(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textH.setText(str);
			}
		});
	}

	private void setTextV(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textV.setText(str);
			}
		});
	}
	
	private void setTextIR(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textIR.setText(str);
			}
		});
		
	}
	
	private void setTextFSH(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textFSH.setText(str);
			}
		});
		
	}
	
	private void setTextFST(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textFST.setText(str);
			}
		});
		
	}
	
	private void setTextPressure(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textPressure.setText(str);
			}
		});
		
	}

	private void setTextRxs(int channel, String str) {
		switch (channel) {
		case 1:
			setTextRS1(str);
			break;
		case 2:
			setTextRS2(str);
			break;
		case 3:
			setTextRS3(str);
			break;
		}
	}

	private void setTextRS1(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textRS1.setText(str);
			}
		});
	}

	private void setTextRS2(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textRS2.setText(str);
			}
		});
	}

	private void setTextRS3(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textRS3.setText(str);
			}
		});
	}

	// ------------------------------------------------------------------------
	// thread to read the Rx of each serial port
	public class RxThread extends Thread {
		private String TAG = getClass().getSimpleName();
		private int channel;
		private DataInputStream dIn;
		private boolean abort_ = false;

		public RxThread(int channel, DataInputStream in) {
			this.channel = channel;
			this.dIn = in;
		}

		@Override
		public void run() {
			super.run();
			Log.i(TAG + "::" + channel, "RxThread started!");
			while (true) {
				synchronized (this) {
					if (abort_) {
						break;
					}
				}
				try {
					while (true) {
						if (dIn.available() > 0) {
							String line = dIn.readLine();
							Log.i(TAG, "data = " + line);
							setTextRxs(channel, line);
						}
						if (abort_) {
							Log.i(TAG + "::" + channel, "Abort!");
							break;
						}
						sleep(10);
					}
				} catch (Exception e) {
					Log.e(TAG + "::" + channel, "Unexpected exception caught"
							+ e.toString());
					break;
				}
			}

		}// end run()

		synchronized public void abort() {
			abort_ = true;
		}
	}// end RxThread

}