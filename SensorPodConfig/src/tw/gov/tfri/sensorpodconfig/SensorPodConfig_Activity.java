package tw.gov.tfri.sensorpodconfig;

import java.io.File;
import java.io.IOException;

//import tw.gov.tfri.XPathParser;
//import tw.gov.tfri.MainActivity.SaveButtonListener;

//import com.example.test3.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SensorPodConfig_Activity extends Activity {

	public static EditText editRemoteDT_IP;
	public static EditText editRemoteDT_port;
	public static EditText editSiteName;
	public static EditText editUart1;
	public static Button buttonSave;
	
	
	public static String DEFAULT_REMOTEDT_IP = "192.168.168.168";
	public static String DEFAULT_REMOTEDT_PORT = "3333";
	public static String DEFAULT_SITENAME = "TEST";
	public static String DEFAULT_UART1 = "V";
	
	public static File configPath = new File("/mnt/sdcard/SensorPodConfig");
	public static File configFile = new File(configPath + "/" + "SensorPodConfig.xml");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor_pod_config_activity);
		
		
		editRemoteDT_IP = (EditText)findViewById(R.id.editRemoteDT_IP);
		editRemoteDT_port = (EditText)findViewById(R.id.editRemoteDT_port);
		editSiteName = (EditText)findViewById(R.id.editSiteName);
		editUart1 = (EditText)findViewById(R.id.editUart1);
		buttonSave = (Button)findViewById(R.id.buttonSave);
		
		buttonSave.setOnClickListener(new SaveButtonListener());	
		//		
			
			if(!(configFile.exists())){
				editRemoteDT_IP.setText(DEFAULT_REMOTEDT_IP);
				editRemoteDT_port.setText(DEFAULT_REMOTEDT_PORT);
				editSiteName.setText(DEFAULT_SITENAME);
				editUart1.setText(DEFAULT_UART1);
				
				CreateConfigFile cf = new CreateConfigFile();
				cf.createConfigFile(configPath, configFile, DEFAULT_REMOTEDT_IP, DEFAULT_REMOTEDT_PORT, DEFAULT_SITENAME, DEFAULT_UART1);
			}else{
				if(configFile.exists()){
					//Toast.makeText(this, "Config File Exists", Toast.LENGTH_LONG).show();
					XPathParser g = new XPathParser();
					
					//get remoteDT_IP
					try {
						editRemoteDT_IP.setText(g.getDdataFromXML(configFile, "//config//remoteDT_IP"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(this, "IP ERROR", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
					
					//get remoteDT_port
					try {
						editRemoteDT_port.setText(g.getDdataFromXML(configFile, "//config//remoteDT_port"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(this, "PORT ERROR", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
					
					//get siteName
					try {
						editSiteName.setText(g.getDdataFromXML(configFile, "//config//siteName"));
					} catch (Exception e){
						// TODO Auto-generated catch block
						Toast.makeText(this, "SITENAME ERROR", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
					
					//get Uart1Rx
					try {
						editUart1.setText(g.getDdataFromXML(configFile, "//config//uart1"));
					} catch (Exception e){
						// TODO Auto-generated catch block
						Toast.makeText(this, "UART1RX ERROR", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}

				}
			}
	}//onCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor_pod_config, menu);
		return true;
	}
	
	
	public class SaveButtonListener implements OnClickListener{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			CreateConfigFile cf = new CreateConfigFile();
			try {
				cf.saveXML(
						configFile, 
						editRemoteDT_IP.getText().toString(), 
						editRemoteDT_port.getText().toString(), 
						editSiteName.getText().toString(),
						editUart1.getText().toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}	
	}//SaveButtonListener 
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
