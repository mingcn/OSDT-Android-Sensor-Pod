/**
 * RxThread.java
 * 
 * Read the serial port
 * then, the data is stored in the phone and send to the DataLineProcessors  
 * 
 * @author Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.android.ntl.datagather;

import java.io.DataInputStream;
import java.io.InputStream;

//import org.cleos.android.lib.Constants;
import org.cleos.android.lib.Write2File;
import org.cleos.android.ntl.broadcasts.SendBroadcast;

import android.content.Context;
//import android.content.Intent;
import android.util.Log;

class RxThread extends Thread {
	private Context context;
	private String slcName;
	private boolean abort_ = false;
	private InputStream in;
	private DataInputStream dIn;
	private Write2File log;
	private Write2File datalog;

	public RxThread(Context context, String slcName, InputStream in,
			Write2File datalog, Write2File log) {
		this.slcName = slcName;
		this.context = context;
		this.in = in;
		this.datalog = datalog;
		this.log = log;
		this.dIn = new DataInputStream(this.in);
	}

	@Override
	public void run() {
		super.run();
		log.writelnT("RxThread started!");
		while (true) {
			synchronized (this) {
				if (abort_) {
					break;
				}
			}
			try {
				while (true) {
					if (in.available() > 0) {
						String dl = dIn.readLine();
						if (dl.length() > 1) {
							datalog.writelnT(dl);
							SendBroadcast.sendData2DLP(context, slcName, dl);
							SendBroadcast
									.sendData2DLP4RDT(context, slcName, dl);
						}
					}
					if (abort_)
						break;
					sleep(10);
				}
			} catch (Exception e) {
				log.writelnT("Unexpected exception caught" + e.toString());
				Log.e("RxThread", "Unexpected exception caught", e);
				break;
			}
		}

	}// end run()

	synchronized public void abort() {
		abort_ = true;
	}

}// end class RxThread