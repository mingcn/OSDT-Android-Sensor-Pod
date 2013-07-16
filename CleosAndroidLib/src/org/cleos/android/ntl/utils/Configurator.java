/**
 * Configurator.java
 * 
 * Create the command and commandList for each sensor
 * Sensors: Sonde, Templine, Vaisela Weather Station
 * Onboard sensor (analog sensor attached to the IOIO board): temperature, humidity, and voltage
 * 
 * @author Peter Shin, Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.android.ntl.utils;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedList;

import org.cleos.android.lib.Interval;
import org.cleos.android.lib.TimeHelper;

import org.cleos.android.ntl.utils.Command;
import org.cleos.android.ntl.utils.CommandList;

import tw.gov.tfri.CreateConfigFile;
import tw.gov.tfri.XPathParser;

public class Configurator {
	/**
	 * Global value settings are for different site to put data to the same RBNB server.
	 * 
	 * The values are used by: 
	 * 		Configurator.java
	 * 		SerialLineController.java
	 * 		DataLineProcessor_Service.java
	 * 		DataLineProcessor4RemoteDt_Service.java
	 * 		DataGather_Service.java
	 * 		RemoteController.java
	 * 
	 * by pstango
	 */
	//public static final String remoteDtHost = "192.168.20.125:3333";
	//public static final String siteName = "LHC";
	//public static final String onboardHumidity = siteName + "_" + "OnboardHumidity";
	//public static final String onboardTemperature = siteName + "_" + "OnboardTemperature";
	//public static final String onboardVoltage = siteName + "_" + "OnboardVoltage";
	//public static final String VWS = siteName + "_" + "VWS";

	
	private static final File configPath = new File("/mnt/sdcard/SensorPodConfig");
	private static final File configFile = new File(configPath + "/" + "SensorPodConfig.xml");
	
	public static String remoteDtHost;
	public static String remoteDtIP;// = "192.168.168.168";
	public static String remoteDtPort;// = "3333";
	public static String siteName;// = "TEST";
	
	
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
		 String remoteDT_IP = xp.getDdataFromXML(configFile, "//config//remoteDT_IP");
		 String remoteDT_port = xp.getDdataFromXML(configFile, "//config//remoteDT_port");
		 String sName = xp.getDdataFromXML(configFile, "//config//siteName");
		
		 Configurator.remoteDtHost = remoteDT_IP + ":" + remoteDT_port;
		 Configurator.siteName =sName;
	}
	
	public  static String onboardHumidity = siteName + "_" + "OnboardHumidity";
	public  static String onboardTemperature = siteName + "_" + "OnboardTemperature";
	public  static String onboardVoltage = siteName + "_" + "OnboardVoltage";
	public  static String VWS = siteName + "_" + "VWS";
	
