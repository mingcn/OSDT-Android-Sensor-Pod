/**
 * CommandList.java
 * 
 * List of commands for sensor
 * 
 * @author Gesuri Ramirez, Peter Shin
 * 
 */

package org.cleos.android.ntl.utils;

import java.util.LinkedList;
import java.util.Calendar;

public class CommandList {

	private LinkedList<Command> commandSet = new LinkedList<Command>();

	private String name;
	private Calendar startDateTime;
	private Calendar endDateTime;

	private boolean executionStatus;

	private String logFileName;

	public CommandList(String name, LinkedList<Command> commandSet,
			Calendar startDateTime, Calendar endDateTime) {
		this.name = name;
		this.commandSet = commandSet;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;

	}

	public LinkedList<Command> getCommandSet() {
		return commandSet;
	}

	public void setCommandSet(LinkedList<Command> commandSet) {
		this.commandSet = commandSet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Calendar getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Calendar startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Calendar getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Calendar endDateTime) {
		this.endDateTime = endDateTime;
	}

	public boolean isExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(boolean executionStatus) {
		this.executionStatus = executionStatus;
	}

	public String getLogFileName() {
		return logFileName;
	}

	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}

	
	@Override
	public String toString() {

		StringBuffer concatedCommands = new StringBuffer();

		for (Command cmd : commandSet) {
			concatedCommands.append(cmd.getCommandString());
			concatedCommands.append(",");
		}
		return "CommandSet [commandSet=" + concatedCommands + "]";
	}

	public String[] getDelimiter() {
		String[] setOfDelimiters = new String[this.commandSet.size()];
		int i = 0;
		for (Command cmd : this.commandSet) {
			setOfDelimiters[i] = cmd.getDelimiter();
			i++;
		}
		return setOfDelimiters;
	}

	public String[][] getChNames() {
		String[][] setOfChNames = new String[this.commandSet.size()][];
		int i = 0;
		for (Command cmd : this.commandSet) {
			setOfChNames[i] = cmd.getChNames();
			i++;
		}
		return setOfChNames;
	}

	public String[][] getDTypes() {
		String[][] setOfDTypes = new String[this.commandSet.size()][];
		int i = 0;
		for (Command cmd : this.commandSet) {
			setOfDTypes[i] = cmd.getDTypes();
			i++;
		}
		return setOfDTypes;
	}

	public String[][] getUnits() {
		String[][] setOfUnits = new String[this.commandSet.size()][];
		int i = 0;
		for (Command cmd : this.commandSet) {
			setOfUnits[i] = cmd.getUnits();
			i++;
		}
		return setOfUnits;
	}

	public String[][] getMIMEs() {
		String[][] setOfMIMEs = new String[this.commandSet.size()][];
		int i = 0;
		for (Command cmd : this.commandSet) {
			setOfMIMEs[i] = cmd.getMIMEs();
			i++;
		}
		return setOfMIMEs;
	}

	public String[] getDTSrcNames() {
		String[] setOfDTSrcNames = new String[this.commandSet.size()];
		int i = 0;
		for (Command cmd : this.commandSet) {
			setOfDTSrcNames[i] = cmd.getDtSrcName();
			i++;
		}
		return setOfDTSrcNames;
	}

	public String[] getDTAddress() {
		String[] setOfDTAddress = new String[this.commandSet.size()];
		int i = 0;
		for (Command cmd : this.commandSet) {
			setOfDTAddress[i] = cmd.getDtAddress();
			i++;
		}
		return setOfDTAddress;
	}

	public String[] getRemoteDTAddress() {
		String[] setOfRemoteDTAddress = new String[this.commandSet.size()];
		int i = 0;
		for (Command cmd : this.commandSet) {
			setOfRemoteDTAddress[i] = cmd.getRemoteDtAddress();
			i++;
		}
		return setOfRemoteDTAddress;
	}
}
