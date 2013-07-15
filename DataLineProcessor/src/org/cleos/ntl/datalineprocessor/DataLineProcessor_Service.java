/**
 * DataLineProcessor_Service.java
 * 
 * Create a service for the DLP service if there is no one
 * Start the DLP threads for each sensor
 * Receive the data for each sensor and then look for the appropriate DLP
 *  thread to process the line 
 * 
 * 
 * @author Gesuri Ramirez, Peter Shin
 */

package org.cleos.ntl.datalineprocessor;

import java.util.LinkedList;

import org.cleos.android.lib.Constants;
import org.cleos.android.lib.Utils;
import org.cleos.android.lib.Write2File;
import org.cleos.android.ntl.utils.CommandList;
import org.cleos.android.ntl.utils.Configurator;
import org.cleos.ntl.datalineprocessor.lib.DTSrcManager;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.cleos.android.ntl.utils.Configurator;//add by pstango

public class DataLineProcessor_Service extends Service {
	private String TAG = getClass().getSimpleName();
	private boolean isRunningService = false;
	private String flagFile = Constants.LOCK_DLP_FLAG_FILE;
	private Write2File log = new Write2File("DataLineProcessor/" + TAG, TAG
			+ ".txt", true);

	DTSrcManager dtSrc;

	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();

