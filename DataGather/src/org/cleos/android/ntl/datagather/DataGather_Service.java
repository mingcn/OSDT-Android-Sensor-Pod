/**
 * DataGather_Service.java
 * 
 * send broadcast to the other modules to start
 * create the DataGather service
 * Setup the IOIO board 
 * control the IOIO board
 * start/stop the SerialLineController
 * 
 * @author Gesuri Ramirez, Peter Shin
 * @date August 2012
 */

package org.cleos.android.ntl.datagather;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import org.cleos.android.lib.Utils;
import org.cleos.android.lib.Write2File;
import org.cleos.android.ntl.broadcasts.SendBroadcast;
import org.cleos.android.ntl.utils.CommandList;
import org.cleos.android.ntl.utils.Configurator;
import org.cleos.android.ntl.utils.IOIOParameters;

import tw.gov.tfri.CreateConfigFile;
import tw.gov.tfri.XPathParser;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIOFactory;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.api.exception.IncompatibilityException;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.cleos.android.ntl.utils.Configurator;//add by pstango

public class DataGather_Service extends Service {

	private Context DGServiceContext = this;

	/** Baud rate for the UARTs */
	private int baud = 9600;

	private String TAG = getClass().getSimpleName();
	private int pinRx1, pinRx2, pinRx3, pinTx1, pinTx2, pinTx3;

	private IOIOThread ioio_thread_;

	/** The uart objects from the IOIOboard */
	private Uart uart1;
	private Uart uart2;
	private Uart uart3;
	private OutputStream out1;
	private InputStream in1;
	private OutputStream out2;
	private InputStream in2;
	private OutputStream out3; // write to the serial port 3
	private InputStream in3; // reading the serial port 3
	private AnalogInput temperature; // onBoard temperature sensor
	private AnalogInput humidity; // onBoard humidity sensor
	private AnalogInput voltage; // onBoard voltage sensor
	private AnalogInput SolarIR;
	private AnalogInput FSH;
	private AnalogInput FST;
	private AnalogInput Soil;

	/** Object to write a log */
	private Write2File log;

	private LinkedList<SerialLineController> slcThreads;

	private boolean isRunningService = false;
	
	/*Object for reading the XML document for modularity*/
	private static final File configPath = new File("/mnt/sdcard/SensorPodConfig");
	private static final File configFile = new File(configPath + "/" + "SensorPodConfig.xml");
	private static String chooseSensor;
	private static String tempIP;
	
	static{
		if(!(configFile.exists())){
			if(!(configPath.exists())){
				configPath.mkdir();
				
				CreateConfigFile cf = new CreateConfigFile();
				cf.createConfigFile(configPath, configFile, "192.168.168.168", "3333", "TEST", "V");
			}else{
				CreateConfigFile cf = new CreateConfigFile();
				cf.createConfigFile(configPath, configFile, "192.168.168.168", "3333", "TEST", "V");
			}
		}
		
		 XPathParser xp = new XPathParser();
		 chooseSensor = xp.getDdataFromXML(configFile, "//config//uart1");
		 tempIP = xp.getDdataFromXML(configFile, "//config//remoteDT_IP");
		 
	}

