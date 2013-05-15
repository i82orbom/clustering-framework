package existdb;

import groovyx.net.ws.cxf.SSLHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.xml.sax.SAXException;

import FingerPrinting.description.MPEG7Description;
import FingerPrinting.description.XMLFile;

public class ExistDBDriver {

    private String uri;
    private SSLHelper helper;

    private XmlRpcClient client;
    private XmlRpcClientConfigImpl config;
    private HashMap<String, String> options;
    
    private final String xmlFolderDB = "/db/fingerprinting/";
    private final int NUMBER_XML = 101;
    private int currentXMLIdx = 0;
    
    public ExistDBDriver(String uri) throws MalformedURLException{
    	this.uri = uri;
    	// Initialize HTTPS connection to accept selfsigned certificates
        // and the Hostname is not validated 
        this.helper = new SSLHelper();
        this.helper.initialize();
        
        
        this.client = new XmlRpcClient();
        this.config = new XmlRpcClientConfigImpl();
        this.config.setServerURL(new URL(this.uri));
        this.config.setBasicUserName("admin");
        this.config.setBasicPassword("1");
        client.setConfig(config);
        
        this.options = new HashMap<String, String>();
        this.options.put("indent", "yes");
        this.options.put("encoding", "UTF-8");
        this.options.put("expand-xincludes", "yes");
        this.options.put("process-xsl-pi", "no");
    }
    
    public boolean hasNext(){
    	if (currentXMLIdx <= NUMBER_XML)
    		return true;
    	else
    		return false;
    }
    
    public XMLFile nextXML(){
    	if (hasNext()){
    		Vector<Object> params = new Vector<Object>();
    		params.addElement( this.xmlFolderDB + new String(this.currentXMLIdx + ".xml") ); 
    		params.addElement( options );
    		this.currentXMLIdx++;
    		String xml = null;
    		try {
    			xml = (String)client.execute( "getDocumentAsString", params );
    			PrintWriter pw = new PrintWriter(new File("tempXML.out"));
    			pw.print(xml);
    			pw.close();
    			MPEG7Description xmlFile = new MPEG7Description();
    			xmlFile.loadFromFile("tempXML.out");
    			File f = new File("tempXML.out");
    			f.delete();
    			return xmlFile;	
    		} catch (XmlRpcException e) {
    			return null;
    		} catch (FileNotFoundException e) {
				System.out.println("Cannot read xml temp file");
				e.printStackTrace();
				return null;
			} catch (ParserConfigurationException e) {
				System.out.println("Cannot read xml temp file");
				e.printStackTrace();
				return null;
			} catch (SAXException e) {
				System.out.println("Cannot read xml temp file");
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				System.out.println("Cannot read xml temp file");
				e.printStackTrace();
				return null;
			}
    	}
    	else
    		return null;
    }
    
}
