import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;

public class Server {
	
	static ServerSocket controlSocket;
	static Socket dataSocket;
	static int port;
	static PrintWriter out;
	static BufferedReader in;
	
	
	public static void main (String ...args){
		
		boolean end = false;
		boolean error = false;
		String queryMsg;
		String answerMsg;
	
		port = Integer.parseInt("4567");
		
		try{
			controlSocket = new ServerSocket(port);
		}
		catch(IOException e){
			System.err.println("Error: cannot open port.");
			System.exit(-2);
		}
		
		do{
			try{
				dataSocket = controlSocket.accept();
				System.out.println("Connection accepted from: " + dataSocket.getInetAddress().getCanonicalHostName());
				new Service(dataSocket).start();				
			}
			catch(IOException e){
				System.err.println("Error: cannot accept connection request.");
				error = true;
			}
		}
		while(!end);
	}
	
	private static class Service extends Thread{
		Socket dataSocket;
		PrintWriter out;
		private BufferedInputStream in;
		
		public Service (Socket dataSocket){
			this.dataSocket = dataSocket;
			
			try{
				
				this.out = new PrintWriter(this.dataSocket.getOutputStream(), true);
				this.in = new BufferedInputStream(this.dataSocket.getInputStream());
			}
			catch (IOException e){
				System.err.println(this.getName() + "Error: cannot get data input stream.");
			}
		}
		
		public void run(){
			System.out.println("Serving connection");
			String queryMessage = "";
		
			
		
			System.out.print("Deserializing...");
			try {
				ObjectInputStream oi = new ObjectInputStream(dataSocket.getInputStream());
				MyRandomClass receivedObject = (MyRandomClass)oi.readObject();
				oi.close();
				
				System.out.println(receivedObject.toString());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Object sucessfully deserialized!");
			
			try{
				in.close();
				out.close();
				dataSocket.close();
			}
			catch(IOException e){
				System.err.println(this.getName() + "Error: cannot close connection.");
			}
		}
	}

}
