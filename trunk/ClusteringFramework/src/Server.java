import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


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
		BufferedReader in;
		
		public Service (Socket dataSocket){
			this.dataSocket = dataSocket;
			
			try{
				
				this.out = new PrintWriter(this.dataSocket.getOutputStream(), true);
				this.in = new BufferedReader(new InputStreamReader(this.dataSocket.getInputStream()));
			}
			catch (IOException e){
				System.err.println(this.getName() + "Error: cannot get data input stream.");
			}
		}
		
		public void run(){
			String queryMessage = "";
			String responseMessage = "";
			
			try{
				queryMessage = in.readLine();
			}
			catch (IOException e){
				System.err.println(this.getName() + " Error: cannot read message.");
			}
			
			responseMessage = "Answer: MEH: " + queryMessage;
			
			out.println(responseMessage);
			
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
