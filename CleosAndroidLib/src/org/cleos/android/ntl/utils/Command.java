/**
 * Command.java
 * 
 * Object that contains the commands to be executed by the sensor
 * Also include information about the sensor such as interval, last and ideal time of execution
 * 
 * @author Peter Shin, Gesuri Ramirez
 * @date July 2012
 */

package org.cleos.android.ntl.utils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cleos.android.lib.Interval;

public class Command {

	private String commandString;
	private String regexPatern;
	private double Timeout;
	private int numRetries;
	private long sleepDuration;
	private String suffix;

	private Pattern pattern;
	private Matcher matcher;

	private String delimiter;
	private String[] chNames;
	private String[] dTypes;
	private String[] units;
	private String[] MIMEs;

	private String dtSrcName;
	private String dtAddress;
	private String remoteDtAddress;

	private int dtCacheSize;
	private String ctMode;
	private int dtArchiveSize;

	private Interval intervalTime;
	private Calendar lastCmdExecTime = Calendar.getInstance();
	private Calendar idealNextExecTime = Calendar.getInstance();

	public Command(String commandString, String regexPatern, String suffix,
			double timeout, int numRetries, long sleepDuration,
			Interval intervalTime) {
		this.commandString = commandString;
		this.suffix = suffix;
		this.regexPatern = regexPatern;
		Timeout = timeout;
		this.numRetries = numRetries;
		this.sleepDuration = sleepDuration;
		this.intervalTime = intervalTime;
	}

	public Calendar getLastCmdExcecTime() {
		return lastCmdExecTime;
	}

	public void setLastCmdExcecTime(Calendar last) {
		this.lastCmdExecTime = last;
	}

	public void setLastCmdExcecTime(long ms) {
		this.lastCmdExecTime.setTimeInMillis(ms);
	}

	public Interval getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(Interval intervalTime) {
		this.intervalTime = intervalTime;
	}

	public String getCommandString() {
		return commandString;
	}

	public void setCommandString(String commandString) {
		this.commandString = commandString;
	}

	public String getRegexPatern() {
		return regexPatern;
	}

	public void setRegexPatern(String regexPatern) {
		this.regexPatern = regexPatern;
	}

	public double getTimeout() {
		return Timeout;
	}

	public void setTimeout(double timeout) {
		Timeout = timeout;
	}

	public int getNumRetries() {
		return numRetries;
	}

	public void setNumRetries(int numRetries) {
		this.numRetries = numRetries;
	}

	public long getSleepDuration() {
		return sleepDuration;
	}

	public void setSleepDuration(long sleepDuration) {
		this.sleepDuration = sleepDuration;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public Matcher getMatcher() {
		return matcher;
	}

	public void setMatcher(Matcher matcher) {
		this.matcher = matcher;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String[] getChNames() {
		return chNames;
	}

	public void setChNames(String[] chNames) {
		this.chNames = chNames;
	}

	public String[] getDTypes() {
		return dTypes;
	}

	public void setDTypes(String[] chTypes) {
		this.dTypes = chTypes;
	}

	public String[] getUnits() {
		return units;
	}

	public void setUnits(String[] units) {
		this.units = units;
	}

	public String[] getMIMEs() {
		return MIMEs;
	}

	public void setMIMEs(String[] mIMES) {
		MIMEs = mIMES;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDelimiter() {
		return this.delimiter;
	}

	public String createCommandString() {
		return this.commandString + this.suffix;
	}

	public boolean validateCommandResponse(String response) {
		boolean matchfound;

		pattern = Pattern.compile(regexPatern);
		matcher = pattern.matcher(response);
		matchfound = matcher.matches();

		return (matchfound);
	}

	public String[] getdTypes() {
		return dTypes;
	}

	public void setdTypes(String[] dTypes) {
		this.dTypes = this.deepcopyStringArr(dTypes);
	}

	public String getDtSrcName() {
		return dtSrcName;
	}

	public void setDtSrcName(String dtSrcName) {
		this.dtSrcName = dtSrcName;
	}

	public String getDtAddress() {
		return dtAddress;
	}

	public String getRemoteDtAddress() {
		return remoteDtAddress;
	}

	public void setDtAddress(String dtAddress) {
		this.dtAddress = dtAddress;
	}

	public void setRemoteDtAddress(String remoteDtAddress) {
		this.remoteDtAddress = remoteDtAddress;
	}

	public int getDtCacheSize() {
		return dtCacheSize;
	}

	public void setDtCacheSize(int dtCacheSize) {
		this.dtCacheSize = dtCacheSize;
	}

	public String getCtMode() {
		return ctMode;
	}

	public void setCtMode(String ctMode) {
		this.ctMode = ctMode;
	}

	public int getDtArchiveSize() {
		return dtArchiveSize;
	}

	public void setDtArchiveSize(int dtArchiveSize) {
		this.dtArchiveSize = dtArchiveSize;
	}

	private String[] deepcopyStringArr(String[] strArray) {
		String[] copyArr = new String[strArray.length];
		for (int i = 0; i < strArray.length; i++) {
			copyArr[i] = new String(strArray[i]);
		}
		return copyArr;
	}

	public Calendar getIdealNextExecTime() {
		return idealNextExecTime;
	}

	public void setIdealNextExecTime(Calendar idealNextExecTime) {
		this.idealNextExecTime = idealNextExecTime;
	}

	public void setIdealNextExecTime(long idealTime) {
		this.idealNextExecTime.setTimeInMillis(idealTime);
	}
}