	private LinkedList<DataLineProcessor_x> DLPs;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		runService(intent.getExtras());
		return (START_NOT_STICKY);
	}

	private void runService(Bundle extras) {

		Log.i(TAG, "The service is running? " + isRunningService);
		if (!isRunningService) {
			log.writelnT("The DataLineProcessor service will start.");
			// Log.i(TAG, "Got to runService()!");
			isRunningService = true;

			Notification note = new Notification(R.drawable.ic_launcher,
					"The DataLineProcessor service is running",
					System.currentTimeMillis());
			Intent i = new Intent(this, DataLineProcessor_Service.class);

			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

			note.setLatestEventInfo(this, TAG, "Now Running!", pi);
			note.flags |= Notification.FLAG_NO_CLEAR;

			msgToast("Services started on runService()");
			// log.writelnT("Services started on runService()");
			startForeground(1337, note);

		}

		if (extras != null) {
			String slcName = extras.getString(Constants.SLC_NAME);
			String dataLine = extras.getString(Constants.DATALINE);
			// look for the correct DLP_x
			if (DLPs != null) {
				for (DataLineProcessor_x dlpx : DLPs) {
					if (dlpx.getDLPName().equalsIgnoreCase(slcName)) {
						dlpx.processDataLine(dataLine);
						break;
					}
				}
			} else
				DLPs = spawnDLPs();
		} else {
			Log.i(TAG, "The DataLineProcessor will restart.");
			log.writelnT("The DataLineProcessor will restart.");

			// always close and create connection
			// destroy all the DataLineProcessor_x

			destroyAllDLP_x();

			// create all the DataLineProcessor_x

			DLPs = spawnDLPs();

			// after the source and the connection to the RBNB is open the
			// service will be unlock
			Utils.lockService(this, flagFile, false);
		}
	}

	private void destroyAllDLP_x() {
		if (DLPs != null) {
			for (DataLineProcessor_x dlpx : DLPs) {
				dlpx.kill();
				Utils.wait(1000);
			}
			DLPs.clear();
		}

	}

	private LinkedList<DataLineProcessor_x> spawnDLPs() {
		Configurator conf = new Configurator();
		// CommandList cmd;
		LinkedList<DataLineProcessor_x> dlpList = new LinkedList<DataLineProcessor_x>();

		// KippZonen SolarIR
		DataLineProcessor_x dlpSolarIR = new DataLineProcessor_x(this, "SolarIR");
		CommandList cmdSolarIR = conf.createSolarIRCmdList("SolarIR");
		dlpSolarIR.setAddressAndPort(cmdSolarIR.getDTAddress()[0]);
		dlpSolarIR.setChNames(cmdSolarIR.getChNames()[0]);
		dlpSolarIR.setDelimiter(cmdSolarIR.getDelimiter()[0]);
		dlpSolarIR.setdTypes(cmdSolarIR.getDTypes()[0]);
		dlpSolarIR.setMIMEs(cmdSolarIR.getMIMEs()[0]);
		dlpSolarIR.setNumParameter(cmdSolarIR.getChNames()[0].length);
		dlpSolarIR.setUnits(cmdSolarIR.getUnits()[0]);
		dlpSolarIR.start();
		
		//Fuel Stick Moisture
		DataLineProcessor_x dlpFSM = new DataLineProcessor_x(this, "FSM");
		CommandList cmdFSM = conf.createFSMCmdList("FSM");
		dlpFSM.setAddressAndPort(cmdFSM.getDTAddress()[0]);
		dlpFSM.setChNames(cmdFSM.getChNames()[0]);
		dlpFSM.setDelimiter(cmdFSM.getDelimiter()[0]);
		dlpFSM.setdTypes(cmdFSM.getDTypes()[0]);
		dlpFSM.setMIMEs(cmdFSM.getMIMEs()[0]);
		dlpFSM.setNumParameter(cmdFSM.getChNames()[0].length);
		dlpFSM.setUnits(cmdFSM.getUnits()[0]);
		dlpFSM.start();
		
		// onBoardTemp
		DataLineProcessor_x dlpOnBoardTemp = new DataLineProcessor_x(this,
				/*"temperature"*/ Configurator.onboardTemperature);//modified by pstango
		CommandList cmdTemp = conf.createTempCmdList(/*"temperature"*/ Configurator.onboardTemperature);//modified by pstango
		dlpOnBoardTemp.setAddressAndPort(cmdTemp.getDTAddress()[0]);
		dlpOnBoardTemp.setChNames(cmdTemp.getChNames()[0]);
		dlpOnBoardTemp.setDelimiter(cmdTemp.getDelimiter()[0]);
		dlpOnBoardTemp.setdTypes(cmdTemp.getDTypes()[0]);
		dlpOnBoardTemp.setMIMEs(cmdTemp.getMIMEs()[0]);
		dlpOnBoardTemp.setNumParameter(cmdTemp.getChNames()[0].length);
		dlpOnBoardTemp.setUnits(cmdTemp.getUnits()[0]);
		dlpOnBoardTemp.start();

		// onBoardHumi
		DataLineProcessor_x dlpOnBoardHumi = new DataLineProcessor_x(this,
				/*"humidity"*/ Configurator.onboardHumidity);//modified by pstango
		CommandList cmdHumi = conf.createHumiCmdList(/*"humidity"*/ Configurator.onboardHumidity);//modified by pstango
		dlpOnBoardHumi.setAddressAndPort(cmdHumi.getDTAddress()[0]);
		dlpOnBoardHumi.setChNames(cmdHumi.getChNames()[0]);
		dlpOnBoardHumi.setDelimiter(cmdHumi.getDelimiter()[0]);
		dlpOnBoardHumi.setdTypes(cmdHumi.getDTypes()[0]);
		dlpOnBoardHumi.setMIMEs(cmdHumi.getMIMEs()[0]);
		dlpOnBoardHumi.setNumParameter(cmdHumi.getChNames()[0].length);
		dlpOnBoardHumi.setUnits(cmdHumi.getUnits()[0]);
		dlpOnBoardHumi.start();

		// onBoardVolt
		DataLineProcessor_x dlpOnBoardVolt = new DataLineProcessor_x(this,
				/*"voltage"*/ Configurator.onboardVoltage);//modified by pstango
		CommandList cmdVolt = conf.createVoltCmdList(/*"voltage"*/ Configurator.onboardVoltage);//modified by pstango
		dlpOnBoardVolt.setAddressAndPort(cmdVolt.getDTAddress()[0]);
		dlpOnBoardVolt.setChNames(cmdVolt.getChNames()[0]);
		dlpOnBoardVolt.setDelimiter(cmdVolt.getDelimiter()[0]);
		dlpOnBoardVolt.setdTypes(cmdVolt.getDTypes()[0]);
		dlpOnBoardVolt.setMIMEs(cmdVolt.getMIMEs()[0]);
		dlpOnBoardVolt.setNumParameter(cmdVolt.getChNames()[0].length);
		dlpOnBoardVolt.setUnits(cmdVolt.getUnits()[0]);
		dlpOnBoardVolt.start();

		// Vaisela Weather Station
		DataLineProcessor_x dlpVWS = new DataLineProcessor_x(this, /*"VWS"*/ Configurator.VWS);//modified by pstango
		CommandList cmdVWS = conf.createVWSCmdList(/*"VWS"*/ Configurator.VWS);//modified by pstango
		dlpVWS.setAddressAndPort(cmdVWS.getDTAddress()[0]);
		dlpVWS.setChNames(cmdVWS.getChNames()[0]);
		dlpVWS.setDelimiter(cmdVWS.getDelimiter()[0]);
		dlpVWS.setdTypes(cmdVWS.getDTypes()[0]);
		dlpVWS.setMIMEs(cmdVWS.getMIMEs()[0]);
		dlpVWS.setNumParameter(cmdVWS.getChNames()[0].length);
		dlpVWS.setUnits(cmdVWS.getUnits()[0]);
		dlpVWS.start();

		dlpList.add(dlpSolarIR);
		dlpList.add(dlpFSM);
		dlpList.add(dlpOnBoardTemp);
		dlpList.add(dlpOnBoardHumi);
		dlpList.add(dlpOnBoardVolt);
		dlpList.add(dlpVWS);

		return dlpList;
	}

	/*
	 * public boolean isDTconnAlive() { Log.i(TAG,
	 * "The connection with RBNB si not open"); // maybe this method is not
	 * needed
	 * 
	 * return false; }
	 */
	public void call(String str) {
		Log.i(TAG, "A string was received from the activity. String: " + str);
	}

	private void msgToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		// log.writelnT(msg);
	}

	// -----------------------------------------------------------------------------

	public void stopService() {
		if (isRunningService) {
			isRunningService = false;
			Log.w(getClass().getName(), "Got to stop()!");
			stopForeground(true);
			msgToast("Stoped service on stop()");
		}
	}

	@Override
	public void onDestroy() {
		stopService();
		Toast.makeText(this, "service done onDestroy", Toast.LENGTH_LONG)
				.show();
		Log.i(TAG, "Service donde onDestroy().");
	}

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		DataLineProcessor_Service getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return DataLineProcessor_Service.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

}