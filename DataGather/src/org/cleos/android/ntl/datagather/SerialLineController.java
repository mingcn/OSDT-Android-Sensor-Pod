/**
 * SerialLineController.java
 * 
 * Control each sensor
 * Execute each command (could be more than 1 command per sensor)
 * calculate the time to sleep and wake up for the next command
 * start the RxThread per sensor
 * 
 * @author Peter Shin, Gesuri Ramirez
 * @date August 2012
 */

package org.cleos.android.ntl.datagather;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.exception.ConnectionLostException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.LinkedList;

import org.cleos.android.lib.TimeHelper;
import org.cleos.android.lib.Write2File;
import org.cleos.android.ntl.broadcasts.SendBroadcast;
import org.cleos.android.ntl.utils.Command;
import org.cleos.android.ntl.utils.CommandList;
import org.cleos.android.ntl.utils.IOIOParameters;

import android.content.Context;
import android.util.Log;

import org.cleos.android.ntl.utils.Configurator;//add by pstango

public class SerialLineController extends Thread {
	private Context context;
	private CommandList cmdList;
	private Calendar currentTime;
	private OutputStream out;
	private DataOutputStream dOut;
	private InputStream in;
	private String path;
	private Write2File log;
	private Write2File dataLog;
	private boolean abort_ = false;
	private LinkedList<Command> llCommand;
	private RxThread rxThread;
	private TimeHelper th;
	private AnalogInput analogSensor;
	private AnalogInput analogSensorExtra;
	private IOIOParameters ioioParameters;

	public SerialLineController(Context context, CommandList cmdList,
			IOIOParameters ioioParameters, String path) {
		this.context = context;
		this.cmdList = cmdList;
		this.ioioParameters = ioioParameters;
		this.in = this.ioioParameters.getIn();
		this.out = this.ioioParameters.getOut();
		this.analogSensor = this.ioioParameters.getAnalogInput();

		this.path = path;
		this.log = new Write2File(this.path, "testLog.txt", true);
		this.dataLog = new Write2File(this.path, "dataLog.txt", true);
		this.dOut = new DataOutputStream(this.out);
		this.th = new TimeHelper();

		if (this.in != null) {
			this.rxThread = new RxThread(context, this.cmdList.getName(), in,
					this.dataLog, this.log);
			this.rxThread.start();
		}
	}
	
	public SerialLineController(Context context, CommandList cmdList,
			IOIOParameters ioioParameters, IOIOParameters ioioParametersExtra, String path) {
		this.context = context;
		this.cmdList = cmdList;
		this.ioioParameters = ioioParameters;
		this.in = this.ioioParameters.getIn();
		this.out = this.ioioParameters.getOut();
		this.analogSensor = this.ioioParameters.getAnalogInput();
		this.analogSensorExtra = ioioParametersExtra.getAnalogInput();

		this.path = path;
		this.log = new Write2File(this.path, "testLog.txt", true);
		this.dataLog = new Write2File(this.path, "dataLog.txt", true);
		this.dOut = new DataOutputStream(this.out);
		this.th = new TimeHelper();

		if (this.in != null) {
			this.rxThread = new RxThread(context, this.cmdList.getName(), in,
					this.dataLog, this.log);
			this.rxThread.start();
		}
	}

	@Override
	public void run() {
		super.run();
		log.writelnT("This is a thread named: " + this.cmdList.getName());

		llCommand = cmdList.getCommandSet();
		Command nextCommand;

		initRunConf();

		while (true) {

			synchronized (this) {
				if (abort_) {
					killRxThread();
					break;
				}
			}

			printCurrentTime();

			nextCommand = findNextCommandToExecute();

			executeCommand(nextCommand);

			sleepUntilNextCommand();

		}
	}// end run

	private void initRunConf() {
		// print("This is the first time!");
		for (Command cm : llCommand) {
			cm.setLastCmdExcecTime(Calendar.getInstance().getTimeInMillis());
		}
	}

	private void printCurrentTime() {
		currentTime = Calendar.getInstance();
		log.writelnT("At this time wake-up the SLC: "
				+ th.stringPrintCal(currentTime));
	}

	private Command findNextCommandToExecute() {
		if (llCommand == null || llCommand.size() == 0) {
			return null;
		}
		if (llCommand.size() == 1) {
			return llCommand.get(0);
		}

		else {
			// go through the commands, and fine the min (offset)

			long minOffset = calculateOffset(llCommand.get(0));
			int minIndex = 0;

			Command cmd;

			for (int i = 0; i < llCommand.size(); i++) {
				cmd = llCommand.get(i);
				if (calculateOffset(cmd) < minOffset) {
					minIndex = i;
				}
			}
			return llCommand.get(minIndex);
		}
	}

	private long calculateOffset(Command cmd) {
		long offsetFromNextScheduleTime = cmd.getIdealNextExecTime()
				.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
		// print("The current time is " + offsetFromNextScheduleTime +
		// " ms away from the ideal, next scheduled time.");
		return offsetFromNextScheduleTime;
	}

