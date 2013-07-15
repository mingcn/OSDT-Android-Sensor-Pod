/**
 * DataParser.java
 * 
 * Receive a data line a parse it
 * 
 * @author Peter Shin
 * @date July 2012
 */

package org.cleos.ntl.datalineprocessor4remotedt.lib;

import java.util.StringTokenizer;

public class DataParser {

	StringTokenizer st;
	String delimiter;

	public DataParser() {
		this.delimiter = ",";
	}

	public DataParser(String del) {
		this.delimiter = del;
	}

	public String[] getStringData(String line, String del) {

		st = new StringTokenizer(line, del);

		String[] data = new String[st.countTokens()];

		for (int i = 0; i < data.length; i++) {
			data[i] = st.nextToken();
		}
		st = null;
		return data;
	}

	public boolean checkDelimiterCount(String dLine, String del, int count) {

		st = new StringTokenizer(dLine, del);

		if (st.countTokens() == count) {
			st = null;
			return true;
		} else {
			st = null;
			return false;
		}
	}

	public boolean checkDoubleTypeData(String data) {
		try {
			Double.parseDouble(data);
		} catch (NumberFormatException ne) {
			ne.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean checkIntTypeData(String data) {
		try {
			Integer.parseInt(data);
		} catch (NumberFormatException ne) {
			ne.printStackTrace();
			return false;
		}
		return true;
	}

}