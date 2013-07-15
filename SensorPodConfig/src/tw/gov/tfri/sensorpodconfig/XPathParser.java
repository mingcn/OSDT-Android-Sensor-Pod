package tw.gov.tfri.sensorpodconfig;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.widget.Toast;

public class XPathParser {

	public String getDdataFromXML(File f, String expression){
		//loading the XML document from a file
        DocumentBuilderFactory builderfactory = DocumentBuilderFactory.newInstance();
        builderfactory.setNamespaceAware(true);

        
        DocumentBuilder builder = null;
		try {
			builder = builderfactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        Document xmlDocument = null;
        
		try {
			xmlDocument = builder.parse(f);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        XPathFactory factory = javax.xml.xpath.XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        
        XPathExpression xPathExpression = null;
		try {
			xPathExpression = xPath.compile(expression);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        String result = "faggot";
		try {
			result = xPathExpression.evaluate(xmlDocument, XPathConstants.STRING).toString();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        return result;
	}

}
