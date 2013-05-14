package member;

import java.util.HashSet;
import java.util.Set;


public class TestMember {

	public static void main (String...args) throws InterruptedException{
		
	
		Set<MemberInfo> members = new HashSet<MemberInfo>();
		members.add(new MemberInfo("localhost", 4000));
		members.add(new MemberInfo("localhost", 4001));
		members.add(new MemberInfo("localhost", 4002));
				
		Member member = new Member(3999);
		member.joinCluster(members);
		member.initMemberCallback();
			
		
	}
}
