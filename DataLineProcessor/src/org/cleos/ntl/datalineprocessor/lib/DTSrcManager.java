/**
 * DTSrcManager.java
 * 
 * Manager for the DT source
 * 
 * @author Peter Shin, Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.ntl.datalineprocessor.lib;

import org.cleos.android.lib.Write2File;
import org.cleos.ntl.datalineprocessor.DataLineProcessor_x;

import android.content.Context;

import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.Source;


public class DTSrcManager {

	Source src;
	ChannelMap chMap;
	int[] intDataType;
	String[] dataType;
	Context contex;
	private DataLineProcessor_x dlp_x;
	private Write2File log;

	private String[] chNames;
	private String[] units;
	private String[] MIMEs;

	// ----- DT Client & Source related operation
	public DTSrcManager(Context context, DataLineProcessor_x dlp_x,
			Write2File log) {
		this.contex = context;
		this.dlp_x = dlp_x;
		this.log = log;
		this.src = new Source(1, "append", 10000000);
	}

	DTSrcManager(Context context, Write2File log, int cache, String mode,
			int archive) {
		this.contex = context;
		this.src = new Source(cache, mode, archive);
	}

	public void connectToDT() {
		String address = "localhost:3333";
		String clientName = "DTSrc";
		this.connectToDT(address, clientName);
	}

	public void connectToDT(String address, String name) {
		new RBNBConnectHelper().connectToDT(this.contex, this.log, this.src,
				address, name);
	}

	public void detachSrc() {
		if (this.src != null)
			this.src.Detach();
		this.src = null;
	}

	public void closeRBNBConnection() {
		if (this.src != null)
			this.src.CloseRBNBConnection();
	}

	public void clear() {
		this.src = null;
	}

	public boolean isDTConnectionAlive() {
		return this.src.VerifyConnection();
	}

	/*
	 * // ----- DT ChannelMap related operations
	 * 
	 * // this method creates the following: // a new channel map with proper
	 * cheannel names // an array of int for data type defined by
	 * com.rbnb.sapi.ChannelMap // // "int8", "int16", "int32", "int64"
	 * "float32", "float64", // "string", "bytearray", or "unknown". // Assume
	 * that the chNames and the dataTypes have the same length
	 */
	public void createChMap(String[] chNames, String[] dTypes, String[] units,
			String[] MIMEs) {

		this.chMap = new ChannelMap();

		this.chNames = chNames;
		this.units = units;
		this.MIMEs = MIMEs;

		this.createIntTypeArr(dTypes);
	}

	public void insertData(String[] dataItems) {

		new DTSrcInserter().insert(this.contex, this.dlp_x, this.log, this.src,
				dataItems, this.intDataType, this.chNames, this.units,
				this.MIMEs);
	}

	private void createIntTypeArr(String[] dType) {
		this.intDataType = new int[dType.length];

		for (int i = 0; i < dType.length; i++) {
			this.intDataType[i] = this.chMap.TypeID(dType[i]);
		}
	}

}