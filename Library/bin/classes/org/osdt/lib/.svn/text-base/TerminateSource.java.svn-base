
/*
   Copyright 2011 Erigo Technologies LLC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/******************************************************************************
 * Utility class to terminate an RBNB source.
 * <p>
 *
 * @author John P. Wilson
 *
 * @version 05/31/2011
 */

/*
 * Copyright 2011 Erigo Technologies LLC
 * All Rights Reserved
 *
 *   Date      By	Description
 * MM/DD/YYYY
 * ----------  --	-----------
 * 05/31/2011  JPW	Created.
 *
 */

package org.osdt.lib;

import com.rbnb.api.Controller;
import com.rbnb.api.Rmap;
import com.rbnb.api.Server;

public class TerminateSource {
    /**
     * Constructor
     *
     * Requires 2 input arguments: server address and source to terminate.
     **/
    public static void main(String[] argsI) {
	if (argsI.length != 2) {
	    printUsage();
	    System.exit(1);
	}
	String rbnbAddr = argsI[0];
	String sourceName = argsI[1];
	try {
	    stopSource(rbnbAddr,sourceName);
	} catch (Exception e) {
	    System.err.println("Caught exception trying to terminate source \"" + sourceName + "\" in server \"" + rbnbAddr + "\":\n" + e);
	}
    }
    
    /**
     * Print useage.
     **/
    public static void printUsage() {
	System.err.println("Usage:");
	System.err.println("    java -cp .;<path to rbnb.jar> TerminateSource <DT addr> <name of source to terminate>");
    }

    /**
     * Terminate the source.
     */
    public static void stopSource(String rbnbAddrI, String sourceNameI) throws Exception {
	
	// Make a connection to the RBNB
	Server server = Server.newServerHandle("DTServer", rbnbAddrI);
	Controller controller = server.createController("tempController");
	controller.start();
	Rmap tempRmap =
	    Rmap.createFromName(
		sourceNameI + Rmap.PATHDELIMITER + "...");
	tempRmap.markLeaf();
	Rmap rmap = controller.getRegistered(tempRmap);
	if (rmap == null) {
	    controller.stop();
	    return;
	}
	// Get rid of all the unnamed stuff in the Rmap hierarchy
	rmap = rmap.toNameHierarchy();
	if (rmap == null) {
	    controller.stop();
	    return;
	}
	System.err.println(
	    "\nTerminateSource.stopSource(): Full Rmap =\n" +
	    rmap +
	    "\n");
	Rmap startingRmap = rmap.findDescendant(sourceNameI,false);
	if (startingRmap == null) {
	    controller.stop();
	    return;
	}
	System.err.println(
	    "\nTerminateSource.stopSource(): Starting Rmap =\n" +
	    startingRmap +
	    "\n");
	try {
	    // If the client is a Source, clear the keep cache flag.  This will
	    // ensure that the RBO will actually go away.
	    if (startingRmap instanceof com.rbnb.api.Source) {
		((com.rbnb.api.Source) startingRmap).setCkeep(false);
	    }
	    controller.stop((com.rbnb.api.Client)startingRmap);
	} catch (Exception e) {
	    controller.stop();
	    throw e;
	}
	controller.stop();
	
    }
}
