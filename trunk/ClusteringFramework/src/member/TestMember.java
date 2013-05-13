package member;

import member.iMember.MemberType;

public class TestMember {

	public static void main (String...args){
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Thread 1, running");

				MemberInstance mem = new MemberInstance(MemberType.Local,null,4080);
				
			}
		});
		th.start();
		
		Thread th2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Thread 2, running");

				MemberInstance mem = new MemberInstance(MemberType.Remote, "localhost", 4080);
				
			}
		});
		
		th2.start();
		
		
	}
}
