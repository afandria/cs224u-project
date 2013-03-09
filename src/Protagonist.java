
public class Protagonist {
	public static CountEvents countEvents = null;
	
	int pro;
	public Protagonist(int s) {
		pro = s;
	}
	
	public String toString() {
		return pro + ":" + countEvents.proIDToStringList.get(pro);
	}
}
