/**
 * Write2File.java
 * 
 * write string in a file
 * if time option is specified and the file remain open for more than a day, a new string will will be written in a new file
 * 
 * @author Gesuri Ramirez
 * @date July 2012
 */

package org.cleos.android.lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Environment;

public class Write2File {
	private String folderName;
	private String fileName;
	private File root;
	private File gpxfile;
	private Calendar createdFileCalendar;
	private TimeHelper th;
	boolean newFileWithTime = false;
	private static String formatString4LogName = "MMdd_HHmmss";

	private static final SimpleDateFormat format = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss-SSS");
	private static final SimpleDateFormat logNameFormat = new SimpleDateFormat(
			formatString4LogName);

	// constructor to crea a new file.
	public Write2File(String folderName, String fileName) {
		timeCalledW2F();
		this.folderName = folderName;
		this.fileName = fileName;
		checkCreateFolder();
		gpxfile = new File(root, fileName);
	}// Write2File("FolderName","fileName")

	// constructor with option to create new file after midnight
	public Write2File(String folderName, String fileName,
			boolean newFileWithTime) {
		timeCalledW2F();
		this.newFileWithTime = newFileWithTime;
		this.folderName = folderName;
		if (this.newFileWithTime) {
			this.fileName = logNameFormat.format(new Date()) + "_" + fileName;
			// this.createdFileCalendar =
		} else
			this.fileName = fileName;
		checkCreateFolder();
		gpxfile = new File(root, this.fileName);
	}

	// constructor using default file name
	public Write2File(String folder) {
		timeCalledW2F();
		this.folderName = folder;
		this.fileName = logNameFormat.format(new Date()) + "_log.txt";
		checkCreateFolder();
		gpxfile = new File(root, fileName);
	}

	// default constructor
	public Write2File() {
		timeCalledW2F();
		folderName = "Write2File";
		fileName = "Log.txt";
		checkCreateFolder();
		gpxfile = new File(root, fileName);
	}

	private void timeCalledW2F() {
		this.th = new TimeHelper();
		this.createdFileCalendar = th.now();
	}

	public void setNewFileWithTime(boolean boo) {
		this.newFileWithTime = boo;
	}

	private boolean checkCreateFolder() {
		root = new File(Environment.getExternalStorageDirectory(), folderName);
		if (!root.exists()) {
			root.mkdirs();
			return true;
		} else
			return false;
	}

	// write a string and at the end insert a LN
	public boolean writeln(String str) {
		try {
			BufferedWriter bW = new BufferedWriter(
					new FileWriter(gpxfile, true));
			bW.write(str);
			bW.newLine();
			bW.flush();
			bW.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// write the time each time that a new string is wrote
	public boolean writelnT(String str) {
		if (this.newFileWithTime) {
			int timeField = Calendar.DAY_OF_YEAR;
			if (this.createdFileCalendar.get(timeField) != th.now().get(
					timeField)) {
				updateSetup();
			}
		}
		return writeln(format.format(new Date()) + "--> " + str);
	}

	private void updateSetup() {
		int startIndex = Write2File.formatString4LogName.length() + 1;
		String justFileName = this.fileName.substring(startIndex);
		timeCalledW2F();
		this.fileName = logNameFormat.format(new Date()) + "_" + justFileName;
		checkCreateFolder();
		this.gpxfile = new File(this.root, this.fileName);
	}

} // end class Write2File
