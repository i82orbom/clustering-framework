package member;

import java.util.HashSet;
import java.util.Set;


public class TestMember {

	public static void main (String...args) throws InterruptedException{
		
		Set<MemberInfo> members = new HashSet<MemberInfo>();
		members.add(new MemberInfo("127.0.0.1", 4001, -1));
		members.add(new MemberInfo("127.0.0.1", 4002, -1));
		members.add(new MemberInfo("127.0.0.1", 4003, -1));

				
		Member member = new Member(4000);
		member.joinCluster(members);
		member.initMemberCallback();
	}
}
