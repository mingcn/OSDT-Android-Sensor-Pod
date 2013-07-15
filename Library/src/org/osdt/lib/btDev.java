
/*
   Copyright 2011 Erigo Technologies LLC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/******************************************************************************
 * Bluetooth Serial Device Interface
 * <p>
 * Includes open, read, write, close methods
 * <p>
 *
 * @author Matthew J. Miller
 *
 * @version 05/31/2011
 */

/*
 * Copyright 2011 Erigo Technologies LLC
 * All Rights Reserved
 *
 *   Date      By	Description
 * MM/DD/YYYY
 * ----------  --	-----------
 * 04/14/2011  MJM	Created.
 * 05/31/2011  JPW	Add ability to do buffered reads.  Note, however, that
 * 			the user cannot mix buffered and non-buffered reads.
 * 			Once they have switched over to buffered reads, they
 * 			need to keep with the buffered reads.
 * 08.26/2011  MJM  Add btAvailable() for async check of input
 */

package org.osdt.lib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

public class btDev  {
    private final UUID SerialPortServiceClass_UUID = 
	UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final String TAG = "btDev";
    private boolean status = false;			// true/false, running/not
    private String address = "";

    // Member fields
    private InputStream btInStream;
    private OutputStream btOutStream;
    private BluetoothSocket btSocket;
    // For performing buffered reads
    private InputStreamReader bfIn = null;
    private BufferedReader buReader = null;
    // Performing buffered reads?
    private boolean bBuffRead = false;

    private static BluetoothAdapter adapter=null;		// expose to external so can initialize outside of thread?

    // constructor
    public  btDev() throws IOException {
	// Make sure the adapter has been initialized
	if (adapter == null) {
	    throw new IOException("Must initialize Bluetooth by calling btDev.btInit() on the system thread.");
	}
    }

    //-------------------------------------------------------------------------------
    // setup connection and start connection-handling thread, enable adapter as nec
    public static void btInit() {
	if(adapter == null) adapter = BluetoothAdapter.getDefaultAdapter();

	for(int i=0; i<10; i++) {
	    if (!adapter.isEnabled()) adapter.enable();	// supposed to ask permission but hay
	    else break;
	    try { Thread.sleep(1000); } catch(Exception e){};
	}
	Log.d(TAG,"btInit: "+adapter);
    }

    public BluetoothSocket btOpen() throws IOException {
	return(btOpen(address));		// re-open with prior address
    }

    public BluetoothSocket btOpen(String btaddress) throws IOException {	
	BluetoothSocket socket;
	BluetoothDevice device;
	try {
	    btClose();			// close before (re)open

	    address = new String(btaddress);

	    if(adapter == null) adapter = BluetoothAdapter.getDefaultAdapter();
	    if (adapter == null) {
		Log.e(TAG,"BT getDefaultAdapter failed!");
		throw new IOException("BT getDefaultAdapter failed");
	    }

	    // check if BT enabled, if not warn user and bail (no human-interaction side effects)
	    if (!adapter.isEnabled()) {
		Log.e(TAG,"BT not enabled!");
		throw new IOException("BT not enabled");
	    }
	} catch(IOException e) {
	    Log.e(TAG,"Unexpected exception on btOpen: "+e);
	    throw e;
	}
	
	// Always cancel discovery because it will slow down a connection
	// if (adapter.isDiscovering()) {
	    adapter.cancelDiscovery();	// does this mess up multi-threaded device opens?
	// }
	
	// Get BluetoothSocket for connection with given BluetoothDevice
	try {
	    device = adapter.getRemoteDevice(address);
	    socket = device.createRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);
	    socket.connect();
	    btSocket = socket;
	} catch (IOException e) {
	    socket = null;
	    Log.e(TAG, "BT socket connection failed", e);
	    throw e;
	}
	
	// Get the BluetoothSocket input and output streams
	try {
	    btInStream = socket.getInputStream();
	    btOutStream = socket.getOutputStream();
	} catch (IOException e) {
	    Log.e(TAG, "Bluetooth streams not created", e);
	    throw e;
	}

