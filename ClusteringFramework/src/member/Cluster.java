package member;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;

public class Cluster {
	
	private Set<MemberInfo> members;
	private double clusterID;
	private MemberInfo clusterCaller; /* Because members has the rest of the members in the cluster, this variable stores the other member */
	

	public double getClusterID() {
		return clusterID;
	}

	public Cluster(MemberInfo caller){
		members = null;
		this.clusterCaller = caller;
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
				sk = new Socket();
				sk.connect(new InetSocketAddress(extractedMember.getAddress(), extractedMember.getPort()), 1000);
				memberConnected = true;
			} catch (Exception e){
				/** Connection not possible */
				memberConnected = false;
				System.out.println("No member connected...");
			}
		
		}
		
		if (memberConnected == true){
			System.out.println("Some member is connected!");
			  try {
				DataOutputStream outToMember = new DataOutputStream(sk.getOutputStream());
				outToMember.writeBytes("JOIN\n");
				System.out.println("JOIN request sent.");
				
				DataInputStream inFromMember = new DataInputStream(sk.getInputStream());
				/** Wait for response **/
				String memberResponse = inFromMember.readLine();
				if (memberResponse.equalsIgnoreCase("OK\n")){
					memberResponse = inFromMember.readLine();
					/** Member response should be the cluster id */
					this.clusterID = Double.parseDouble(memberResponse);
					/** Now should answer with the IDs of the rest of the cluster */
					boolean done = false;
					
					System.out.println("Received cluster id: " + this.clusterID);
					
					while (!done){
						try{
							sk.setSoTimeout(100);
							ObjectInputStream oi = new ObjectInputStream(sk.getInputStream());
							MemberInfo receivedObject = (MemberInfo)oi.readObject();

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
					System.out.println("Received member info.");
					outToMember.writeBytes("ACK\n");
					System.out.println("ACK sent.");
					outToMember.writeBytes(new String(""+this.clusterCaller.getMemberID()));
					System.out.println("SELF ID SENT.");

					
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
