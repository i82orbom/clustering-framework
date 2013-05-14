package member;

import java.util.HashSet;
import java.util.Set;


public class TestMember {

	public static void main (String...args) throws InterruptedException{
		
		Set<MemberInfo> members = new HashSet<MemberInfo>();
		members.add(new MemberInfo("169.254.169.23", 4000, -1));
		members.add(new MemberInfo("localhost", 4000, -1));

				
		Member member = new Member(4001);
		member.joinCluster(members);
		member.initMemberCallback();
	}
}
