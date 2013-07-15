/**
 * SendBroadcast.java
 * 
 * Send broadcast to different modules
 * 
 * @author Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.android.ntl.broadcasts;

import org.cleos.android.lib.Constants;

import android.content.Context;
import android.content.Intent;

public class SendBroadcast {
	/**
	 * For DT service
	 * 
	 * @param context
	 */
	// send broadcast with the call to restart the RBNB server. if service is
	// stopped, it stated
	public static void restartRBNB(Context context) {
		Intent intent = new Intent();
		intent.setAction(Constants.BROADCASTRECEIVER_RESTART_RBNB);
		intent.putExtra(Constants.ACTION, Constants.RESTART);
		context.sendBroadcast(intent);
	}

	// to startDTService --- DEPRECATED ---
	public static void startDTService(Context context) {
		String broadcast = Constants.BROADCASTRECEIVER_RESTART_RBNB;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		intent.putExtra(Constants.ACTION, Constants.START);
		context.sendBroadcast(intent);
	}

	// to stop DT service
	public static void stopDTService(Context context) {
		String broadcast = Constants.BROADCASTRECEIVER_RESTART_RBNB;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		intent.putExtra(Constants.ACTION, Constants.STOP);
		context.sendBroadcast(intent);
	}

	// to lock DT service
	public static void lockDTservice(Context context) {
		String broadcast = Constants.BROADCASTRECEIVER_RBNB_LOCK;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		context.sendBroadcast(intent);
	}

	// to unlock DT service
	public static void unlockDTservice(Context context) {
		String broadcast = Constants.BROADCASTRECEIVER_RBNB_UNLOCK;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		context.sendBroadcast(intent);
	}

	// -----------------------------------------------------------------------------
	/**
	 * for DataLineProcessor
	 * 
	 * @param context
	 */
	// to lock DLP service
	public static void lockDLPService(Context context) {
		String broadcast = Constants.BROADCASTRECEIVER_DLP_LOCK;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		context.sendBroadcast(intent);
	}

	// to unlock DLP service
	public static void unlockDLPService(Context context) {
		String broadcast = Constants.BROADCASTRECEIVER_DLP_UNLOCK;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		context.sendBroadcast(intent);
	}

	// to restart DLP
	public static void restartDLP(Context context) {
		String broadcast = Constants.BROADCASTRECEIVER_DLP_RESTART;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		context.sendBroadcast(intent);
	}

	// to stop DLP
	public static void stopDLP(Context context) {
		String broadcast = Constants.BROADCASTRECEIVER_DLP_STOP;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		context.sendBroadcast(intent);
	}

	// -----------------------------------------------------------------------------
	/**
	 * for DataLineProcessor4RemoteDT
	 * 
	 * @param context
	 */
	// to lock DLP service
	public static void lockDLP4RDTService(Context context) {
		String broadcast = Constants.BROADCASTRECEIVER_DLP4RDT_LOCK;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		context.sendBroadcast(intent);
	}

	// to unlock DLP service
	public static void unlockDLP4RDTService(Context context) {
		String broadcast = Constants.BROADCASTRECEIVER_DLP4RDT_UNLOCK;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		context.sendBroadcast(intent);
	}

	// to restart DLP
	public static void restartDLP4RDT(Context context) {
		String broadcast = Constants.BROADCASTRECEIVER_DLP4RDT_RESTART;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		context.sendBroadcast(intent);
	}

	// to stop DLP
	public static void stopDLP4RDT(Context context) {
		String broadcast = Constants.BROADCASTRECEIVER_DLP4RDT_STOP;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		context.sendBroadcast(intent);
	}

	// ------------------------------------------------------

	// to send data to DLP
	public static void sendData2DLP(Context context, String slcName,
			String dataLine) {
		String broadcast = Constants.BROADCASTRECEIVER_DLP_PROCESSDATALINE;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		intent.putExtra(Constants.SLC_NAME, slcName);
		intent.putExtra(Constants.DATALINE, dataLine);
		context.sendBroadcast(intent);
	}

	// to send data to DLP4RDT
	public static void sendData2DLP4RDT(Context context, String slcName,
			String dataLine) {
		String broadcast = Constants.BROADCASTRECEIVER_DLP4RDT_PROCESSDATALINE;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		intent.putExtra(Constants.SLC_NAME, slcName);
		intent.putExtra(Constants.DATALINE, dataLine);
		context.sendBroadcast(intent);
	}

}
