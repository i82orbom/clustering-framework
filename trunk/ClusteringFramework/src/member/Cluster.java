package member;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;

public class Cluster {
	
	private Set<MemberInfo> members;
	private double clusterID;
	
	

	public double getClusterID() {
		return clusterID;
	}

	public Cluster(){
		members = null;
	}
	
	public Set<MemberInfo> getMembers() {
		return members;
	}

	public void setMembers(Set<MemberInfo> members) {
		this.members = members;
	}
	
	public void joinCluster(){
		// Try to get one cluster member info
		Iterator<MemberInfo> it = members.iterator();
		MemberInfo extractedMember = null;
		boolean memberConnected = false;
		Socket sk = null;
		while (it.hasNext()){ /** Cluster has members (maybe not connected) */
			extractedMember = it.next();
			
			/** Try connection to member */
			try {
				sk = new Socket(extractedMember.getAddress(), extractedMember.getPort());
				memberConnected = true;
			} catch (Exception e){
				/** Connection not possible */
				memberConnected = false;
			}
		
		}
		
		if (memberConnected == true){
			  try {
				DataOutputStream outToMember = new DataOutputStream(sk.getOutputStream());
				outToMember.writeBytes("JOIN");
				DataInputStream inFromMember = new DataInputStream(sk.getInputStream());
				/** Wait for response **/
				String memberResponse = inFromMember.readLine();
				if (memberResponse.equalsIgnoreCase("OK")){
					memberResponse = inFromMember.readLine();
					/** Member response should be the cluster id */
					this.clusterID = Double.parseDouble(memberResponse);
					/** Now should answer with the IDs of the rest of the cluster */
					boolean done = false;
					
					while (!done){
						try{
							ObjectInputStream oi = new ObjectInputStream(sk.getInputStream());
							MemberInfo receivedObject = (MemberInfo)oi.readObject();
							oi.close();

							Iterator<MemberInfo> it2 = this.members.iterator();
							while (it2.hasNext()){
								MemberInfo mem = it2.next();
								if (mem.getAddress() == receivedObject.getAddress() && mem.getPort() == receivedObject.getPort()){
									mem.setMemberID(mem.getMemberID());
								}
							}
						}
						catch (IOException e){
							done = true;
						}
					}
					
					outToMember.writeBytes("ACK");
					
				}
			} catch (Exception e) {
				System.err.println("Error getting output stream to member.");
				e.printStackTrace();
			}

		}
		else{
			// Create new cluster
			createNewCluster();
		}
		
	}
	
	private void createNewCluster(){
		// At this point none of the Members inside MemberInfo should be online
		this.clusterID = Math.random();
		System.out.println("New cluster [" + this.clusterID + "] created.");
	}
}
