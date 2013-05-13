import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {

	private Socket clientSocket;
	private OutputStream outToServer;
	private InputStream inFromServer;
	
	public Client(String addr, int port) throws UnknownHostException, IOException{
		this.clientSocket = new Socket(addr, port);
		this.outToServer= this.clientSocket.getOutputStream(); 
		this.inFromServer = this.clientSocket.getInputStream();
	}
	
	public void sendToServer(MyRandomClass bos) throws IOException{
		ObjectOutputStream os = new ObjectOutputStream(this.outToServer);
		os.writeObject(bos);
		os.flush();
		os.close();
	}
	
	public void receiveFromServer(){
		
	}
	
//	public static void main (String...args) throws UnknownHostException, IOException{
//		 String sentence;
//		  String modifiedSentence;
//		  BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
//		  Socket clientSocket = new Socket("169.254.169.23", 1879);
//		  DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//		  BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//		  sentence = inFromUser.readLine();
//		  outToServer.writeBytes(sentence + '\n');
//		  modifiedSentence = inFromServer.readLine();
//		  System.out.println("FROM SERVER: " + modifiedSentence);
//		  clientSocket.close();
//		
//	}
}
