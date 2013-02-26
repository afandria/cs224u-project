
public class Event {
	public static CountEvents countEvents = null;
	
	// Maybe this should be verb, argument-type (subject/object)

	public int verb;
	public boolean argType;
	public Event(int v, boolean a) {
		verb = v;
		argType = a;
	}
	
	public Event getComplementEvent() {
		return new Event(verb, !argType);
	}
	
	public int getLargeIndex() {
		return 2 * verb + (argType ? 0 : 1);
	}
}
