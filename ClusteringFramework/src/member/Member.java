package member;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import FingerPrinting.computation.matching.DataPoint;

public class Member{

	private MemberInfo info;
	
	private ServerSocket controlSocket;
	private Socket dataSocket;
	
	private Cluster cluster;
	
	public Member(int port){
		this.info = new MemberInfo(null, port, Math.random());

		try {
			this.controlSocket = new ServerSocket(this.info.getPort());			
		} catch (IOException e) {
			System.err.println("Error creating new member, port not available");
			e.printStackTrace();
		}
	}
	
	public void printClusterInfo(){
		if (this.cluster != null){
			System.out.println("===================================================");
			System.out.println("\t Cluster ID : " + this.cluster.getClusterID());
			System.out.println("\t\t Member ID : " + this.info.getMemberID());
			System.out.println("\t\t Member ADDR : " + this.info.getAddress() + ":" + this.info.getPort());
			System.out.println("===================================================");
			Iterator<MemberInfo> it = this.cluster.getMembers().iterator();
			while (it.hasNext()){
				MemberInfo member = it.next();
				if (member.getMemberID() != -1){
					System.out.println("\tMEMBER ID: " + member.getMemberID());
					System.out.println("\tMEMBER ADDR: " + member.getAddress() + ":" + member.getPort());
				}
				else{
					System.out.println("\tMEMBER ID: " + member.getMemberID());
					System.out.println("\tMEMBER ADDR: " + member.getAddress() + ":" + member.getPort() + "(NOT CONNECTED YET)");
				}
			}
			System.out.println("===================================================");
		}
		else{
			System.out.println("===================================================");
			System.out.println("\t\t Member ID : " + this.info.getMemberID());
			System.out.println("\t\t Member ADDR : " + this.info.getAddress() + ":" + this.info.getPort());
			System.out.println("NOT YET A CLUSTER MEMBER");
			System.out.println("===================================================");
		}
		
	}
	
	public void initMemberCallback(){
		try {
			while(true){

				printClusterInfo();
				System.out.println("Waiting connection...");
				
				this.dataSocket = this.controlSocket.accept();
				
				System.out.print("Connection from: " + this.dataSocket.getInetAddress().getCanonicalHostName());

				DataOutputStream outToMember = new DataOutputStream(this.dataSocket.getOutputStream());
				DataInputStream inFromMember = new DataInputStream(this.dataSocket.getInputStream());
				/** RECEIVE COMMAND */
				String command = inFromMember.readLine();
				
				if (command.equalsIgnoreCase(Command.JOIN.getValue())){
					System.out.println("JOIN request received.");
					processJoinCommand(inFromMember, outToMember);
				}
				else if (command.equalsIgnoreCase(Command.EXEC_NOTIFICATION.getValue())){ /** EXEC FROM BROTHER */
					// RECEIVE DATAPOINT
					ArrayList<DataPoint> receivedDescriptors;
					ObjectInputStream oi = new ObjectInputStream(inFromMember);
					receivedDescriptors = (ArrayList<DataPoint>)oi.readObject();
					// SEND GO
					outToMember.writeBytes("GO\n");
					// EXECUTE AND GIVE RESULT AND WATCH FOR POSSIBLE MSG FROM OTHERS
					
				}
				else if (command.equalsIgnoreCase(Command.EXEC_QUERY.getValue())){ /** EXEC QUERY FROM CLIENT */
					// RECEIVE DATAPOINT
					ArrayList<DataPoint> receivedDescriptors;
					ObjectInputStream oi = new ObjectInputStream(inFromMember);
					receivedDescriptors = (ArrayList<DataPoint>) oi.readObject();
					// SEND EXEC_NOTIFICATION TO THE REST OF THE CLUSTER
					broadcastMessage(Command.EXEC_NOTIFICATION.getValue(), this.cluster.getMembers());
					// SEND RECEIVED DATAPOINT
					ArrayList<Socket> listSockets = new ArrayList<Socket>();
					for(MemberInfo mem : this.cluster.getMembers()){
						if (mem.getMemberID() != -1){
							Socket sk = new Socket(mem.getAddress(),mem.getPort());
							ObjectOutputStream oo = new ObjectOutputStream(sk.getOutputStream());
							oo.writeObject(receivedDescriptors);
							oo.flush();
							listSockets.add(sk);
						}
					}
					// WAIT FOR GO
					for (Socket sk : listSockets){
						DataInputStream di = new DataInputStream(sk.getInputStream());
						String go = di.readLine();
						// WE SUPOSSE IT'S GO MSG
					}
					
					// EXECUTE AND WATCH FOR RESULT FROM OTHERS
					
					
				}
				else if (command.equalsIgnoreCase(Command.UPDATE_CLUSTER_STATUS.getValue())){
					processUpdateCommand(inFromMember);
				}
				else{
					System.err.println("UNRECOGNIZED COMMAND");
				}
			}
			
		} catch (IOException e) {
			System.err.println("I/O exception in member callback.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException in member callback.");
			e.printStackTrace();
		}
	}

