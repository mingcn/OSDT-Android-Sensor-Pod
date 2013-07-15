/**
 * RunProcessDataLine.java
 * 
 * Thread to start the service and pass the data with the name of the sensor 
 * 
 * @author Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.ntl.datalineprocessor;

import org.cleos.android.lib.Constants;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RunProcessDataLine extends Thread {
	private String TAG = getClass().getSimpleName();
	private Context context;
	String slcName;
	String dataLine;
	
	public RunProcessDataLine(Context context, String slcName, String dataLine){
		this.context = context;
		this.slcName = slcName;
		this.dataLine = dataLine;
	}
	
	@Override
	public void run() {
		super.run();
		Log.w(TAG,"Got thread!!!");
		
		Intent i=new Intent(this.context, DataLineProcessor_Service.class);
	    i.putExtra(Constants.SLC_NAME, slcName);
	    i.putExtra(Constants.DATALINE, dataLine);
	    this.context.startService(i);
	
	}
}