	status = true;		// running
	Log.d(TAG,"btOpen: "+address);
	return(socket);
    }

    //------------------------------------------------------------------------
    // Write to the connected OutStream. 
    public void btWrite(byte[] buffer) throws IOException {
	btWrite(buffer, 0);
    }

    public void btWrite(String strBuffer) throws IOException {
	if ( (strBuffer != null) && (strBuffer.length() != 0) ) {
	    this.btWrite(strBuffer.getBytes());
	}
    }

    public void btWrite(byte[] buffer, long retryTime) throws IOException {
	// Log.d(TAG,"btWrite: "+new String(buffer));
	if(!status) throw(new IOException("attempt to write to closed BT socket"));

	try {
	    btOutStream.write(buffer);
	} catch (IOException e) {
	    Log.e(TAG, "Exception during write to "+address, e);
	    if(retryTime > 0) {			// auto-reconnect?
		btRestart(retryTime);
		try {
		    btOutStream.write(buffer);
		} catch (IOException e1) { throw(e); };
	    }
	    else {
		btClose();
		throw(e);
	    }
	}
    }

    public void btWrite(String strBuffer, long retryTime) throws IOException {
	if ( (strBuffer != null) && (strBuffer.length() != 0) ) {
	    this.btWrite(strBuffer.getBytes(),retryTime);
	}
    }

    //------------------------------------------------------------------------
    // Read from the connected OutStream.
    public byte[] btRead() throws IOException {
	if (bBuffRead) {
	    throw new IOException("Must use the buffered read call, btBufferedRead().");
	}
	return(btRead(0));
    }
    
    public String btReadString() throws IOException {
	if (bBuffRead) {
	    throw new IOException("Must use the buffered read call, btBufferedRead().");
	}
	String newStr = btReadString(0);
	return newStr;
    }
    
    public String btReadString(long retryTime) throws IOException {
	if (bBuffRead) {
	    throw new IOException("Must use the buffered read call, btBufferedRead().");
	}
	byte[] btbuffer = btRead(retryTime);
	String newStr = new String();
	if ( (btbuffer != null) && (btbuffer.length != 0) ) {
	    newStr = new String(btbuffer);
	}
	return newStr;
    }

    public byte[] btRead(long retryTime) throws IOException {
	
	if (bBuffRead) {
	    throw new IOException("Must use the buffered read call, btBufferedRead().");
	}
	
	if (!status) {
	    throw(new IOException("attempt to read from closed BT socket"));
	}

	byte[] buffer = new byte[65536];
	int bytes;
	try {
	    bytes = btInStream.read(buffer);
	} catch (IOException e) {
	    Log.e(TAG, "Exception during read from "+address, e);
	    if(retryTime > 0) {			// auto-reconnect?
		btRestart(retryTime);
		try {
		    bytes = btInStream.read(buffer);
		} catch (IOException e1) { throw(e); };
	    }
	    else {
		btClose();
		throw(e);
	    }
	}

	byte[] trimbuffer = new byte[bytes];
	System.arraycopy(buffer, 0, trimbuffer, 0, bytes);

	// Log.d(TAG,"btRead: "+new String(trimbuffer));
	return trimbuffer;
    }
    
    // Perform buffered read of the input stream
    // NOTE: Once you have started doing a buffered read, I don't
    //       think you can go back and read from the input stream
    //       directly.  Thus the use of the "bBuffRead" boolean.
    public String btBufferedRead() throws IOException {
	if (buReader == null) {
	    // The buffered reader hasn't been initialized yet
	    bfIn = new InputStreamReader (btInStream);
	    buReader = new BufferedReader (bfIn);
	}
	bBuffRead = true;
	String line = buReader.readLine();
	return line;
    }
    
    //------------------------------------------------------------------------
    // close the BT device (important to release for others to use)
    public void btClose() {
	
	// Log.d(TAG,"btClose, status: "+status);
	
	if(!status) return; // already closed
	
	status = false;
	bBuffRead = false;
	try {
	    btSocket.close();
	    if (buReader != null) {
		// Close the buffered reader
		buReader.close();
	    }
	} catch (Exception e) { Log.d(TAG,"Failed to close socket for "+address);};	// walk away
	
	Log.d(TAG,"btClose: "+address);
    }

    //------------------------------------------------------------------------
    //  public access methods
    public boolean getStatus() {
	return(status);
    }

    public String getAddress() {
	return(address);
    }
    //------------------------------------------------------------------------
    // try to re-open a lost connection (for tryTime seconds)
    private void btRestart(long tryTime) {	
	if(!status) return;		// restart only if nominally still open

	long startTime = System.currentTimeMillis();
	long mtryTime = tryTime * 1000;		// convert sec to msec
	btClose(); // make sure it's closed

	while((tryTime<0) || 	// -1 means try forever
		((System.currentTimeMillis()-startTime)<mtryTime) ) {
	    try {
		Thread.sleep(1000); // 1 sec poll
		Log.d(TAG,"Attempting reconnect...");
		btOpen();
		if(status) break;
	    } catch(Exception e){};
	}
	Log.d(TAG,"Yay! Reconnected.");
    }

    //------------------------------------------------------------------------
    // return available bytes on input stream (for non-blocking I/O)
    public int btAvailable() {
    	try {
    		return(btInStream.available());
    	} catch(Exception e) { return 0; }
    }
}
