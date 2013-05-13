package member;

public class MemberInstance implements iMember{

	private MemberType type;
	private Member memberInst;
	
	public MemberInstance(MemberType memberType, String address, int port) {
		this.type = memberType;
		if (this.type == MemberType.Local){ /** If it's local we simply create the server member */
			memberInst = new Member(port);
		}
		else if (this.type == MemberType.Remote){ /** If it's remote, we have to connect to it */
			memberInst = new Member(address, port);
		}
	}

	public int getMemberPort() {
		return memberInst.getPort();
	}

	public String getMemberAddress() {
		return memberInst.getAddress();
	}


	
	
	
}
