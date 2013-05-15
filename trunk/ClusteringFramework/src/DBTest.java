import java.net.MalformedURLException;
import java.util.List;

import FingerPrinting.computation.matching.DataPoint;
import FingerPrinting.description.MPEG7Description;
import existdb.ExistDBDriver;


public class DBTest {
	public static void main (String...args){
		ExistDBDriver db = null;
		try {
			db = new ExistDBDriver("http://localhost:8080/exist/xmlrpc");
		} catch (MalformedURLException e) {
			System.err.println("Cannot connect to DB");
			e.printStackTrace();
		}
		if (db != null){
			MPEG7Description mpg7 = null;
			int currentIDX = 0;
			while ( (mpg7 = (MPEG7Description)db.nextXML()) != null){
				System.out.println(currentIDX + " >> " + mpg7.getTitle());
				currentIDX++;
			}
			
		}
		
	}
}
