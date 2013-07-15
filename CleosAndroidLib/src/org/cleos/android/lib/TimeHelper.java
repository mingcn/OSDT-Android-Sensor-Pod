/**
 * TimeHelper.java
 * 
 * Create calendar objects.
 * help to add two intervals
 * create intervals
 * print a calendar object in a string
 * 
 * @author Gesuri Ramirez, Peter Shin
 * @date August 2012
 */

package org.cleos.android.lib;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeHelper {

	public Calendar now() {
		return Calendar.getInstance();
	}

	public Calendar addInterval2Calendar(Calendar lastIdealSampleTime,
			Interval interval) {
		return addInterval(lastIdealSampleTime, interval.getDay(),
				interval.getHour(), interval.getMin(), interval.getSec());
	}


	public Calendar addInterval(Calendar cal1, int day, int hour, int min,
			int sec) {
		cal1.add(Calendar.DAY_OF_MONTH, day);
		cal1.add(Calendar.HOUR, hour);
		cal1.add(Calendar.MINUTE, min);
		cal1.add(Calendar.SECOND, sec);
		return cal1;
	}

	public Interval createInterval(int day, int hour, int min, int sec) {
		return new Interval(day, hour, min, sec);
	}

	public String stringPrintCal(String msg, Calendar cal) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"hh:mm:ss-SSS yyyy/MM/dd");
		return msg + ": " + formatter.format(cal.getTime());
	}

	public String stringPrintCal(Calendar cal) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"hh:mm:ss-SSS yyyy/MM/dd");
		return formatter.format(cal.getTime());
	}

	public String calendar2String(Calendar cal) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"hh:mm:ss-SSS yyyy/MM/dd");
		return formatter.format(cal.getTime());
	}

	private void prt(String str) {
		System.out.print(str);
	}

	public static void main(String[] args) {
		TimeHelper th = new TimeHelper();

		Calendar now_;
		now_ = th.now();

		th.prt("=======================");
		th.prt("This is the current time: " + th.calendar2String(now_));
		th.prt("This is the current time in mS: " + now_.getTimeInMillis());
		th.prt("=======================");
		th.prt("Creating an interval with just 0 days, 0 hours, 0 minutes, and 5 seconds");
		Interval inter = new Interval(0, 0, 0, 5);
		th.prt("Adding interval to a calendar now_");
		Calendar cal = th.addInterval2Calendar(now_, inter);
		th.prt("Calendar with the interval added");
		th.prt(th.calendar2String(cal));

	}
}
