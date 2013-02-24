import java.util.*;


public class EventChain {

	private Protagonist pro;
	private Map<Event, Boolean> subjectMap;
	private List<Event> events;
	
	public EventChain() {
		pro = null;
		events = new LinkedList<Event>();
		subjectMap = new HashMap<Event, Boolean>();
	}
	
	public void setProtagonist(Protagonist p) {
		pro = p;
	}
	public Protagonist getProtagonist() {
		return pro;
	}
	
	private void addEvent(Event e) {
		events.add(e);
	}
	public void addEventAsSbuject(Event e) {
		addEvent(e);
		subjectMap.put(e, true);
	}
	public void addEventAsObject(Event e) {
		addEvent(e);
		subjectMap.put(e, false);
	}
	
	public double getLikelihood() {
		return 0.0; // compute likelihood in some fashion
	}
	public double getLikelihoodOfAdding(Event e) {
		return 0.0; // compute likelihood of adding on this bonus event
		// may also wish to return who the likely protagonists would be of that event
	}
}
