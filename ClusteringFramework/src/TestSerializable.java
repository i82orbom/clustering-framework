import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;


public class TestSerializable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client cl = null;
		try {
			cl = new Client("localhost", 4567);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MyRandomClass obj = new MyRandomClass("Meh", "Moh");


		try {
			cl.sendToServer(obj);
			System.out.println("Object serialized...");
			System.out.println("Object sent!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
