package member;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

class Member{

	private String address;
	private int port;
	private ServerSocket localSocket;
	private Socket remoteSocket;
	
	private Socket dataSocket;
	
	public Member(int port){ /** The Member is local */
		this.port = port;
		try {
			this.localSocket = new ServerSocket(this.port); /** If it's local, start waiting for a connection */
			this.dataSocket = this.localSocket.accept();
			System.out.println("Connection accepted from: " + this.dataSocket.getInetAddress().getCanonicalHostName());
		} catch (IOException e) {
			System.err.println("Error creating new member, port not available");
			e.printStackTrace();
		}
	}
	
	public Member(String address, int port){ /** The member is remote */
		try {
			this.remoteSocket = new Socket(address, port);
		} catch (UnknownHostException e) {
			System.err.println("Error connecting to member, unknown host specified");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error connecting to member, I/O Exception");
			e.printStackTrace();
		}

	}

	public String getAddress(){
		return this.address;
	}
	
	public int getPort(){
		return this.port;
	}
	
}
