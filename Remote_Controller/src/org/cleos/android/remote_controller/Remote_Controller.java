/**
 * remote_Controller.java
 * 
 * To restart, stop, lock, unlock:
 *  DT, DLP, and DLP4RDT
 *  Also, send test data to the DLP and DLP4RDT.
 *  It simulate the DataGather
 * 
 * @author Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.android.remote_controller;


import org.cleos.android.lib.Utils;
import org.cleos.android.ntl.broadcasts.SendBroadcast;
import org.cleos.rbnb_remotr_controller.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.cleos.android.ntl.utils.Configurator;//add by pstango

public class Remote_Controller extends Activity {
	private String TAG = getClass().getSimpleName();
	
	/**
	 * Send broadcast to service 
	 * 		Constants.BROADCASTRECEIVER_RESTART_RBNB 
	 */

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

//-----------------------------------------------------------------------------
/**
 * For RBNB
 */
    public void startDTService(View v) {
    	SendBroadcast.startDTService(this);

	}

	public void stopDTService(View v) {
		SendBroadcast.stopDTService(this);

	}
	
    
    public void restartDTserver(View v){
    	SendBroadcast.restartRBNB(this);

    }
    
    public void lockDTservice(View v){
    	SendBroadcast.lockDTservice(this);

    }
    
    public void unlockDTservice(View v){
    	SendBroadcast.unlockDTservice(this);

    }
    