	private String chosenSensor = chooseSensor.toLowerCase();
	private String printIP = tempIP;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startDT();
		runService();
		return (START_NOT_STICKY);
	}


	private void startDT() {
		// unlock DT service
		SendBroadcast.unlockDTservice(DGServiceContext);
		Utils.wait(10);
		// lock the DLP service
		SendBroadcast.lockDLPService(DGServiceContext);
		// start the DT server and then start DLP
		SendBroadcast.restartRBNB(DGServiceContext);
		// ----- for DLP4RDT
		SendBroadcast.lockDLP4RDTService(DGServiceContext);
		SendBroadcast.restartDLP4RDT(DGServiceContext);
	}

	private void runService() {
		if (!isRunningService) {
			Log.w(getClass().getName(), "Got to runService()!");
			isRunningService = true;

			Notification note = new Notification(R.drawable.ic_launcher,
					"The service is running", System.currentTimeMillis());
			Intent i = new Intent(this, DataGather_Activity.class);

			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

			note.setLatestEventInfo(this, "IOIO_sonde_templine",
					"Now Running!", pi);
			note.flags |= Notification.FLAG_NO_CLEAR;

			setupRunIOIO();
			msgToast("Services started on runService()");
			msgToast("The ip is: " + printIP);
			log.writelnT("Services started on runService()");
			startForeground(1337, note);
		}
	}

	private void msgToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		// log.writelnT(msg);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
		stop();
		Toast.makeText(this, "service done onDestroy", Toast.LENGTH_LONG)
				.show();
		log.writelnT("Service donde onDestroy().");
	}

	private void stop() {

		if (isRunningService) {
			log.writelnT("Service stopped from stop().");
			stopThreads();
			Log.w(getClass().getName(), "Got to stop()!");
			isRunningService = false;
			stopForeground(true);
			msgToast("Stoped service on stop()");
		}
	}

	// -----------------------------------------

	private void setupRunIOIO() {
		setUart1(5, 6, baud);
		setUart2(3, 4, baud);
		setUart3(10, 11, baud);
		log = new Write2File(TAG, "log.txt", true); // create the log file
		this.ioio_thread_ = new IOIOThread();
		this.ioio_thread_.start();
	}

	private void stopThreads() {
		killSLCThreads(this.slcThreads);
		if (ioio_thread_ != null)
			ioio_thread_.abort();

		try {
			ioio_thread_.join();
		} catch (InterruptedException e) {
		}
	}

	private void setUart1(int pinRx, int pinTx, int baud) {
		this.pinRx1 = pinRx;
		this.pinTx1 = pinTx;
		this.baud = baud;
	}

	private void setUart2(int pinRx, int pinTx, int baud) {
		this.pinRx2 = pinRx;
		this.pinTx2 = pinTx;
		this.baud = baud;
	}
	
	private void setUart3(int pinRx, int pinTx, int baud) {
		this.pinRx3 = pinRx;
		this.pinTx3 = pinTx;
		this.baud = baud;
	}

	private void killSLCThreads(LinkedList<SerialLineController> slcList) {
		if (slcList != null) {
			for (SerialLineController slc : slcList) {
				slc.interrupt();
				slc.abort();
				try {
					slc.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		log.writelnT("killSLCThreads() was called.");
	}

	class IOIOThread extends Thread {
		private IOIO ioio_; // Creates the IOIO object
		private boolean abort_ = false; // Flag in case of abort
		DigitalOutput led; // to control onboard LED

		/** Thread body. */
		@Override
		public void run() {
			super.run();
			loopIOIO();
		}// end run()

		public void loopIOIO() {
			while (true) {
				synchronized (this) {
					if (abort_) {
						killSLCThreads(slcThreads);
						break;
					}
					ioio_ = IOIOFactory.create();
				}
				try {
					setupIOIOConnection();
					setupIOIO_IOs();
					log.writelnT("IOIO Connection established.");
					slcThreads = spawnSLCs();
					while (true) {

						if (abort_) {
							killSLCThreads(slcThreads);
							break;
						}

						led.write(false);
						sleep(100);
						led.write(true);
						sleep(100);
					}
				} catch (ConnectionLostException e) {
				} catch (Exception e) {
					killSLCThreads(slcThreads); // kill all the threads
					Log.e("IOIO", "Unexpected exception caught", e);
					log.writelnT("IOIO disconected in unexpected exception: "
							+ e.toString());
					ioio_.disconnect();
					break;
				} finally {
					if (ioio_ != null) {
						log.writelnT("KillSLCThreads on finally ...");
						killSLCThreads(slcThreads);
						try {
							ioio_.waitForDisconnect();
						} catch (InterruptedException e) {
						}
					}
					synchronized (this) {
						ioio_ = null;
					}
				}
			}
		}

		/**
		 * @param the
		 *            instrument information, port configuration, UI handle,
		 *            system information
		 * @return list of handles to the threads
		 * */
		public LinkedList<SerialLineController> spawnSLCs() {
			Configurator conf = new Configurator();
			LinkedList<SerialLineController> slcList = new LinkedList<SerialLineController>();
			
			// KippZonnen Solar Irradiation SMP3-V
			IOIOParameters ioioParametersSolarIR = new IOIOParameters(SolarIR);
			CommandList cmdSolarIR = conf.createSolarIRCmdList(Configurator.SolarIR);
			SerialLineController kippZonen = new SerialLineController(
					DGServiceContext, cmdSolarIR, ioioParametersSolarIR, TAG + "/" + Configurator.SolarIR);
			
			// FTS Fuel Stick Moisture FS3-1
			IOIOParameters ioioParametersFSH = new IOIOParameters(FSH);
			IOIOParameters ioioParametersFST = new IOIOParameters(FST);
			CommandList cmdFSM = conf.createSolarIRCmdList(Configurator.FSM);
			SerialLineController fuelStick = new SerialLineController(
					DGServiceContext, cmdFSM, ioioParametersFSH, ioioParametersFST, TAG + "/" + Configurator.FSM);
			
			// Decagon 10HS Soil Moisture
			IOIOParameters ioioParametersSoil = new IOIOParameters(Soil);
			CommandList cmdSoil = conf.createSoilCmdList(Configurator.Soil);//modified by pstango
			SerialLineController soilMoisture = new SerialLineController(
					DGServiceContext, cmdSoil, ioioParametersSoil, TAG + "/" + Configurator.Soil);//modified by pstango
			
			// onBoardTemp
			IOIOParameters ioioParametersTemp = new IOIOParameters(temperature);
			CommandList cmdTemp = conf.createTempCmdList(/*"temperature"*/ Configurator.onboardTemperature);//modified by pstango
			SerialLineController tempTemp = new SerialLineController(
					DGServiceContext, cmdTemp, ioioParametersTemp, TAG + "/" + Configurator.onboardTemperature);//modified by pstango

			// onBoardHumi
			IOIOParameters ioioParametersHumi = new IOIOParameters(humidity);
			CommandList cmdHumi = conf.createHumiCmdList(/*"humidity"*/ Configurator.onboardHumidity);//modified by pstango
			SerialLineController tempHumi = new SerialLineController(
					DGServiceContext, cmdHumi, ioioParametersHumi, TAG + "/"
							+ /*"humidity"*/ Configurator.onboardHumidity);//modified by pstango

			// onBoardVolt
			IOIOParameters ioioParametersVolt = new IOIOParameters(voltage);
			CommandList cmdVolt = conf.createVoltCmdList(/*"voltage"*/ Configurator.onboardVoltage);//modified by pstango
			SerialLineController tempVolt = new SerialLineController(
					DGServiceContext, cmdVolt, ioioParametersVolt, TAG + "/"
							+ /*"voltage"*/ Configurator.onboardVoltage);//modified by pstango
		
			
			// Vaisela Weather Station #1
			IOIOParameters ioioParametersVWS = new IOIOParameters(in1, out1);
			CommandList cmdVWS = conf.createVWSCmdList(Configurator.VWS);//modified by pstango
			SerialLineController tempVWS = new SerialLineController(
					DGServiceContext, cmdVWS, ioioParametersVWS, TAG + "/"
							+ Configurator.VWS);//modified by pstango
				
				tempVWS.start();
				slcList.add(tempVWS);
				
			//Vaisala Weather Station #2
			IOIOParameters ioioParametersVWS2 = new IOIOParameters(in2, out2);
			CommandList cmdVWS2 = conf.createVWSCmdList(Configurator.VWS + "2");
			SerialLineController tempVWS2 = new SerialLineController(
					DGServiceContext, cmdVWS2, ioioParametersVWS2, TAG + "/" + Configurator.VWS + "2");
			
				tempVWS2.start();
				slcList.add(tempVWS2);
				
			//Vaisala Weather Station #3
			IOIOParameters ioioParametersVWS3 = new IOIOParameters(in3, out3);
			CommandList cmdVWS3 = conf.createVWSCmdList(Configurator.VWS + "3");
			SerialLineController tempVWS3 = new SerialLineController(
					DGServiceContext, cmdVWS3, ioioParametersVWS3, TAG + "/" + Configurator.VWS + "3");
				
				tempVWS3.start();
				slcList.add(tempVWS3);
			
			
			/*
			// Decagon CTD
			IOIOParameters ioioParametersCTD = new IOIOParameters(in2, out2);
			CommandList cmdCTD = conf.createCTDCmdList(Configurator.CTD);//modified by pstango
			SerialLineController CTD = new SerialLineController(
				DGServiceContext, cmdCTD, ioioParametersCTD, TAG + "/"
						+ Configurator.CTD);//modified by pstango
			
				CTD.start();
				slcList.add(CTD);
				
			// Decagon CTD #2
			IOIOParameters ioioParametersCTD2 = new IOIOParameters(in3, out3);
			CommandList cmdCTD2 = conf.createCTDCmdList(Configurator.CTD + "#2");//modified by pstango
			SerialLineController CTD2 = new SerialLineController(
				DGServiceContext, cmdCTD2, ioioParametersCTD2, TAG + "/"
						+ Configurator.CTD + "#2");//modified by pstango
			
				CTD2.start();
				slcList.add(CTD2);
			
			
			else if(chosenSensor.charAt(0) == 'd')
			{
				// Drain Gauge G3
				IOIOParameters ioioParametersDG = new IOIOParameters(in3, out3);
				CommandList cmdDG = conf.createDGCmdList(Configurator.DG);//modified by pstango
				SerialLineController DG = new SerialLineController(
					DGServiceContext, cmdDG, ioioParametersDG, TAG + "/"
							+ Configurator.DG);//modified by pstango
			
				DG.start();
				slcList.add(DG);
			}*/

			kippZonen.start();
			slcList.add(kippZonen);
			
			fuelStick.start();
			slcList.add(fuelStick);
			
			soilMoisture.start();
			slcList.add(soilMoisture);
			
			tempTemp.start();
			slcList.add(tempTemp);

			tempHumi.start();
			slcList.add(tempHumi);

			tempVolt.start();
			slcList.add(tempVolt);
			

			return slcList;
		}

		private void setupIOIOConnection() throws ConnectionLostException,
				IncompatibilityException {
			ioio_.waitForConnect();
		}// end setupIOIOConnection()

		private void setupIOIO_IOs() throws ConnectionLostException {
			led = ioio_.openDigitalOutput(0, true);
			uart1 = ioio_.openUart(pinRx1, pinTx1, baud, Uart.Parity.NONE,
					Uart.StopBits.ONE);
			uart2 = ioio_.openUart(pinRx2, pinTx2, baud, Uart.Parity.NONE,
					Uart.StopBits.ONE);
			uart3 = ioio_.openUart(pinRx3, pinTx3, baud, Uart.Parity.NONE,
					Uart.StopBits.ONE);
			// in and out streams
			out1 = uart1.getOutputStream();
			in1 = uart1.getInputStream();
			out2 = uart2.getOutputStream();
			in2 = uart2.getInputStream();
			out3 = uart3.getOutputStream();
			in3 = uart3.getInputStream();
			temperature = ioio_.openAnalogInput(46);
			humidity = ioio_.openAnalogInput(45);
			voltage = ioio_.openAnalogInput(44);
			SolarIR = ioio_.openAnalogInput(33);
			FSH = ioio_.openAnalogInput(34);
			FST = ioio_.openAnalogInput(37);
			Soil = ioio_.openAnalogInput(38);
		}// end setupIOIO_IOs()

		/**
		 * Abort the connection.
		 * 
		 * This is a little tricky synchronization-wise: we need to be handle
		 * the case of abortion happening before the IOIO instance is created or
		 * during its creation.
		 */
		synchronized public void abort() {
			abort_ = true;
			if (ioio_ != null) {
				ioio_.disconnect();
			}
		}

	}// end IOIOThread

}
