/**
 * Interval.java
 * 
 * Class to define the time interval while the sensor is sleeping.
 * The minimum interval can be 1 second and the maximum can be 1 day with 23 hours, 59 minutes, and 59 seconds.
 * 
 * 
 * @author Gesuri Ramirez, Peter Shin
 * @date August 2012
 */
package org.cleos.android.lib;

import java.util.Calendar;

public class Interval {

	private int day;
	private int hour;
	private int min;
	private int sec;

	public Interval(int day, int hour, int min, int sec) {
		this.day = day;
		this.hour = hour;
		this.min = min;
		this.sec = sec;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	private long secInMs() {
		return sec * 1000L;
	}

	private long minInMs() {
		return min * 60L * 1000L;
	}

	private long hourInMs() {
		return hour * 60L * 60L * 1000L;
	}

	private long dayInMs() {
		return day * 24L * 60L * 60L * 1000L;
	}

	// return the time in milliseconds
	public long toMs() {
		return dayInMs() + hourInMs() + minInMs() + secInMs();
	}

	// return the interval in a Calendar object
	public Calendar toCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(toMs());
		return cal;
	}

}
