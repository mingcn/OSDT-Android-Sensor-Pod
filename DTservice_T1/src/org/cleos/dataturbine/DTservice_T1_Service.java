/**
 * DTservice_T1_Service.java
 * 
 * Create a foreground service extending from DTService.
 * Manage DT server (Start/restart/stop DT server)
 * 
 * @author Gesuri Ramirez, Peter Shin, Matt Miller
 * @date May 2012
 */

package org.cleos.dataturbine;

import java.io.File;
import java.io.IOException;

import org.cleos.android.lib.Constants;
import org.cleos.android.lib.Utils;
import org.osdt.lib.DTService;

import com.rbnb.api.Server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

//---------------------------------------------------------------------------------
public class DTservice_T1_Service extends DTService {

	private static Server rbnbServer;
	private boolean Running;
	private int counter, restarts;
	private boolean starting;
	private boolean stopping;
	private String homeDir;
	private Context contex_;

	// -------------------------------------------------------------------------------
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.contex_ = this;
		lockService(true);
		TAG = "DTservice_T1_Service";

		int retVal = super.onStartCommand(intent, flags, startId);

		this.Running = false;
		this.starting = false;
		this.stopping = false;
		this.homeDir = "/sdcard/RBNB/";

		try {
			AppThread newThread = new AppThread(intent, flags, startId);
			newThread.start(); // Start the application thread
			myThread.add(newThread);
			Log.d(TAG, "after myThread.add");
		} catch (Exception e) {
			Log.e(TAG, "Exception on AppThread: " + e);
			return START_NOT_STICKY; // no thrash
		}
		return retVal;
	}

	private void lockService(boolean lock) {
		int flag;
		String menssage;
		if (lock) {
			flag = Constants.LOCK;
			menssage = "The service is LOCKED!!!";
		} else {
			flag = Constants.UNLOCK;
			menssage = "The service is UNLOCKED!!!";
		}
		Utils.writeAIntInLocalFile(this.contex_,
				Constants.LOCK_RBNB_SERVICE_FLAG_FILE, flag);
		Log.i(TAG, menssage);
		// Log.i(TAG,"The cache directory is: "+this.contex_.getCacheDir());
	}

	// ---------------------------------------------------------------------------------
	@Override
	public void onCreate() {
		File path = Environment.getExternalStorageDirectory();
		homeDir = path.getAbsolutePath() + "/RBNB/";
	}

	// ---------------------------------------------------------------------------------
	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients.
	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends DTService.LocalBinder {
		DTservice_T1_Service getService() {
			return DTservice_T1_Service.this;
		}

		public String getStatus() {
			return ((DTservice_T1_Service.AppThread) myThread.get(0))
					.myStatus();
		}
	}

	// -------------------------------------------------------------------------------
	// Application specific code to follow. Generic service startup code
	// preceding.
	// -------------------------------------------------------------------------------

	public class AppThread extends DTService.AppThread {
		Intent intent;

		public AppThread(Intent intent, int flags, int startId)
				throws IOException {
			super(intent, flags, startId);
			restarts = 0;
			counter = 0;
			this.intent = intent;

		}

		public void run() {
			stopping = false; // done stopping
			startServer(intent); // startup RBNB in constructor (force wait? may
									// lead to Force Stop)
		}

		public void disconnect() {
			Log.i(TAG, "Called disconnect");
			stopServer();

		}

		// ---------------------------------------------------------------------------------

		private void startServer(Intent intent) {

			if (isRunning())
				Log.i(TAG, "RBNB_Server is running");
			else
				Log.i(TAG, "RBNB_Server is NOT running");

			System.setProperty("parentName", "Server"); // hope for default
														// parent server name

			String args[] = getArgs(intent.getExtras());

			RBNBServerHelper starterServer = new RBNBServerHelper(rbnbServer);
			rbnbServer = starterServer.startRNBNServer(args);

			String statusMsg = "DataTurbine Startup Error.";
			if (isRunning()) {
				statusMsg = "DataTurbine Started.";
				restartDLP(); // it is sending a broadcast to restart the DLP
			} else {
				Log.i(TAG, "The service will stop itself.");
				stopServer();
				stopSelf();
			}

			Log.i(TAG, statusMsg);
			starting = false;
			lockService(false);
			// Log.i(TAG,"set flag to UNLOCK!");

		}

		// -----------------------------------------------------------------------------
		private void reStartRBNBServer(String[] args) {
			if (rbnbServer != null) {
				try {
					rbnbServer.stop();
					Log.i(TAG, "RBNB server stoped for first time.");
				} catch (Exception e) {
					Log.e(TAG,
							"Exception on stop Server for first time "
									+ Log.getStackTraceString(e));
				}
			}
			try {
				rbnbServer = Server.launchNewServer(args);
				Log.i(TAG, "RBNB server launched for first time.");
			} catch (Exception e) {
				Log.e(TAG,
						"Exception on launchNewServer for first time"
								+ Log.getStackTraceString(e));
			}

			waitUntilStart();

			try {
				rbnbServer.stop();
				Log.i(TAG, "RBNB server stoped for second time.");
			} catch (Exception e) {
				Log.e(TAG,
						"Exception on stop Server for second time "
								+ Log.getStackTraceString(e));
			}
			try {
				rbnbServer = Server.launchNewServer(args);
				Log.i(TAG, "RBNB server launched for second time.");
			} catch (Exception e) {
				Log.e(TAG,
						"Exception on launchNewServer for second time"
								+ Log.getStackTraceString(e));
			}
		}

		protected void waitUntilStart() {
			int t = 0;
			while (t < 20) {
				try {
					if (rbnbServer != null && rbnbServer.isRunning()) {
						Log.i(TAG + "::waitUntil",
								"rbnbServer is up and running.");
						break;
					} else {
						Thread.sleep(250);
						t++;
					}
				} catch (Exception e) {
					// Log.e(tag,"Exception in stopping the server for the second time on:"+Log.getStackTraceString(e));
					Log.e(TAG + "::waitUntil",
							"Exception in isRunning()." + e.toString());
					break;
				}
			}

		}

		// -----------------------------------------------------------------------------

		private boolean isRunning() {
			RBNBServerHelper checkServer = new RBNBServerHelper(rbnbServer);
			boolean testrun = checkServer.isServerRunning(rbnbServer);

			Running = testrun;
			if (Running)
				Log.i(TAG, "The RBNB server is running.");
			else
				Log.i(TAG, "The RBNB server is NOT running.");
			return (Running);
		}

		// ---------------------------------------------------------------------------------
		public void stopServer() {
			stopping = true;
			if (rbnbServer == null) {
				Log.i(TAG, "DataTurbine nothing to stop!!!!!!!!!!!!!!");
			} else if (!isRunning()) {
				Log.i(TAG, "DataTurbine already stopped!!!!!!!!!!!!!!");
			} else {
				Log.d(TAG, "Stopping!!!!!!!!!!!!!!!!!!!!!");
				Thread.setDefaultUncaughtExceptionHandler(null); // needed to
																	// server.stop?

				RBNBServerHelper stopServer = new RBNBServerHelper(rbnbServer);
				stopServer.stopRBNBServer(rbnbServer);

				Log.i(TAG, "DataTurbine server terminated!");
				lockService(false);
			}
		}

		// called from dtControl...
		public String myStatus() {

			String myStatus = "unknown";
			if (!Running) {
				myStatus = "Stopped: " + counter;
			} else if (restarts > 0) {
				myStatus = "Running: " + counter + "," + restarts;
			} else {
				myStatus = "Running: " + counter;
			}
			Log.i(TAG, myStatus);

			return (myStatus);
		}

		// ---------------------------------------------------------------------------------
		private String[] getArgs(Bundle extras) {
			Integer i = 0;
			String[] args = new String[99];
			String parentIA = ""; // was "erigoserver.com"
			String serverName = "DT.android";

			Log.d(TAG, "Bundle extras: " + extras.keySet());
			// note: all bundle extras put in as string, parse to other types
			// here

			if (extras.containsKey("-p"))
				parentIA = extras.getString("-p");
			if (parentIA == null)
				parentIA = "";
			if (parentIA.length() > 0) { // allow no-parent stand-alone mode
				args[i++] = "-p";
				args[i++] = parentIA;
			}

			args[i++] = "-l";
			if (extras.containsKey("-l"))
				args[i++] = extras.getString("-l");
			else
				args[i++] = "60,10,0,none";

			args[i++] = "-m";
			if (extras.containsKey("-m"))
				args[i++] = extras.getString("-m");
			else
				args[i++] = "1,600,0,none";

			String myIA = "localhost:3333"; // try localhost
			args[i++] = "-a";
			if (extras.containsKey("-a"))
				args[i++] = extras.getString("-a");
			else
				args[i++] = myIA; // what happens if this changes (e.g.
									// roaming)??

			if (extras.containsKey("-F")
					&& !extras.getString("-F").equals("false"))
				args[i++] = "-F"; // reload archives

			if (extras.containsKey("-n"))
				serverName = extras.getString("-n");
			args[i++] = "-n";
			args[i++] = serverName;

			args[i++] = "-H";

			File path = new File(homeDir, serverName);
			path.mkdirs(); // make sure it is there
			args[i++] = path.getAbsolutePath();
			Log.d(TAG, "HomeDir: " + path);

			String[] rargs = new String[i];
			System.arraycopy(args, 0, rargs, 0, rargs.length);

			return (rargs);
		}

	} // end appThread

	// ---------------------------------------------------------------------------------

	public void printStrAr(String[] ar) {
		for (String str : ar) {
			Log.d(TAG, "Array = " + str);
		}
	}

	private void restartDLP() {
		Log.i(TAG, "Restart DLP");

		String broadcast = Constants.BROADCASTRECEIVER_DLP_RESTART;
		Intent intent = new Intent();
		intent.setAction(broadcast);
		sendBroadcast(intent);
	}

}
