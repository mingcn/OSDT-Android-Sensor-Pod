/**
 * DataLineProcessor_x.java
 * 
 * Create the connection to DT and create the channel and subchannels for the
 *  sensor
 * If for any reason there is not created the connection and channels, there is
 *  a method to deal with the problem
 * It will close connection, open connection, and probably restart the DT
 *  server 
 * 
 * @author Gesuri Ramirez, Peter Shin
 * @date August 2012
 */

package org.cleos.ntl.datalineprocessor;

import org.cleos.android.lib.Constants;
import org.cleos.android.lib.Utils;
import org.cleos.android.lib.Write2File;
import org.cleos.ntl.datalineprocessor.lib.DTSrcManager;
import org.cleos.ntl.datalineprocessor.lib.DataParser;

import android.content.Context;
import android.util.Log;

public class DataLineProcessor_x extends Thread {
	private String TAG = getClass().getSimpleName();

	private String name;
	private DTSrcManager dtSrc;
	private DataParser parser;
	private Context context;

	private Write2File log;

	// for DT
	private String addressAndPort;
	private String delimiter;
	private int numParameters;
	private String[] chNames;
	private String[] dTypes;
	private String[] units;
	private String[] MIMEs;

	public DataLineProcessor_x(Context context, String name) {
		this.context = context;
		this.name = name;

		log = new Write2File("DataLineProcessor/" + TAG + "/" + name + "/", TAG
				+ ".txt", true);
		this.parser = new DataParser();
	}

	@Override
	public void run() {
		Log.i(TAG, "Started DLP_x: " + name);
		createConnections();
	}

	private synchronized void createConnections() {
		Log.i(TAG,
				"The source will be created and the RBNB connection will be opened.");
		createDTSrc();
		openRBNBConnection();
		declareChInfo();
	}

	private void stopSrcCloseRBNBConnection() {
		Log.i(TAG,
				"The source will be stoped and the RBNB connection will be closed.");

		stopDTSrc();
	}

	// ------- when create or any problem --------
	/**
	 * the correct order is set values or use methods with parameters
	 * createDTSrc openRBNBconnection declareChInfo
	 */
	public void createDTSrc() {
		this.dtSrc = new DTSrcManager(this.context, this, log);
	}

	private void closeRBNBConnection() {
		Log.i(TAG, "The RBNB connection will be closed.");
		this.dtSrc.closeRBNBConnection();
	}

	private void stopDTSrc() {
		Log.i(TAG, "The source will be stoped.");
		this.dtSrc.detachSrc();
	}

	public boolean openRBNBConnection() {
		if (addressAndPort != null && name != null) {
			this.dtSrc.connectToDT(addressAndPort, name);
			return true;
		}
		return false;
	}

	public void openRBNBconnection(String address, String name) {
		this.addressAndPort = address;
		this.name = name;
		this.dtSrc = new DTSrcManager(this.context, this, this.log);
		this.dtSrc.connectToDT(address, name);
	}

	public boolean declareChInfo() {
		if (chNames != null && dTypes != null && units != null && MIMEs != null) {
			this.dtSrc.createChMap(chNames, dTypes, units, MIMEs);
			return true;
		} else
			return false;
	}

	public void declareChInfo(String[] chNames, String[] dTypes,
			String[] units, String[] MIMEs) {
		this.chNames = chNames;
		this.dTypes = dTypes;
		this.units = units;
		this.MIMEs = MIMEs;
		this.dtSrc.createChMap(chNames, dTypes, units, MIMEs);
	}

	// --------- DataLineProcessor:: parser and insertingDT--------

	public void processDataLine(String dataLine) {
		// Log.i(TAG, "Delimiter: " + delimiter);
		
		if(dataLine.charAt(1) == '-')
			dataLine = dataLine.substring(2);
		
		if(dataLine.charAt(0) == 'D')
		{
			if(dataLine.charAt(1) == 'A')
				dataLine = dataLine.substring(8);
		}
		
		String[] dataItems = this.parser.getStringData(dataLine, delimiter);
		if (dataItems.length != numParameters) {
			
			Log.e(TAG, "Error in parsing data line: " + dataLine
					+ ". Parsered data: " + dataItems.toString());
			log.writelnT("Error in parsing data line: " + dataLine
					+ ". Parsered data: " + dataItems.toString());			
		} else {
			this.dtSrc.insertData(dataItems);
			// if something fails in inseertData, it'll call dtErrorHandler
		}
	}

	public synchronized void dtErrorHandler() {
		String lockFile = Constants.LOCK_DLP_FLAG_FILE;
		Log.i(TAG, "****On dtErrorHandler.");
		if (!Utils.isLockService(context, lockFile)) {
			Utils.lockService(context, lockFile, true);
			stopDTSrc();
			waitUntilClean();
			createConnections();

		} else {
			Log.i(TAG, "The DLP service is LOCK.");
		}
	}

	private void waitUntilClean() {
		Utils.wait(5000);
	}

	// -------------------- kill -------------------
	public void kill() {
		Log.d(TAG, name + "::kill()");
		stopSrcCloseRBNBConnection();
	}

	public boolean isDTConnectionAlive() {
		return this.dtSrc.isDTConnectionAlive();
	}

	// -------------------- setters ------------------
	public void setAddressAndPort(String ipp) {
		this.addressAndPort = ipp;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
		this.parser = new DataParser(delimiter);
	}

	public void setNumParameter(int numParameters) {
		this.numParameters = numParameters;
	}

	public void setChNames(String[] chNames) {
		this.chNames = chNames;
	}

	public void setdTypes(String[] dTypes) {
		this.dTypes = dTypes;
	}

	public void setUnits(String[] units) {
		this.units = units;
	}

	public void setMIMEs(String[] mIMEs) {
		MIMEs = mIMEs;
	}

	public String getDLPName() {
		return this.name;
	}

}
