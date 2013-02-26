import java.util.*;


public class EventChain {
	public static CountEvents countEvents = null;
	
	private Protagonist pro = null;
	private List<Event> events;
	
	public EventChain() {
		events = new LinkedList<Event>();
		// pro is kept as null
	}
	
	public void setProtagonist(Protagonist p) {
		pro = p;
	}
	public Protagonist getProtagonist() {
		return pro;
	}
	public List<Protagonist> likelyProtagonists() {
		return null; // TODO
	}
	
	public void addEvent(Event e) {
		events.add(e);
	}
	
	public double getLikelihood() {
		return 0.0; // compute likelihood in some fashion
	}
	public double getLikelihoodOfAdding(Event e) {
		return 0.0; // compute likelihood of adding on this bonus event
		// may also wish to return who the likely protagonists would be of that event
	}
}
