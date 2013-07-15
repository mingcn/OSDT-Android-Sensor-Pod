/**
 * Constats.java
 * 
 * @author Gesuri Ramirez
 * @Date August 2012
 */

package org.cleos.android.lib;

public class Constants {
	/**
	 * Broadcasts
	 * */
	// for the RBNB server
	public static final String BROADCASTRECEIVER_RESTART_RBNB = "org.cleos.RBNB.broadcastreceiver.RESTART_RBNB";
	public static final String BROADCASTRECEIVER_ERROR_RBNB = "org.cleos.RBNB.broadcastreceiver.ERROR_RBNB"; // not
																												// used
																												// anymore
	public static final String BROADCASTRECEIVER_RBNB_LOCK = "org.cleos.RBNB.broadcastreceiver.LOCK";
	public static final String BROADCASTRECEIVER_RBNB_UNLOCK = "org.cleos.RBNB.broadcastreceiver.UNLOCK";

	// for the DataLineProcessor
	public static final String BROADCASTRECEIVER_DLP_RESTART = "org.cleos.ntl.datalineprocessor.broadcastreceiver.RESTART";
	public static final String BROADCASTRECEIVER_DLP_PROCESSDATALINE = "org.cleos.ntl.datalineprocessor.broadcastreceiver.PROCESSDATALINE";
	public static final String BROADCASTRECEIVER_DLP_LOCK = "org.cleos.ntl.datalineprocessor.broadcastreceiver.LOCK";
	public static final String BROADCASTRECEIVER_DLP_UNLOCK = "org.cleos.ntl.datalineprocessor.broadcastreceiver.UNLOCK";
	public static final String BROADCASTRECEIVER_DLP_STOP = "org.cleos.ntl.datalineprocessor.broadcastreceiver.STOP";

	// for the DataLineProcessor4RemoteDT
	public static final String BROADCASTRECEIVER_DLP4RDT_RESTART = "org.cleos.ntl.datalineprocessor4remotedt.broadcastreceiver.RESTART";
	public static final String BROADCASTRECEIVER_DLP4RDT_PROCESSDATALINE = "org.cleos.ntl.datalineprocessor4remotedt.broadcastreceiver.PROCESSDATALINE";
	public static final String BROADCASTRECEIVER_DLP4RDT_LOCK = "org.cleos.ntl.datalineprocessor4remotedt.broadcastreceiver.LOCK";
	public static final String BROADCASTRECEIVER_DLP4RDT_UNLOCK = "org.cleos.ntl.datalineprocessor4remotedt.broadcastreceiver.UNLOCK";
	public static final String BROADCASTRECEIVER_DLP4RDT_STOP = "org.cleos.ntl.datalineprocessor4remotedt.broadcastreceiver.STOP";

	/**
	 * Tags
	 */
	public static final String LOCK_RBNB_SERVICE_FLAG_FILE = "lockRBNBService.flag"; // lock
																						// file
	public static final String LOCK_DLP_FLAG_FILE = "lockDLPService.flag"; // lock
																			// file
	// counter file that counts the number of retrieves while the service is
	// locked
	public static final String COUNTER_LOCKS_FILE = "counterLocks.integer";
	public static final int LOCK = 7; // means lock and it is on the file
	public static final int UNLOCK = 5; // means unlock and it is on the file

	// for the DataLineProcessor
	public static final String SLC_NAME = "SLC_name"; // tag with the name of
														// the serial line
														// controller (e.g.
														// Sonde)
	public static final String DATALINE = "dataLine"; // tag for the data

	// keyname for intents
	public static final String REMOTE_CALL = "remoteCall"; // for a boolean
															// value
	public static final String CALLED_FROM = "calledFrom"; // for a byte value
	public static final String ACTION = "action"; // for a byte value

	// values for intents
	// CALLED_FROM
	// not used anymore
	public static final byte ACTIVITY = 99;
	public static final byte CONTROLLER = 98;
	public static final byte BOOT = 97;
	public static final byte SOURCE = 96;
	public static final byte MONITOR = 95;
	// ACTION
	public static final byte STOP = 0; // stop the service
	public static final byte START = 1; // start the service
	public static final byte RESTART = 2; // stop and then start the service

	public static String getString(byte value) {
		String str = null;
		switch (value) {
		case ACTIVITY:
			str = "Activity";
			break;
		case CONTROLLER:
			str = "Controller";
			break;
		case BOOT:
			str = "Boot";
			break;
		case SOURCE:
			str = "Source";
			break;
		case MONITOR:
			str = "Monitor";
			break;
		case STOP:
			str = "Stop";
			break;
		case START:
			str = "Start";
			break;
		case RESTART:
			str = "Restart";
			break;
		}
		return str;
	}
}
