/**
 * DataGather_activity.java
 * 
 * Activity to start and stop the DataGather service
 * 
 * @author Gesuri Ramirez
 * @date July 2012
 */

package org.cleos.android.ntl.datagather;

import java.io.File;

import org.cleos.android.ntl.datagather.R;
import org.cleos.android.ntl.utils.Configurator;

import tw.gov.tfri.CreateConfigFile;
import tw.gov.tfri.XPathParser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class DataGather_Activity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_gather);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_data_gather, menu);
		return true;
	}

	public void startService(View v) {
		Intent i = new Intent(this, DataGather_Service.class);
		startService(i);
	}

	public void stopService(View v) {
		Intent i = new Intent(this, DataGather_Service.class);
		stopService(i);
	}

}
