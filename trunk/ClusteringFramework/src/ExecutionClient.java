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
	

	public void sendDescriptors(){
		try {
			Socket sk = new Socket(this.requestMember.getAddress(), this.requestMember.getPort());
			ObjectOutputStream oo = new ObjectOutputStream(sk.getOutputStream());
			oo.writeObject(this.descriptorList);
			oo.flush();
			sk.close();
		} catch (UnknownHostException e) {
			System.err.println("Could not send descriptors to member, unknown host");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Could not send descriptors to member, I/O exception");
			e.printStackTrace();
		}
	}
	
	public void sendExecutionMsg() {
		String message = "EXEC_QUERY";
		if(requestMember.getMemberID() != -1) {
			Socket dSocket = null;
			try {
				dSocket = new Socket(requestMember.getAddress(), requestMember.getPort());
				DataOutputStream outToMember = new DataOutputStream(dSocket.getOutputStream());
				outToMember.writeBytes(message+'\n');
				
			} catch (UnknownHostException e1) {
				System.err.println(String.format("Could send message %s to member [%s] unknown host exception", message, requestMember.getMemberID()));
				e1.printStackTrace();
			} catch (IOException e1) {
				System.err.println(String.format("Could send message %s to member [%s] I/O Exception", message, requestMember.getMemberID()));
				e1.printStackTrace();
			}
			
		}
		else {
			System.out.println(String.format("Message not sent, member [%s]:%d is not connected", 
					requestMember.getAddress(), requestMember.getPort()));
		}
	}

}