	public void processJoinCommand(DataInputStream inFromMember, DataOutputStream outToMember){
		try {
			outToMember.writeBytes(this.info.getMemberID()+"\n");
			outToMember.writeBytes(new String(""+this.cluster.getClusterID())+"\n");
			System.out.println("SELF ID and cluster ID sent");
			
			Iterator<MemberInfo> it = this.cluster.getMembers().iterator();
			
			/**
			 * We sent everything, although we're sending information about the same member, it'll filter it
			 */
			while(it.hasNext()){
				ObjectOutputStream os = new ObjectOutputStream(outToMember);
				os.writeObject(it.next());
				os.flush();
			}
			
			System.out.println("Member info sent");


		} catch (IOException e) {
			System.err.println("Error processing join request.");
			e.printStackTrace();
		}
		
		String ack = null;
		try {
			System.out.print("Waiting for ACK...");
			ack = inFromMember.readLine();
			double newMemberId = -1;
			MemberInfo justJoinedMember = null;
			boolean done = false;
			if (ack.equalsIgnoreCase("ACK")){
				String id = inFromMember.readLine(); // RECEIVE ID FROM MEMBER
				String remotePort = inFromMember.readLine(); // RECEIVE LISTENING PORT FROM MEMBER
				int remotePortInt = Integer.parseInt(remotePort);
				newMemberId = Double.parseDouble(id);
				Iterator<MemberInfo> it = this.cluster.getMembers().iterator();
				String connectedMemberAddr = ((InetSocketAddress)this.dataSocket.getRemoteSocketAddress()).getAddress().getHostAddress();
				while(it.hasNext() && done == false){
					MemberInfo mem = it.next();
					System.out.println("MEM(" + mem.getMemberID() + ") ADD: " + mem.getAddress() + " // SOCKET ADD: " + ((InetSocketAddress)this.dataSocket.getRemoteSocketAddress()).getAddress().getHostAddress() + ":" + ((InetSocketAddress)this.dataSocket.getRemoteSocketAddress()).getPort());

					if ( (mem.getAddress().compareTo(connectedMemberAddr) == 0) && (mem.getPort() == remotePortInt)){
						mem.setMemberID(newMemberId);
						mem.setPort(remotePortInt);
						System.out.println("MEMBER INFO WRITTEN");
						done = true;
						justJoinedMember = mem;
					}
				}
				broadcastJoin(justJoinedMember);	

				System.out.println("Member accepted");
			}
		} catch (IOException e) {
			System.out.println("Expected ACK, received: " + ack);
		}
		
	}

