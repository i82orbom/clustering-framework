package member.algorithm;

	
public enum SignalType{
		THRESHOLD_RAISED(1),
		ALGORITHM_END_RAISED(2);
		
		int value;
		
		SignalType(int value) {
			this.value = value;
		}
		
		int getValue(){
			return this.value;
		}
}
