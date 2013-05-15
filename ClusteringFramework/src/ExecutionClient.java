import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import member.MemberInfo;

import org.xml.sax.SAXException;

import FingerPrinting.computation.matching.DataPoint;
import FingerPrinting.description.MPEG7Description;



public class ExecutionClient {

	private MPEG7Description mpeg7;
	private ArrayList<DataPoint> descriptorList;
	private MemberInfo requestMember;
	private Socket openSocket;
	
	public void loadMPEG7(String xmlFile){
		mpeg7 = new MPEG7Description();
		try {
			mpeg7.loadFromFile(xmlFile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		descriptorList = (ArrayList<DataPoint>) mpeg7.getData();
	}
	

	private void sendDescriptors(){
		try {
			ObjectOutputStream oo = new ObjectOutputStream(openSocket.getOutputStream());
			oo.writeObject(this.descriptorList);
			oo.flush();
		} catch (UnknownHostException e) {
			System.err.println("Could not send descriptors to member, unknown host");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Could not send descriptors to member, I/O exception");
			e.printStackTrace();
		}
	}
	
	private void sendExecutionMsg() {
		String message = "EXEC_QUERY";
			
			try {
				openSocket = new Socket(requestMember.getAddress(), requestMember.getPort());
				DataOutputStream outToMember = new DataOutputStream(openSocket.getOutputStream());
				outToMember.writeBytes(message+'\n');
				
			} catch (UnknownHostException e1) {
				System.err.println(String.format("Could send message %s to member [%s] unknown host exception", message, requestMember.getMemberID()));
				e1.printStackTrace();
			} catch (IOException e1) {
				System.err.println(String.format("Could send message %s to member [%s] I/O Exception", message, requestMember.getMemberID()));
				e1.printStackTrace();
			}
			
	}
	
	public void exec(MemberInfo inMember){
		this.requestMember = inMember;
		sendExecutionMsg();
		sendDescriptors();
	}

}