/*--------------------------------------------------------------------------------------------------------*/	
	private String remoteDtAddress = remoteDtHost; //modified by pstango
	//private String remoteDtAddress = "192.168.20.125:3333";
	//private String remoteDtAddress = "184.169.156.148:3333";// "192.168.1.48:3333";
															// //"184.169.156.148:3333";
	private String localDtAddress = "localhost:3333";
	
	
	private TimeHelper th = new TimeHelper();
	private char endLine = 13;


	public CommandList createTempCmdList(String name) {

		String[] chNames = { "temperature" };

		String[] dTypes = { "float64" };

		String[] units = { "Celsius" };

		String[] MIMEs = { "application/octet-stream" };

		LinkedList<Command> tempCommandList = new LinkedList<Command>();

		Calendar startDateTime = th.now();
		Calendar endDateTime = null;

		Interval interval = new Interval(0, 0, 1, 0); // days, hours/24,
														// min/60, sec/60

		Command tempCmd = new Command("@T", "regularExpression", "", 5000, 3,
				1, interval);
		tempCmd.setDtSrcName("BoardTempSrc");
		tempCmd.setDtAddress(localDtAddress);
		tempCmd.setRemoteDtAddress(remoteDtAddress);
		tempCmd.setDelimiter("");
		tempCmd.setChNames(chNames);
		tempCmd.setDTypes(dTypes);
		tempCmd.setUnits(units);
		tempCmd.setMIMEs(MIMEs);

		tempCommandList.add(tempCmd);

		CommandList cmdList = new CommandList(name, tempCommandList,
				startDateTime, endDateTime);

		return cmdList;
	}

	public CommandList createHumiCmdList(String name) {
		String[] chNames = { "humidity" };

		String[] dTypes = { "float64" };

		String[] units = { "Percent" };

		String[] MIMEs = { "application/octet-stream" };

		LinkedList<Command> tempCommandList = new LinkedList<Command>();

		Calendar startDateTime = th.now();
		Calendar endDateTime = null;

		Interval interval = new Interval(0, 0, 1, 0); // days, hours/24,
		// min/60, sec/60

		Command tempCmd = new Command("@H", "regularExpression", "", 5000, 3,
				1, interval);
		tempCmd.setDtSrcName("BoardHumiditySrc");
		tempCmd.setDtAddress(localDtAddress);
		tempCmd.setRemoteDtAddress(remoteDtAddress);
		tempCmd.setDelimiter("");
		tempCmd.setChNames(chNames);
		tempCmd.setDTypes(dTypes);
		tempCmd.setUnits(units);
		tempCmd.setMIMEs(MIMEs);

		tempCommandList.add(tempCmd);

		CommandList cmdList = new CommandList(name, tempCommandList,
				startDateTime, endDateTime);
		return cmdList;
	}

	public CommandList createVoltCmdList(String name) {
		String[] chNames = { "voltage" };

		String[] dTypes = { "float64" };

		String[] units = { "volts" };

		String[] MIMEs = { "application/octet-stream" };

		LinkedList<Command> tempCommandList = new LinkedList<Command>();

		Calendar startDateTime = th.now();
		Calendar endDateTime = null;

		Interval interval = new Interval(0, 0, 1, 0); // days, hours/24,
														// min/60, sec/60

		Command tempCmd = new Command("@V", "regularExpression", "", 5000, 3,
				1, interval);
		tempCmd.setDtSrcName("BoardVoltageSrc");
		tempCmd.setDtAddress(localDtAddress);
		tempCmd.setRemoteDtAddress(remoteDtAddress);
		tempCmd.setDelimiter("");
		tempCmd.setChNames(chNames);
		tempCmd.setDTypes(dTypes);
		tempCmd.setUnits(units);
		tempCmd.setMIMEs(MIMEs);

		tempCommandList.add(tempCmd);

		CommandList cmdList = new CommandList(name, tempCommandList,
				startDateTime, endDateTime);
		return cmdList;
	}

	public CommandList createVWSCmdList(String name) {
		String[] chNames = { 
				"C0",
				"C1",
				"WindDirMin", //Dn=088D, Wind Direction Minimum (degree)
				"WindDirAvg", //Dm=088D, Wind Direction Average (degree)
				"WindDirMax", //Dx=089D, Wind Direction Maximum (degree)
				"WindSpdMin", //Sn=4.1M, Wind Speed Minimum (m/s)
				"WindSpdAvg", //Sm=4.8M, Wind Speed Average (m/s)
				"WindSpdMax", //Sx=5.7M, Wind Speed Maximum (m/s)
				"AirTemp", //Ta=18.2C, Air Temperature (Celsius)
				"RelHumi", //Ua=87.8P, Relative Humidity (Percentage)
				"AirPress", //Pa=930.9H, Air Pressure (hPa)
				"RainAcc", //Rc=79.59M, Rain Accumulation (mm)
				"RainDur", //Rd=42760s, Rain Duration (seconds)
				"RainInt", //Ri=0.0M, Rain Intensity (mm/hr)
				"HailAcc", //Hc=0.0M, Hail Accumulation (hits/cm2)
				"HailDur", //Hd=10s, Hail Duration (seconds)
				"HailInt", //Hi=0.0M, Hail Intensity (hits/cm2hr)
				"SupVol", //Vs=12.9V, Supply Voltage (V)
				"RefVol" //Vr=3.490V, Reference Voltage (V)
				};

		String[] dTypes = { "float64", 
				"float64", 
				"float64", 
				"float64", 
				"float64", 
				"float64",
				"float64", 
				"float64", 
				"float64", 
				"float64", 
				"float64",
				"float64", 
				"float64", 
				"float64", 
				"float64", 
				"float64",
				"float64", 
				"float64", 
				"float64" };

		String[] units = {  
				"none",
				"none",
				"Deg", 
				"Deg", 
				"Deg",
				"m/s", "m/s", "m/s",
				"Celsius", "$RH", 
				"hPa", "mm", "Seconds", 
				"mm/hr", "hits/cm^2", "Seconds", "hits/cm^2hr", 
				"Volts", "Volts" };

		String[] MIMEs = { "application/octet-stream",
				"application/octet-stream", "application/octet-stream",
				"application/octet-stream", "application/octet-stream",
				"application/octet-stream", "application/octet-stream",
				"application/octet-stream", "application/octet-stream",
				"application/octet-stream", "application/octet-stream",
				"application/octet-stream", "application/octet-stream",
				"application/octet-stream", "application/octet-stream",
				"application/octet-stream", "application/octet-stream",
				"application/octet-stream", "application/octet-stream"};

		LinkedList<Command> tempCommandList = new LinkedList<Command>();

		Calendar startDateTime = th.now();
		Calendar endDateTime = null;

		Interval interval = new Interval(0, 0, 1, 0);// days, hours/24,
		// min/60, sec/60

		char CR = 13;
		char LF = 10;
		Command tempCmd = new Command("0R0", "regularExpression", "" + CR + LF,
				5000, 3, 1, interval);
		tempCmd.setDtSrcName("VWSSrc");
		tempCmd.setDtAddress(localDtAddress);
		tempCmd.setRemoteDtAddress(remoteDtAddress);
		tempCmd.setDelimiter("[,=CDHMPRSTUVacdimnprsx]+");
		tempCmd.setChNames(chNames);
		tempCmd.setDTypes(dTypes);
		tempCmd.setUnits(units);
		tempCmd.setMIMEs(MIMEs);

		tempCommandList.add(tempCmd);

		CommandList cmdList = new CommandList(name, tempCommandList,
				startDateTime, endDateTime);

		return cmdList;
	}
	
	public CommandList createCTDCmdList(String name) {
		String[] chNames = { 
				"Water Depth",
				"Temperature",
				"Electrical Conductivity"
				};

		String[] dTypes = { "float64", 
				"float64", 
				"float64", 
				"float64" 
				 };

		String[] units = {
				"mm",
				"Celsius",
				"dS/m"
				};

		String[] MIMEs = { "application/octet-stream",
				"application/octet-stream", "application/octet-stream"
				};

		LinkedList<Command> tempCommandList = new LinkedList<Command>();

		Calendar startDateTime = th.now();
		Calendar endDateTime = null;

		Interval interval = new Interval(0, 0, 1, 0);// days, hours/24,
		// min/60, sec/60

		//char CR = 13;
		//char LF = 10;
		Command tempCmd = new Command("", "regularExpression", "",
				5000, 3, 1, interval);
		tempCmd.setDtSrcName("CTDSrc");
		tempCmd.setDtAddress(localDtAddress);
		tempCmd.setRemoteDtAddress(remoteDtAddress);
		tempCmd.setDelimiter("");
		tempCmd.setChNames(chNames);
		tempCmd.setDTypes(dTypes);
		tempCmd.setUnits(units);
		tempCmd.setMIMEs(MIMEs);

		tempCommandList.add(tempCmd);

		CommandList cmdList = new CommandList(name, tempCommandList,
				startDateTime, endDateTime);

		return cmdList;
	}
	
	public CommandList createSolarIRCmdList(String name) {

		String[] chNames = { "solarIR" };

		String[] dTypes = { "float64" };

		String[] units = { "W/m^2" };

		String[] MIMEs = { "application/octet-stream" };

		LinkedList<Command> tempCommandList = new LinkedList<Command>();

		Calendar startDateTime = th.now();
		Calendar endDateTime = null;

		Interval interval = new Interval(0, 0, 1, 0); // days, hours/24,
														// min/60, sec/60

		Command tempCmd = new Command("@S", "regularExpression", "", 5000, 3,
				1, interval);
		tempCmd.setDtSrcName("BoardSolarIRSrc");
		tempCmd.setDtAddress(localDtAddress);
		tempCmd.setRemoteDtAddress(remoteDtAddress);
		tempCmd.setDelimiter("");
		tempCmd.setChNames(chNames);
		tempCmd.setDTypes(dTypes);
		tempCmd.setUnits(units);
		tempCmd.setMIMEs(MIMEs);

		tempCommandList.add(tempCmd);

		CommandList cmdList = new CommandList(name, tempCommandList,
				startDateTime, endDateTime);

		return cmdList;
	}

	public CommandList createFSMCmdList(String name) {

		String[] chNames = { "FSM" };

		String[] dTypes = { "float64" };

		String[] units = { "%" };

		String[] MIMEs = { "application/octet-stream" };

		LinkedList<Command> tempCommandList = new LinkedList<Command>();

		Calendar startDateTime = th.now();
		Calendar endDateTime = null;

		Interval interval = new Interval(0, 0, 1, 0); // days, hours/24,
														// min/60, sec/60

		Command tempCmd = new Command("@F", "regularExpression", "", 5000, 3,
				1, interval);
		tempCmd.setDtSrcName("BoardFSMSrc");
		tempCmd.setDtAddress(localDtAddress);
		tempCmd.setRemoteDtAddress(remoteDtAddress);
		tempCmd.setDelimiter("");
		tempCmd.setChNames(chNames);
		tempCmd.setDTypes(dTypes);
		tempCmd.setUnits(units);
		tempCmd.setMIMEs(MIMEs);

		tempCommandList.add(tempCmd);

		CommandList cmdList = new CommandList(name, tempCommandList,
				startDateTime, endDateTime);

		return cmdList;
	}
	

}