//-----------------------------------------------------------------------------
    /**
     * for DataLineProcessor 
     */
    public void lockDLPService(View v){
    	Log.i(TAG, "On lockDLPService");
    	
    	SendBroadcast.lockDLPService(this);

    }
    
    public void unlockDLPService(View v){
    	Log.i(TAG, "On unlockDLPService");
    	
    	SendBroadcast.unlockDLPService(this);

    }
    
    public void restartDLP(View v){
    	Log.i(TAG, "On restartDLP");
    	
    	SendBroadcast.restartDLP(this);

    }
    
    public void stopDLP(View v){
    	Log.i(TAG, "On stopDLP");
    	
    	SendBroadcast.stopDLP(this);

    }
    
    
    
    
  //-----------------------------------------------------------------------------
    /**
     * for DataLineProcessor4RemoteDT 
     */
    public void lockDLP4RDTService(View v){
    	Log.i(TAG, "On lockDLPService");
    	
    	SendBroadcast.lockDLP4RDTService(this);

    }
    
    public void unlockDLP4RDTService(View v){
    	Log.i(TAG, "On unlockDLPService");
    	
    	SendBroadcast.unlockDLP4RDTService(this);

    }
    
    public void restartDLP4RDT(View v){
    	Log.i(TAG, "On restartDLP");
    	
    	SendBroadcast.restartDLP4RDT(this);

    }
    
    public void stopDLP4RDT(View v){
    	Log.i(TAG, "On stopDLP");
    	
    	SendBroadcast.stopDLP4RDT(this);

    }
    
    public void lockAll(View v){
    	SendBroadcast.lockDLP4RDTService(this);
    	SendBroadcast.lockDLPService(this);
    	SendBroadcast.lockDTservice(this);
    }
    
    public void unlockAll(View v){
    	SendBroadcast.unlockDLP4RDTService(this);
    	SendBroadcast.unlockDLPService(this);
    	SendBroadcast.unlockDTservice(this);
    }
    
    public void startAll(View v){
    	SendBroadcast.restartDLP(this);
    	SendBroadcast.restartDLP4RDT(this);
    	SendBroadcast.restartRBNB(this);
    }
    
    public void stopAll(View v){
    	SendBroadcast.stopDLP(this);
    	SendBroadcast.stopDLP4RDT(this);
    	SendBroadcast.stopDTService(this);
    }
    
    //------------------------------------------------------------------
    

    public void processLineInDLP(View v){
    	Log.i(TAG, "On processLineInDLP");
//    	testLocksfile();
    	sendData4Sonde();
    	sendData4Templine();
    	sendData4VWS();
    	sendData4Humi();
    	sendData4Temp();
    	sendData4Volt();
//    	
    }
    
    
    
    private void sendData4Sonde(){
    	SendBroadcast.sendData2DLP(this, "Sonde", "0+23.88+2.57+0.17+0+0.0+0.00+12.4");
    	SendBroadcast.sendData2DLP4RDT(this, "Sonde", "0+23.88+2.57+0.17+0+0.0+0.00+12.4");
    }
    
    private void sendData4Templine(){
    	SendBroadcast.sendData2DLP(this, "Templine", "     22.387     22.401     22.545     22.490     22.443     22.446     22.507     22.509     22.504     22.519     22.444     22.476     22.455     22.353     22.465     22.457     22.443     22.578     22.372     22.444     22.597     22.518     22.398     22.448     22.450     22.561     22.549");
    	SendBroadcast.sendData2DLP4RDT(this, "Templine", "     22.387     22.401     22.545     22.490     22.443     22.446     22.507     22.509     22.504     22.519     22.444     22.476     22.455     22.353     22.465     22.457     22.443     22.578     22.372     22.444     22.597     22.518     22.398     22.448     22.450     22.561     22.549");
    }
    
    private void sendData4VWS(){
    	SendBroadcast.sendData2DLP(this, /*"VWS"*/ Configurator.VWS, "0R0,Dm=210D,Dx=210D,Sn=0.0M,Sm=0.0M,Sx=0.1M,Ta=23.0C,Ua=52.8P,Pa=1000.6H,Rc=0.00M,Rd=0s,Ri=0.0M,Hc=0.0M,Hd=0s,Hi=0.0M,Rp=0.0M,Vs=12.7V,Vr=3.525V");//modified by pstango
    	SendBroadcast.sendData2DLP4RDT(this, /*"VWS"*/ Configurator.VWS, "0R0,Dm=210D,Dx=210D,Sn=0.0M,Sm=0.0M,Sx=0.1M,Ta=23.0C,Ua=52.8P,Pa=1000.6H,Rc=0.00M,Rd=0s,Ri=0.0M,Hc=0.0M,Hd=0s,Hi=0.0M,Rp=0.0M,Vs=12.7V,Vr=3.525V");//modified by pstango
    }
    
    private void sendData4Temp(){
    	SendBroadcast.sendData2DLP(this, /*"tmperature"*/ Configurator.onboardTemperature, "22.7");//modified by pstango
    	SendBroadcast.sendData2DLP4RDT(this, /*"temperature"*/ Configurator.onboardTemperature, "22.7");//modified by pstango
    }
    
    private void sendData4Humi(){
    	SendBroadcast.sendData2DLP(this, /*"humidity"*/ Configurator.onboardHumidity, "33.7");//modified by pstango
    	SendBroadcast.sendData2DLP4RDT(this, /*"humidity"*/ Configurator.onboardHumidity, "33.7");//modified by pstango
    }

    private void sendData4Volt(){
    	SendBroadcast.sendData2DLP(this, /*"voltage"*/ Configurator.onboardVoltage, "12.7");//modified by pstango
    	SendBroadcast.sendData2DLP4RDT(this, /*"voltage"*/ Configurator.onboardVoltage, "12.7");//modified by pstango
    }
    
    private void testLocksfile(){
    	//Utils.setZeroLockCounter(this);
    	Log.i("Hola","Number in file is: "+Utils.getLockCounter(this));
    	Utils.incrementLockCounter(this);
    	Log.i("Hola","Number in file is: "+Utils.getLockCounter(this));
    	Utils.incrementLockCounter(this);
    	Log.i("Hola","Number in file is: "+Utils.getLockCounter(this));
    	Utils.incrementLockCounter(this);
    	Log.i("Hola","Number in file is: "+Utils.getLockCounter(this));
    	Utils.incrementLockCounter(this);
    	Log.i("Hola","Number in file is: "+Utils.getLockCounter(this));
    	Utils.setZeroLockCounter(this);
    }
    
    
    
}
