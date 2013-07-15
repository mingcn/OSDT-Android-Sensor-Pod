package tw.gov.tfri;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class CreateConfigFile {
	
	
	public void createConfigFile(File configPath, File configFile, String remoteDT_IP, String remoteDT_port, String siteName, String uart1){
			if(!(configFile.exists())){
				if(!(configPath.exists())){
					configPath.mkdir();
					try {
						saveXML(configFile, remoteDT_IP, remoteDT_port, siteName, uart1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					try {
						saveXML(configFile, remoteDT_IP, remoteDT_port, siteName, uart1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
	}
	
	public void saveXML(File configFile, String remoteDT_IP, String remoteDT_port, String siteName, String uart1) throws IOException{
		
		Writer w = new BufferedWriter(new FileWriter(configFile));
		String s = "<?xml version=\'1.0\' encoding=\'utf-8\' standalone=\'yes\' ?>\n"
					+ "  <config>\n"
					+ "    <remoteDT_IP>" + remoteDT_IP + "</remoteDT_IP>\n"
					+ "    <remoteDT_port>" + remoteDT_port + "</remoteDT_port>\n"
					+ "    <siteName>" + siteName + "</siteName>\n"
				    + "    <uart1>" + uart1 + "</uart1>\n"
					+ "  </config>\n";
		w.write(s);
		w.close();			

		
	}

}
