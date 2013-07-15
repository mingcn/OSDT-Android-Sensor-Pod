/**
 * RBNBServerHelper.java
 * 
 * Help for the DT server. It starts thread for:
 * Check if the DT server is running
 * Stop the DT server
 * Start the DT server
 * 
 * @author Gesuri Ramirez, Peter Shin
 * @date July 2012
 */

package org.cleos.dataturbine;

import android.util.Log;

import com.rbnb.api.Server;

public class RBNBServerHelper {
	private String TAG = "RBNBServerHelper";
	private Server rbnbServer;
	private boolean isRunning;

	public RBNBServerHelper(Server ser) {
		this.isRunning = false;
		this.rbnbServer = ser;
	}

	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Log.d(TAG + "::sleeping", "exception caught: " + e.toString());
		}
	}

	// --------------------------------
	// Checking if the rbnb server is running
	public boolean isServerRunning(Server server) {
		this.server = server;
		new CheckIfServerIsRunning().start();
		sleep(500);
		return isRunning;
	}

	public class CheckIfServerIsRunning extends Thread {

		@Override
		public void run() {
			isRunning = server != null && server.isRunning();
			return;
		}

	}

	// ---------------------------------------------------
	// Stop the rbnb server
	Server server;

	public void stopRBNBServer(Server server) {
		this.server = server;
		new StopRBNBServer().run();
	}

	private class StopRBNBServer extends Thread {
		private String tag = TAG + "::Stop";

		@Override
		public void run() {
			if (server != null)
				try {
					server.stop();
					if (server.isRunning()) {
						Log.i(tag,
								"server.stop() is called, but it's still running");
					} else {
						Log.i(tag, "server is not running after the stop()");
					}
				} catch (Exception e) {
					Log.e(tag, Log.getStackTraceString(e));
				}
			return;
		}

	}

	// -------------------------------------------------
	// Starting the rbnb server
	String[] args;

	public Server startRNBNServer(String[] args) {
		this.args = args;
		StartRBNBServer starter = new StartRBNBServer();
		starter.start();
		sleep(5000);
		waitUntilStart();
		return this.rbnbServer;
	}

	private class StartRBNBServer extends Thread {
		private String tag = TAG + "::Start";

		@Override
		public void run() {
			if (rbnbServer != null) {
				try {
					rbnbServer.stop();
					Log.i(tag, "rbnbServer was stoped for first time.");
				} catch (Exception e) {
					Log.e(tag, "Exception in stopping (first time) a server.",
							e);
				}
			}
			try {
				rbnbServer = Server.launchNewServer(args);
				Log.i(tag, "rbnbServer was launched for first time.");
			} catch (Exception e) {
				Log.e(tag, "Exception on launchNewServer (first time).");
			}

			waitUntilStart();

			try {
				rbnbServer.stop();
				Log.i(tag, "rbnbServer was stoped for second time.");
			} catch (Exception e) {
				Log.e(tag, "Exception in stopping (second time) a server.");
			}

			try {
				rbnbServer = Server.launchNewServer(args);
				Log.i(tag, "rbnbServer was launched for second time.");
			} catch (Exception e) {
				Log.e(tag, "Exception on launchNewServer for the second time.");
			}

			waitUntilStart();
			return;
		}

	}// end class StartRBNBServer

	protected void waitUntilStart() {
		int t = 0;
		while (t < 20) {
			try {
				if (rbnbServer != null && rbnbServer.isRunning()) {
					Log.i(TAG + "::waitUntil", "rbnbServer is up and running.");
					break;
				} else {
					Thread.sleep(250);
					t++;
				}
			} catch (Exception e) {
				Log.e(TAG + "::waitUntil",
						"Exception in isRunning()." + e.toString());
				break;
			}
		}

	}

}
