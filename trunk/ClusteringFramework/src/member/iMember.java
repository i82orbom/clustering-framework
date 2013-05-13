package member;

public interface iMember {

	public int getMemberPort();
	public String getMemberAddress();
	
	enum MemberType{
		Local,
		Remote
	}
}
