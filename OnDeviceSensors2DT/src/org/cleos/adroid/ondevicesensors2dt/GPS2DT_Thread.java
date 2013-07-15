package org.cleos.adroid.ondevicesensors2dt;

import org.cleos.android.lib.Write2File;
import org.cleos.android.rbnb.SimpleFlusher;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GPS2DT_Thread extends Thread {
	
	private String TAG = getClass().getSimpleName();
	private boolean abort_ = false;
	private LocationManager mSM;
	private Context context;
	
	private SimpleFlusher sf;
	
	private String name;
	private Write2File log = new Write2File("Pressure", TAG, true);
	
	private int delay = 250; // in mS
	
	private String[] data = new String[4];
	
	private boolean mUseFine;
	private boolean mUseBoth;

	private static final int UPDATE_SECONDS = 1000;
	private static final int UPDATE_METERS = 10;
	private static final int TWO_MINUTES = 1000 * 60 * 1;
	
	
	public GPS2DT_Thread(String name, Context context, String ipp, LocationManager mSM){
		this.name = name;
		this.context = context;
		this.mSM = mSM;
		//this.sensor = mS;
		
		sf = new SimpleFlusher(name, context, ipp, log);
		
		//mSM.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		setupGPS();
	}
	
	private void setupGPS(){
		mUseFine = false;
		mUseBoth = true;
		Location gpsLocation = null;
		Location networkLocation = null;
		mSM.removeUpdates(listener);

		if (mUseFine) {
			// Request updates from just the fine (gps) provider.
			gpsLocation = requestUpdatesFromProvider(
					LocationManager.GPS_PROVIDER, R.string.not_support_gps);
		} else if (mUseBoth) {
			// Get coarse and fine location updates.
			// Request updates from both fine (gps) and coarse (network)
			// providers.
			gpsLocation = requestUpdatesFromProvider(
					LocationManager.GPS_PROVIDER, R.string.not_support_gps);
			networkLocation = requestUpdatesFromProvider(
					LocationManager.NETWORK_PROVIDER,
					R.string.not_support_network);
		}
	}
	
	
	public void setDelay(int delay_mS){ //
		this.delay = delay_mS;
	}
	
	@Override
	public void run() {
		super.run();
		sf.createConnections();
		while (true) {
			synchronized (this) {
				if (abort_) {
					Log.e(TAG, "Loop terminated!******************************");
					kill();
					break;
				}
			}
			sendData();
			sleep(delay);
		}
		mSM.removeUpdates(listener);
		Log.i(TAG, "Accelerometer thread finished with unregisterListener.");
	}
	
	public synchronized void abort(){
		abort_ = true;
	}
	
	private void sendData() {
		sf.flushData(data);
		
	}
	
	private void sleep(int mS) {
		try {
			super.sleep(mS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public void kill() {
		sf.kill();
	}
	
	public void setChNames(String[] chNames) {
		sf.setChNames(chNames);
	}

	public void setdTypes(String[] dTypes) {
		sf.setdTypes(dTypes);
	}

	public void setUnits(String[] units) {
		sf.setUnits(units);
	}

	public void setMIMEs(String[] mIMEs) {
		sf.setMIMEs(mIMEs);
	}

	
	
	
	
	
	//------------------------------------------------------------------------
	
	/** Determines whether one Location reading is better than the current Location fix.
     * Code taken from
     * http://developer.android.com/guide/topics/location/obtaining-user-location.html
     *
     * @param newLocation  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new
     *        one
     * @return The better Location object based on recency and accuracy.
     */
   protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
       if (currentBestLocation == null) {
           // A new location is always better than no location
           return newLocation;
       }

       // Check whether the new location fix is newer or older
       long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
       boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
       boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
       boolean isNewer = timeDelta > 0;

       // If it's been more than two minutes since the current location, use the new location
       // because the user has likely moved.
       if (isSignificantlyNewer) {
           return newLocation;
       // If the new location is more than two minutes older, it must be worse
       } else if (isSignificantlyOlder) {
           return currentBestLocation;
       }

       // Check whether the new location fix is more or less accurate
       int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
       boolean isLessAccurate = accuracyDelta > 0;
       boolean isMoreAccurate = accuracyDelta < 0;
       boolean isSignificantlyLessAccurate = accuracyDelta > 200;

       // Check if the old and new location are from the same provider
       boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
               currentBestLocation.getProvider());

       // Determine location quality using a combination of timeliness and accuracy
       if (isMoreAccurate) {
           return newLocation;
       } else if (isNewer && !isLessAccurate) {
           return newLocation;
       } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
           return newLocation;
       }
       return currentBestLocation;
   }

   /** Checks whether two providers are the same */
   private boolean isSameProvider(String provider1, String provider2) {
       if (provider1 == null) {
         return provider2 == null;
       }
       return provider1.equals(provider2);
   }
	
	
	/**
     * Method to register location updates with a desired location provider.  If the requested
     * provider is not available on the device, the app displays a Toast with a message referenced
     * by a resource id.
     *
     * @param provider Name of the requested provider.
     * @param errorResId Resource id for the string message to be displayed if the provider does
     *                   not exist on the device.
     * @return A previously returned {@link android.location.Location} from the requested provider,
     *         if exists.
     */
    private Location requestUpdatesFromProvider(final String provider, final int errorResId) {
        Location location = null;
        if (mSM.isProviderEnabled(provider)) {
            mSM.requestLocationUpdates(provider, UPDATE_SECONDS, UPDATE_METERS, listener);
            location = mSM.getLastKnownLocation(provider);
        } else {
            //Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
        	Log.e(TAG,"The provider does not exist on the device. "+errorResId);
        }
        return location;
    }

	private final LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// A new location update is received. Do something useful with it.
			// Update the UI with
			// the location update.
			// updateUILocation(location);
			Log.i("Testing",
					location.getLatitude() + ", " + location.getLongitude()
							+ "**************************");
			data[0] = Double.toString(location.getLatitude());
			data[1] = Double.toString(location.getLongitude());
			data[2] = Double.toString(location.getAltitude());
			data[3] = Double.toString(location.getAccuracy());
		}

		@Override
		public void onProviderDisabled(String provider) {
			/*Intent intent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(intent);*/
			abort();
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	
}
