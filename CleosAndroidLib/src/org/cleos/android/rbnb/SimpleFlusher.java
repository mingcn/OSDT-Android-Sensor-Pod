package org.cleos.android.rbnb;

import org.cleos.android.lib.Utils;
import org.cleos.android.lib.Write2File;

import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.Control;
import com.rbnb.sapi.SAPIException;
import com.rbnb.sapi.Sink;

import android.content.Context;
import android.util.Log;

public class SimpleFlusher {

	private String TAG = getClass().getSimpleName();
	
	private Context context;
	
	private String name;
	private DTSrcManager dtSrc;
	private Write2File log = new Write2File("Accelerometer", TAG, true);
	
	private String addressAndPort;
	private String[] chNames;
	private String[] dTypes;
	private String[] units;
	private String[] MIMEs;
	
	
	public SimpleFlusher(String name, Context context, String addressAndPort, Write2File log){
		this.name = name;
		this.context = context;
		this.addressAndPort = addressAndPort;
		this.log = log;
	}
	
	public void flushData(String[] data){
		this.dtSrc.insertData(data);
	}
	
	public synchronized void createConnections() {
		Log.i(TAG,
				"The source will be created and the RBNB 4RDT connection will be opened.");
		createDTSrc();
		openRBNBConnection();
		declareChInfo();
	}

	private void stopSrcCloseRBNBConnection() {
		Log.i(TAG,
				"The source will be stoped and the RBNB 4RDT connection will be closed.");

		stopDTSrc();
	}

	// ------- when create or any problem --------
	/**
	 * the correct order is set values or use methods with parameters
	 * createDTSrc openRBNBconnection declareChInfo
	 */
	public void createDTSrc() {
		this.dtSrc = new DTSrcManager(this.context, log);
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
		this.dtSrc = new DTSrcManager(this.context, this.log);
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


	public void dtErrorHandler() {
		Log.i(TAG, "****On dtErrorHandler (4RDT).");

		synchronized (this) {
			if (!Utils.isLockService(context)) {
				Utils.lockService(context, true); // setLockDLP_x(true);
				Log.i(TAG,
						"dtErrorHandler() -- obtained and set the lock (4RDT)");
			} else {
				Log.i(TAG, "The DLP_x 4RDT is LOCKED.");
				return;
			}
		}

		terminateChannelInServer();
		waitUntilClean();

		createConnections();
	}

	private void waitUntilClean() {
		Utils.wait(5000);
	}

	public boolean terminateChannelInServer() {
		if (addressAndPort != null && name != null) {
			Control control = new Control();
			Sink sink = new Sink();
			ChannelMap cm = new ChannelMap();
			ChannelMap fetchedCm;
			try {
				sink.OpenRBNBConnection(addressAndPort, name + "Sink");
				cm.Add(name + "/*");
				sink.RequestRegistration(cm);
				fetchedCm = sink.Fetch(5000);
				if (fetchedCm.GetChannelList() != null) {
					if (fetchedCm.GetChannelList().length > 0) {
						control.OpenRBNBConnection(addressAndPort,
								"DLP4RDTControl_" + name);
						control.Terminate(name);
						control.CloseRBNBConnection();
						Log.e(TAG, "Source Client: " + name + " is terminated");
						log.writelnT("Source Client: " + name
								+ " is terminated");
					} else {
						Log.e(TAG, "Source Client doesn't exist");
						log.writelnT("Source Client doesn't exist");
					}
				}
			} catch (SAPIException e) {
				Log.e(TAG, "Error in Control Client: " + addressAndPort
						+ " Terminate: " + name + " Close Control", e);
				log.writelnT("Error in Control Client: " + addressAndPort
						+ " Terminate: " + name + " Close Control"
						+ e.getMessage());
			}

			return true;
		}
		Log.e(TAG, "No address and no name");
		return false;
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

//	public void setDelimiter(String delimiter) {
//		this.delimiter = delimiter;
//		this.parser = new DataParser(delimiter);
//	}
//
//	public void setNumParameter(int numParameters) {
//		this.numParameters = numParameters;
//	}

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
