package member;

import java.io.Serializable;

public class MemberInfo implements Serializable{

	private double memberID;
	private int port;
	private String address;


	public MemberInfo(String address, int port){
		this.address = address;
		this.port = port;
	}
	
	public double getMemberID() {
		return memberID;
	}
	public void setMemberID(double memberID) {
		this.memberID = memberID;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

}
