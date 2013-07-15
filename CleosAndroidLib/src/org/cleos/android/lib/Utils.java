/**
 * Utils.java
 * 
 * Help for lock/counter the services
 * Access to the files to lock/counter the services
 * Lock the services
 * Counter file
 * wait method
 * 
 * @author Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.android.lib;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

public class Utils {

	// check if DLP is lock
	public static synchronized boolean isLockService(Context context) {
		return isLockService(context, Constants.LOCK_DLP_FLAG_FILE);
	}

	// check if a specific service is lock
	public static synchronized boolean isLockService(Context context,
			String flagFile) {
		int lock = Utils.readAIntInLocalFile(context, flagFile);
		if (lock == Constants.LOCK)
			return true;
		else
			return false;
	}

	// Lock DLP service
	public static void lockService(Context context, boolean lock) {
		lockService(context, Constants.LOCK_DLP_FLAG_FILE, lock);
	}

	// Lock a specific service
	public static void lockService(Context context, String flagFile,
			boolean lock) {
		int flag;
		String menssage;
		if (lock) {
			flag = Constants.LOCK;
			menssage = "The service is LOCKED!!!";
		} else {
			flag = Constants.UNLOCK;
			menssage = "The service is UNLOCKED!!!";
		}
		Utils.writeAIntInLocalFile(context, flagFile, flag);
		Log.d("Utils::lockService", menssage);
		// Log.i(TAG,"The cache directory is: "+this.contex_.getCacheDir());
	}

	public static void wait(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Log.e("sleep", "IterruptedException caughted: " + e.toString());
			e.printStackTrace();
		}
	}

	// used to update a lock/counter file
	public static synchronized boolean writeAIntInLocalFile(Context context,
			String fileName, int b) {
		String tag = "Utils" + "::writeInLocalFile";
		FileOutputStream fos = null;
		try {
			fos = context
					.openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE);
		} catch (FileNotFoundException e) {
			Log.i(tag, "FileNotFounfException caughted: " + e.toString());
			e.printStackTrace();
			return false;
		}
		try {
			fos.write(b);
			fos.close();
		} catch (IOException e) {
			Log.i(tag, "IOException caughted: " + e.toString());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// used to read the actual value in a lock/counter file
	public static synchronized int readAIntInLocalFile(Context context,
			String fileName) {
		String tag = "Utils" + "::readInLocalFile";
		FileInputStream fis = null;
		try {
			fis = context.openFileInput(fileName);
		} catch (FileNotFoundException e) {
			Log.i(tag, "FileNotFounfException caughted: " + e.toString());
			e.printStackTrace();
			return -1;
		}
		int data = -1;
		try {
			data = fis.read();
			fis.close();
		} catch (IOException e) {
			Log.i(tag, "IOException caughted: " + e.toString());
			e.printStackTrace();
		}
		return data;
	}

	// -------------------- For the counter --------------------------
	public static synchronized void incrementLockCounter(Context context) {
		String fileName = Constants.COUNTER_LOCKS_FILE;
		int increment = readAIntInLocalFile(context, fileName);
		writeAIntInLocalFile(context, fileName, increment + 1);
	}

	public static synchronized void setZeroLockCounter(Context context) {
		writeAIntInLocalFile(context, Constants.COUNTER_LOCKS_FILE, 0);
	}

	public static synchronized int getLockCounter(Context context) {
		return Utils.readAIntInLocalFile(context, Constants.COUNTER_LOCKS_FILE);
	}

}
