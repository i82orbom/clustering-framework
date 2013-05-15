package member.algorithm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import FingerPrinting.computation.matching.DataPoint;
import FingerPrinting.computation.matching.Matcher;
import FingerPrinting.description.MPEG7Description;
import existdb.ExistDBDriver;

public class DistributedAlgorithm {

	private SignalType signalTypeReached;
	private ArrayList<Socket> restOfCluster;
	private List<DataPoint> sample;
	private int MATCH_THRESHOLD = 60;
	private ExistDBDriver dbDriver;
	private Socket parent;
	private String titleResult;
	private int scoreResult;
	
	public SignalType getSignalType(){
		return this.signalTypeReached;
	}
	
	public DistributedAlgorithm(Socket parent, ArrayList<Socket> restOfCluster, boolean isParent, List<DataPoint> sample){
		try {
			this.dbDriver = new ExistDBDriver("http://localhost:8080/exist/xmlrpc");
		} catch (MalformedURLException e) {
			System.out.println("Cannot connect to DB");
			e.printStackTrace();
		}
		this.restOfCluster = restOfCluster;
		this.sample = sample;
		this.parent = parent;
		if (isParent){
			executeAsParent();
		}
		else{
			executeAsBrother();
		}
	}
	
	private void executeAsParent(){
		MPEG7Description mpg7 = null;
		int currentIDX = 0;
		boolean done = false;
		this.signalTypeReached = SignalType.ALGORITHM_END_RAISED;
		while ( (mpg7 = (MPEG7Description)this.dbDriver.nextXML()) != null && handleIncomingMsgs(100) == false && done == false){
			
			List<DataPoint> dp = mpg7.getData();
			
			int score = Matcher.match(dp, this.sample);
			
			if (score > this.scoreResult){
				this.scoreResult = score;
				this.titleResult = mpg7.getTitle();
			}
			
			if (this.scoreResult >= this.MATCH_THRESHOLD){
				// END COMPUTATION, TELL OTHERS
				signalTypeReached = SignalType.THRESHOLD_RAISED;
				done = true;
			}

			currentIDX++;
			
		}
		
		if (this.signalTypeReached == SignalType.ALGORITHM_END_RAISED){
			// WAIT FOR THE RESULT FROM OTHERS, OR WAIT FROM THRESHOLD_RAISED FROM OTHERS
			String receivedTitle;
			int receivedScore = -1;
			for (Socket sk : this.restOfCluster){
				DataInputStream di;

				try {
					di = new DataInputStream(sk.getInputStream());
					di.readLine(); // SKIP THE ALGORITHM END RAISED OR THRESHOLD RAISED FROM OTHER MEMBERS
					receivedTitle = di.readLine();
					receivedScore = Integer.parseInt(di.readLine());
					
					if (receivedScore > this.scoreResult){
						this.scoreResult = receivedScore;
						this.titleResult = receivedTitle;
					}

				} catch (Exception e) {
				
				} 
			}
			
		
		}
		else if (this.signalTypeReached == SignalType.THRESHOLD_RAISED && !done){ /** Threshold raised it's from other member */
			// GET RESULT
			DataInputStream di;
			try {
				di = new DataInputStream(this.thresholdMemberSocket.getInputStream());
				this.titleResult = di.readLine();
				this.scoreResult = Integer.parseInt(di.readLine());
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			
		}
		else if (this.signalTypeReached == SignalType.THRESHOLD_RAISED && done){ /** This threshold raised it's from this member (parent) */
			// TELL THE OTHERS, DON'T EXPECT RESULT
				for (Socket sk : this.restOfCluster){
						try {
							DataOutputStream dout = new DataOutputStream(sk.getOutputStream());
							dout.writeBytes("THRESHOLD_RAISED\n");
						} catch (Exception e) {
					} 
				}
				
		}
		
		
	}
	private Socket thresholdMemberSocket;
	
	private boolean handleIncomingMsgs(int timeout){
		for (Socket sk : this.restOfCluster){
			try {
				if (timeout != 0)
					sk.setSoTimeout(timeout);
				DataInputStream di = new DataInputStream(sk.getInputStream());
				String msg = di.readLine();
				if (msg.equalsIgnoreCase("THRESHOLD_RAISED")){
					this.thresholdMemberSocket= sk;
					return true;
				}
				else if (msg.equalsIgnoreCase("ALGORITHM_END_RAISED")){
					String title = di.readLine();
					int score = Integer.parseInt(di.readLine());
					
					if (this.scoreResult < score){
						this.scoreResult = score;
						this.titleResult = title;
					}
					return false;
				}
			} catch (Exception e) {
				/** NO MSG FROM MEMBER */
			} 
		}
		return false;
	}
	
	private void executeAsBrother(){
		
		MPEG7Description mpg7 = null;
		int currentIDX = 0;
		boolean done = false;
		this.signalTypeReached = SignalType.ALGORITHM_END_RAISED;
		while ( (mpg7 = (MPEG7Description)this.dbDriver.nextXML()) != null && handleIncomingMsgs(100) == false && done == false){
			
			List<DataPoint> dp = mpg7.getData();
			
			int score = Matcher.match(dp, this.sample);
			
			if (score > this.scoreResult){
				this.scoreResult = score;
				this.titleResult = mpg7.getTitle();
			}
			
			if (this.scoreResult >= this.MATCH_THRESHOLD){
				// END COMPUTATION, TELL OTHERS
				signalTypeReached = SignalType.THRESHOLD_RAISED;
				done = true;
			}

			currentIDX++;
			
		}
		
		if (this.signalTypeReached == SignalType.ALGORITHM_END_RAISED){
			// TELL RESULT TO PARENT
			DataOutputStream dout;
			try {
				dout = new DataOutputStream(this.parent.getOutputStream());
				dout.writeBytes("ALGORITHM_END_RAISED\n");
				dout.writeBytes(this.titleResult+"\n");
				dout.writeBytes(this.scoreResult+"\n");

				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (this.signalTypeReached == SignalType.THRESHOLD_RAISED  && !done){
			// DO NOTHING
		}
		else if (this.signalTypeReached == SignalType.THRESHOLD_RAISED  && done){
			// TELL RESULT TO PARENT
			DataOutputStream dout;
			try {
				dout = new DataOutputStream(this.parent.getOutputStream());
				dout.writeBytes("THRESHOLD_RAISED\n");
				dout.writeBytes(this.titleResult+"\n");
				dout.writeBytes(this.scoreResult+"\n");

				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