	private void executeCommand(Command nextCommand) {
		// print ("Executing the command: " + nextCommand.getCommandString());
		sendCommand(nextCommand);
		nextCommand.setLastCmdExcecTime(Calendar.getInstance());
		nextCommand.setIdealNextExecTime(nextCommand.getIdealNextExecTime()
				.getTimeInMillis() + nextCommand.getIntervalTime().toMs());

	}

	private void sleepUntilNextCommand() {
		Command nextCommand = findNextCommandToExecute();
		if (calculateOffset(nextCommand) > 0)
			sleep4ms(calculateOffset(nextCommand));
	}

	private void killRxThread() {
		if (this.rxThread != null) {
			this.rxThread.abort();
		}
	}

	public String get_name() {
		return this.cmdList.getName();
	}

	private void sendCommand(Command cmd) {
		if (out != null) {
			try {
				dOut.writeBytes(cmd.createCommandString());
				log.writelnT("Command send: \"" + cmd.getCommandString() + "\"");
			} catch (IOException e) {
				log.writelnT("IOException: " + e.toString());
				Log.e("SerialLineController", "IOException: ", e);
				e.printStackTrace();
			}
		} else {
			if (cmd.createCommandString().charAt(0) == '@') {
				readOnBoardSensor(cmd);
			} else
				log.writelnT("Error: the outputStream is null or there is not command to read onboard sensor");
		}
	}

	private void readOnBoardSensor(Command cmd) {
		float reading = 0;
		try {
			reading = analogSensor.getVoltage();
		} catch (InterruptedException e) {
			log.writelnT("Error on reading analog. InterrptedException cached: "
					+ e.toString());
			Log.e(this.path,
					"Error on reading analog. InterrptedException cached: "
							+ e.toString());
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			log.writelnT("Error on reading analog. ConnectionLostException cached: "
					+ e.toString());
			Log.e(this.path,
					"Error on reading analog. ConnectionLostException cached: "
							+ e.toString());
			e.printStackTrace();
		}
		boolean error = false;
		String slcName;
		switch (cmd.createCommandString().charAt(1)) {
		case 'T':
			reading = (float) ((reading * 44.44444) - 61.11);
			//slcName = "temperature";
			slcName = Configurator.onboardTemperature;
			break;
		case 'H':
			reading = (float) ((reading * 76.24) - 40.2);
			//slcName = "humidity";
			slcName = Configurator.onboardHumidity;
			break;
		case 'V':
			reading = (float) ((reading - 2.5) / 0.0681) * -1;
			//slcName = "voltage";
			slcName = Configurator.onboardVoltage;
			break;
		case 'S':
			reading = (float)((reading * 2200) - 200);
			slcName = "SolarIR";
			break;
		case 'O':
			reading = (float)(reading / 1.250);
			slcName = "Soil";
			break;
		case 'F':
			float readingFST = 0;
			try
			{
				readingFST = analogSensorExtra.getVoltage();
				log.writelnT("Success FST");
			}
			catch (InterruptedException e) {
				log.writelnT("Error on reading analog. InterrptedException cached: "
						+ e.toString());
				Log.e(this.path,
						"Error on reading analog. InterrptedException cached: "
								+ e.toString());
				e.printStackTrace();
			} catch (ConnectionLostException e) {
				log.writelnT("Error on reading analog. ConnectionLostException cached: "
						+ e.toString());
				Log.e(this.path,
						"Error on reading analog. ConnectionLostException cached: "
								+ e.toString());
				e.printStackTrace();
			}
			
			//Using Vout from LT1168 to find Resistance value of FST
			readingFST = (float)((float)(1.25) * (float)(49400)) / readingFST; 
			
			//Using the steinhart-hart equation to use the thermistor reading to find Temperature
			double A = .001032, B = .0002378, C = .0000001580;
			readingFST = (float)(A + (B*Math.log(readingFST)) + ( C * Math.pow( (Math.log(readingFST)), 3) ));
			readingFST = 1 / readingFST;
			
			if(reading < 1.0 && reading >= 0.51)
			{
				reading = (float)( 21.0606 + (0.005565*reading*reading) - (0.00035*reading) - (0.483199*reading) ) ;
			}
			else if (reading < 0.51 && reading >= 0.11)
			{
				reading = (float)( 2.22749 + (0.160107*reading) - (0.01478*readingFST) );
			}
			else //reading < 0.11
			{
				reading = (float)( 0.3299 + (0.281073*reading) - (0.00578*reading*readingFST) );
			}
			slcName = "FSM";
			break;

		default:
			error = true;
			slcName = "";
			break;
		}
		if (error == false) {
			String dl = Float.toString(reading);
			dataLog.writelnT(dl);
			SendBroadcast.sendData2DLP(context, slcName, dl);
			SendBroadcast.sendData2DLP4RDT(context, slcName, dl);
		} else {
			// dataLog.writelnT("No definition for the analog sensor: "+cmd.createCommandString().charAt(1));
			log.writelnT("No definition for the analog sensor: "
					+ cmd.createCommandString().charAt(1));
		}
	}

	private void sleep4ms(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			abort();
			e.printStackTrace();
		}
	}

	synchronized public void abort() {
		abort_ = true;
	}

}
