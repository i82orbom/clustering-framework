import member.MemberInfo;


public class ExecTest {
	public static void main (String...args){
		ExecutionClient cl = new ExecutionClient();
		cl.loadMPEG7("/Users/psylock/Documents/workspaceAudio/JavaShazam/rec.xml");
		cl.exec(new MemberInfo("169.254.169.23", 4000,-1));
		
		// WAIT FOR RESULT
	}
}
