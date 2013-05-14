
public class ExecTest {
	public static void main (String...args){
		ExecutionClient cl = new ExecutionClient();
		cl.loadMPEG7("");
		cl.sendExecutionMsg();
		cl.sendDescriptors(); // ONCE THE DESCRIPTORS ARE SENT; THE EXECUTION IS MADE IN THE SERVER
		
		// WAIT FOR RESULT
	}
}
