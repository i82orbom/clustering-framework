package member;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;

public class Member{

	private MemberInfo info;
	
	private ServerSocket controlSocket;
	private Socket dataSocket;
	
	private Cluster cluster;
	
	public Member(int port){
		this.info = new MemberInfo(null, port);
		this.info.setMemberID(Math.random());

		try {
			this.controlSocket = new ServerSocket(this.info.getPort());			
		} catch (IOException e) {
			System.err.println("Error creating new member, port not available");
			e.printStackTrace();
		}
	}
	
	public void initMemberCallback(){
		try {
			this.dataSocket = this.controlSocket.accept();
			
			DataOutputStream outToMember = new DataOutputStream(this.dataSocket.getOutputStream());
			DataInputStream inFromMember = new DataInputStream(this.dataSocket.getInputStream());
			/** RECEIVE COMMAND */
			String command = inFromMember.readLine();
			if (command.equalsIgnoreCase(Command.JOIN.getValue())){
				processJoinCommand(inFromMember, outToMember);
			}
			else if (command.equalsIgnoreCase(Command.EXEC.getValue())){
				
			}
			
			
			System.out.println("Connection accepted from: " + this.dataSocket.getInetAddress().getCanonicalHostName());
		} catch (IOException e) {
			System.err.println("I/O exception in member callback.");
			e.printStackTrace();
		}
	}

	public void processJoinCommand(DataInputStream inFromMember, DataOutputStream outToMember){
		try {
			outToMember.writeBytes("OK");
			outToMember.writeBytes(new String(""+this.cluster.getClusterID()));
			
			Iterator<MemberInfo> it = this.cluster.getMembers().iterator();
			
			while(it.hasNext()){
				ObjectOutputStream os = new ObjectOutputStream(outToMember);
				os.writeObject(it.next());
				os.flush();
				os.close();
			}

		} catch (IOException e) {
			System.err.println("Error processing join request.");
			e.printStackTrace();
		}
		
		String ack = null;
		try {
			ack = inFromMember.readLine();
			if (ack.equalsIgnoreCase("ACK")){
				System.out.println("Member accepted");
			}
		} catch (IOException e) {
			System.out.println("Expected ACK, recived: " + ack);
		}
		
		
	}

	public void joinCluster(Set<MemberInfo> clusterMembers){
		this.cluster = new Cluster();
		this.cluster.setMembers(clusterMembers);
		
		/** Try to join */
		this.cluster.joinCluster();
	}

	public String getAddress(){
		return this.info.getAddress();
	}
	
	public int getPort(){
		return this.info.getPort();
	}
	
	enum Command{
		JOIN("JOIN"),
		EXEC("EXEC");
		
		String value;
		Command(String v){
			value = v;
		}
		
		String getValue(){
			return value;
		}
	}
	
}
