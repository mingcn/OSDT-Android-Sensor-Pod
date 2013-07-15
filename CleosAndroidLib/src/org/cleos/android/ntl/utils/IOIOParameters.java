/**
 * IOIOParameters.java
 * 
 * Parameter for the IOIO board
 * 
 * @author Gesuri Ramirez
 * @date july 2012
 */

package org.cleos.android.ntl.utils;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;

import java.io.InputStream;
import java.io.OutputStream;

public class IOIOParameters {
	private InputStream in;
	private OutputStream out;
	private AnalogInput analogInput;
	private PwmOutput analogOutput;
	private DigitalInput digitalInput;
	private DigitalOutput digitalOutput;
	
	public IOIOParameters(InputStream in, OutputStream out){
		setNullAll();
		this.in = in;
		this.out = out;
	}
	
	public IOIOParameters(AnalogInput analogInput){
		setNullAll();
		this.analogInput = analogInput;
	}
	
	private void setNullAll(){
		this.in = null;
		this.out = null;
		this.analogInput = null;
		this.analogOutput = null;
		this.digitalInput = null;
		this.digitalOutput = null;
	}
	
	public InputStream getIn() {
		return in;
	}
	public void setIn(InputStream in) {
		this.in = in;
	}
	public OutputStream getOut() {
		return out;
	}
	public void setOut(OutputStream out) {
		this.out = out;
	}
	public AnalogInput getAnalogInput() {
		return analogInput;
	}
	public void setAnalogInput(AnalogInput analogInput) {
		this.analogInput = analogInput;
	}
	public PwmOutput getAnalogOutput() {
		return analogOutput;
	}
	public void setAnalogOutput(PwmOutput analogOutput) {
		this.analogOutput = analogOutput;
	}
	public DigitalInput getDigitalInput() {
		return digitalInput;
	}
	public void setDigitalInput(DigitalInput digitalInput) {
		this.digitalInput = digitalInput;
	}
	public DigitalOutput getDigitalOutput() {
		return digitalOutput;
	}
	public void setDigitalOutput(DigitalOutput digitalOutput) {
		this.digitalOutput = digitalOutput;
	}
}
