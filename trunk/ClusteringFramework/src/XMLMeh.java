import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.transform.TransformerException;

import sound.exceptions.UnsuportedSampleRateException;
import FingerPrinting.computation.matching.DataPoint;
import FingerPrinting.computation.matching.Matcher;
import FingerPrinting.description.MPEG7Description;


public class XMLMeh {
	public static void main(String...args){
		
		rename(new File("/Users/psylock/Documents/workspaceAudio/JavaShazam/hashes/"));
	}
	
	public static void rename(File rootDirectory){
		String[] itemsInDirectory = rootDirectory.list();
		int currentIDX = 0;
		for (String itemInDirectory:itemsInDirectory){
			if (itemInDirectory.endsWith(".xml")){
				MPEG7Description original = new MPEG7Description();
				try {
					original.loadFromFile("/Users/psylock/Documents/workspaceAudio/JavaShazam/hashes/" + itemInDirectory);
					original.setOutputFileName(new String(""+currentIDX+".xml"));
					original.write();
					currentIDX++;
				}
				catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
				
			}
			
		}
	}
}	
	

