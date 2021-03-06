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
 * Base class which represents services to be launched by dtController.
 * <p>
 *
 * @author John P. Wilson
 *
 * @version 05/25/2011
 */

/*
 * Copyright 2011 Erigo Technologies LLC
 * All Rights Reserved
 *
 *   Date      By	Description
 * MM/DD/YYYY
 * ----------  --	-----------
 * 05/25/2011  JPW	Created.
 *
 */

package org.osdt.lib;

import android.app.Service;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

abstract public class DTService extends Service {

	public String TAG = "<default_tag>";
	public Vector<AppThread> myThread = new Vector<AppThread>();
	public static long counter = 0;

	public int onStartCommand(Intent intent, int flags, int startId) {

		String notice = TAG + ": " + intent.getAction();
		Log.d(TAG, "onStart: " + notice);
		Toast.makeText(this, notice, Toast.LENGTH_SHORT).show();

		counter = 0; // status count

		// run as "foreground" service with notification
		Notification notification = new Notification(R.drawable.appengine, TAG
				+ ": Service Active", System.currentTimeMillis());
		notification.setLatestEventInfo(this, TAG, notice, PendingIntent
				.getActivity(this.getBaseContext(), 0, intent,
						PendingIntent.FLAG_CANCEL_CURRENT));
		startForeground(TAG.hashCode(), notification); // use hash of TAG as
														// unique ID

		return START_REDELIVER_INTENT; // return STICKY for auto-restart if dies
	}

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	abstract public class LocalBinder extends Binder implements IMyService {

	}

	abstract public IBinder onBind(Intent intent);

	// This is the object that receives interactions from clients.
	// private final IBinder mBinder = new LocalBinder();

	public void onDestroy() {
		Toast.makeText(this, TAG + " Service Stopped", Toast.LENGTH_SHORT)
				.show();
		if (myThread != null) {
			for (Iterator<AppThread> t = myThread.iterator(); t.hasNext();) {
				AppThread at = t.next();
				Log.d(TAG, "Stopping: " + at);
				at.disconnect();
			}
			myThread.clear(); // ??
		}
		stopSelf(); // stop the service
	}

	abstract public class AppThread extends Thread implements DTDisconnectable {
		public AppThread(Intent intent, int flags, int startId)
				throws IOException {

		}
	}

}
