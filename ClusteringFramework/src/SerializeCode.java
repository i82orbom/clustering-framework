import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class SerializeCode {
	public static void main(String[] args) {
	    String filename = "serializedMyRandomClass.ser";
	    MyRandomClass p = new MyRandomClass("Tabs", "Cina");

	    /** Serializing the object to file on filename***/
	    // Save the object to file
	    FileOutputStream fos = null;
	    ObjectOutputStream out = null;
	    
	    try {
	    	System.out.println("Serializing object " + p.toString() + "... into " + filename );
	      fos = new FileOutputStream(filename);
	      out = new ObjectOutputStream(fos);
	      out.writeObject(p);
	      out.close();
	      
	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }
	    
	    /** Recovering serialized object from file***/
	    // Read the object from file
	    // Save the object to file
	    System.out.println("Restoring serialized object ..." );
	    FileInputStream fis = null;
	    ObjectInputStream in = null;
	    try {
	      fis = new FileInputStream(filename);
	      in = new ObjectInputStream(fis);
	      p = (MyRandomClass) in.readObject();
	      out.close();
	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }
	    System.out.println(p);
	  }
}
