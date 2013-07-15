/**
 * DTSrcInsert.java
 * 
 * Insert the data into a DT using the open channels
 * If there is an error on the connection or flushing the data, the dtError
 *  handler will be call from the DataLineProcessor_x
 * 
 * @author Peter Shin, Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.android.rbnb;

import org.cleos.android.lib.Write2File;
//import org.cleos.ntl.datalineprocessor4remotedt.DataLineProcessor_x;

import com.rbnb.sapi.Source;
import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.SAPIException;

import android.content.Context;
import android.util.Log;

public class DTSrcInserter {
	private String TAG = "DTSrcInserter 4RDT";
	//private Context context;
	//private DataLineProcessor_x dlp_x;

	private Write2File log;

	private Source src;
	private ChannelMap chMap;
	private String[] dataItems;
	private int[] intDataType;

	private String[] chNames;
	private String[] units;
	private String[] MIMEs;

	public void insert(Context context, /*DataLineProcessor_x dlp_x,*/
			Write2File log, Source src, String[] dataItems, int[] intDataType,
			String[] chNames, String[] units, String[] MIMEs) {
		//this.context = context;
		//this.dlp_x = dlp_x;
		this.log = log;
		this.src = src;

		this.dataItems = dataItems;
		this.intDataType = intDataType;

		this.chNames = chNames;
		this.units = units;
		this.MIMEs = MIMEs;

		new FlushData().start();
	}

	private class FlushData extends Thread{

		@Override
		public void run() {
			if (chNames != null) {
				try {
					chMap = new ChannelMap();
					for (String chNameTemp : chNames)
						chMap.Add(chNameTemp);
					// putUnits(units);
					// putMIMEs(MIMEs);
				} catch (SAPIException e) {
					Log.e(TAG,
							"Error creating the chanel map. " + e.getMessage());
					log.writelnT("Error creating the chanel map. "
							+ e.getMessage());
					Log.i(TAG, "****Calling the dtErrorHandler!!!!");
					//dlp_x.dtErrorHandler();
					return ;
				}

				chMap.PutTimeAuto("server");

				int i = 0;

				try {

					for (i = 0; i < dataItems.length; i++) {
						putData(i, dataItems[i], intDataType[i]);
						// Log.d("FlushData AsyncTask", "putting data: " +
						// dataItems[i] );
					}
					flushToServer();

				} catch (SAPIException e) {
					Log.e(TAG,
							"Error inserting and flushing the data to RBNB server. "
									+ e.getMessage());
					log.writelnT("Error inserting and flushing the data to RBNB server. "
							+ e.getMessage());
					Log.i(TAG, "****Calling the dtErrorHandler!!!!");
					//dlp_x.dtErrorHandler();
				} catch (NumberFormatException ne) {
					Log.e(TAG,
							"Error in parsing the data when inserting the data to Remote RBNB server. "
									+ ne.getMessage());
					log.writelnT("Error in parsing the data when inserting the data to Remote RBNB server.  "
							+ ne.getMessage());
					Log.i(TAG, "****The channel with bad data " + chNames[i]);
				}
			}
			return ;
		}

	}

	// This method sends the accumulated data in the channel map
	// to the server
	private void flushToServer() throws SAPIException {
		if (this.chMap != null && this.src != null) {
			int flushedChCount = this.src.Flush(this.chMap);
			Log.d(this.TAG, "Flushed " + flushedChCount
					+ " many channels to the Remote DT server");
		} else {
			Log.i(TAG,
					"the channel map or the source is null - nothing to flush.");
		}
	}

	public void putData(int chIndex, String str, int dType)
			throws SAPIException, NumberFormatException {
		if (str == null)
			return;
		if (str == "")
			return;

		switch (dType) {
		case ChannelMap.TYPE_FLOAT32:
			float fData = Float.parseFloat(str);
			this.insertDataToDT(chIndex, fData);
			break;
		case ChannelMap.TYPE_FLOAT64:
			double dData = Double.parseDouble(str);
			this.insertDataToDT(chIndex, dData);
			break;
		case ChannelMap.TYPE_INT32:
			int iData = Integer.parseInt(str);
			this.insertDataToDT(chIndex, iData);
			break;
		case ChannelMap.TYPE_INT64:
			long lData = Long.parseLong(str);
			this.insertDataToDT(chIndex, lData);
			break;
		case ChannelMap.TYPE_STRING:
			this.insertDataToDT(chIndex, str);
			break;
		}
	}

	public void insertDataToDT(String chName, double data) throws SAPIException {
		this.insertDataToDT(chMap.GetIndex(chName), data);
		// Log.d("FlushData AsyncTask", "insertDataToDT channel: "+ chName
		// +" data: "+ Double.toString(data) );;
	}

	public void insertDataToDT(String chName, int data) throws SAPIException {
		this.insertDataToDT(chMap.GetIndex(chName), data);
		// Log.d("FlushData AsyncTask", "insertDataToDT channel "+ chName
		// +" data: "+ Integer.toString(data) );;
	}

	public void insertDataToDT(String chName, long data) throws SAPIException {
		this.insertDataToDT(chMap.GetIndex(chName), data);
		// Log.d("FlushData AsyncTask", "insertDataToDT channel "+ chName
		// +" data: "+ Long.toString(data) );;
	}

	public void insertDataToDT(String chName, String data) throws SAPIException {
		this.insertDataToDT(chMap.GetIndex(chName), data);
		// TODO
	}

	public void insertDataToDT(int chMapIndex, float data) throws SAPIException {
		float[] floatArray = new float[1];
		floatArray[0] = data;
		this.chMap.PutDataAsFloat32(chMapIndex, floatArray);
		// Log.d("FlushData AsyncTask", "insertDataToDT channel index = "+
		// chMapIndex +" data: "+ Float.toString(data) );;

	}

	public void insertDataToDT(int chMapIndex, double data)
			throws SAPIException {
		double[] doubleArray = new double[1];
		doubleArray[0] = data;
		this.chMap.PutDataAsFloat64(chMapIndex, doubleArray);
		// Log.d("FlushData AsyncTask", "insertDataToDT channel index = "+
		// chMapIndex +" data: "+ Double.toString(data) );;
	}

	public void insertDataToDT(int chMapIndex, int data) throws SAPIException {
		int[] intArray = new int[1];
		intArray[0] = data;
		this.chMap.PutDataAsInt32(chMapIndex, intArray);
		// Log.d("FlushData AsyncTask", "insertDataToDT channel index = "+
		// chMapIndex +" data: "+ Integer.toString(data) );;
	}

	public void insertDataToDT(int chMapIndex, long data) throws SAPIException {
		long[] longArr = new long[1];
		longArr[0] = data;
		this.chMap.PutDataAsInt64(chMapIndex, longArr);
		// Log.d("FlushData AsyncTask", "insertDataToDT channel index = "+
		// chMapIndex +" data: "+ Long.toString(data) );;
	}

	public void insertDataToDT(int chMapIndex, String data)
			throws SAPIException {
		this.chMap.PutDataAsString(chMapIndex, data);
		// TODO
	}

	public void putUnits(String[] units) throws SAPIException {
		for (int i = 0; i < units.length; i++) {
			this.chMap.PutUserInfo(i, "units = " + units[i]);
		}
	}

	public void putMIMEs(String[] MIMEs) {
		for (int i = 0; i < MIMEs.length; i++) {
			this.chMap.PutMime(i, MIMEs[i]);
		}
	}
}