	/**
	 * 
	 * @param exceptID represents the new member, so it's not necesary to tell him who's connected already
	 */
	public void broadcastJoin(MemberInfo justJoined){
		// GET MEMBERS
		Iterator<MemberInfo> it = this.cluster.getMembers().iterator();
		
		while (it.hasNext()){
			MemberInfo mem = it.next();
			if (mem.getMemberID() != -1 && mem.getMemberID() != justJoined.getMemberID()){
				try {
					System.out.println("Sending UPDATE command to " + mem.getMemberID());
					Socket sk = new Socket(mem.getAddress(),mem.getPort());
					DataInputStream fromMember = new DataInputStream(sk.getInputStream());
					DataOutputStream toMember = new DataOutputStream(sk.getOutputStream());
					// SEND UPDATE COMMAND

					toMember.writeBytes(new String(""+"UPDATE")+"\n");
					
					// SEND JUST JOINED MEMBER INFO
					System.out.println("Just joined: " + justJoined.getAddress() + ":" + justJoined.getPort() + " (" + justJoined.getMemberID() + ")");
					
					ObjectOutputStream os = new ObjectOutputStream(toMember);
					os.writeObject(justJoined);
					os.flush();
					sk.close();
				} catch (UnknownHostException e) {
					System.err.println("Error sending broadcastJoin to member, unknown host");
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("Error sending broadcastJoin to member, I/O exception");
					e.printStackTrace();
				}
			}
		}
	}
	
	public void processUpdateCommand(DataInputStream di){
		// HERE WE EXPECT TO RECEIVE AND OBJECT CONTAINING THE JUST JOINED MEMBER
		System.out.println("UPDATE command received...");
		ObjectInputStream oi;
		try {
			oi = new ObjectInputStream(di);
			MemberInfo receivedObject = (MemberInfo)oi.readObject();
			
			// ONCE IT'S RECEIVED THE NEW MEMBER, ADD IT TO THE LIST
			
			Iterator<MemberInfo> it = this.cluster.getMembers().iterator();
			while (it.hasNext()){
				MemberInfo member = it.next();
				if ((member.getAddress().compareTo(receivedObject.getAddress()) == 0) && member.getPort() == receivedObject.getPort()){
					member.setMemberID(receivedObject.getMemberID());
					System.out.println("JOIN PROCESSED, NEW ID ADDED " + receivedObject.getMemberID() );
				}
			}

		} catch (IOException e) {
			System.err.println("Error processing update command, I/O exception");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Error processing update command, ClassNotFound exception");
			e.printStackTrace();
		}
	}
	
	
	public void joinCluster(Set<MemberInfo> clusterMembers){
		this.cluster = new Cluster(this.info);
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
	
	public void broadcastMessage(String message, Set<MemberInfo> memberInfoSet){
		for(MemberInfo mem: memberInfoSet){
			sendMessage(message, mem);
		}
	}
	
	public void sendMessage(String message, MemberInfo destinationMember) {
		if(destinationMember.getMemberID() != -1) {
			Socket dSocket = null;
			try {
				dSocket = new Socket(destinationMember.getAddress(), destinationMember.getPort());
				DataOutputStream outToMember = new DataOutputStream(dSocket.getOutputStream());
				outToMember.writeBytes(message+'\n');
				
			} catch (UnknownHostException e1) {
				System.err.println(String.format("Could send message %s to member [%s] unknown host exception", message, destinationMember.getMemberID()));
				e1.printStackTrace();
			} catch (IOException e1) {
				System.err.println(String.format("Could send message %s to member [%s] I/O Exception", message, destinationMember.getMemberID()));
				e1.printStackTrace();
			}
			
		}
		else {
			System.out.println(String.format("Message not sent, member [%s]:%d is not connected", 
					destinationMember.getAddress(), destinationMember.getPort()));
		}
	}
	
	enum Command{
		JOIN("JOIN"),
		EXEC_QUERY("EXEC_QUERY"),
		EXEC_NOTIFICATION("EXEC_NOTIFICATION"),
		UPDATE_CLUSTER_STATUS("UPDATE");
		
		String value;
		Command(String v){
			value = v;
		}
		
		String getValue(){
			return value;
		}
	}
	
}
