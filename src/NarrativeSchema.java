import java.util.*;


public class NarrativeSchema {

	private List<Event> events;
	private Map<Event, Protagonist> subjects;
	private Map<Event, Protagonist> objects;
	
	public NarrativeSchema() {
		events = new LinkedList<Event>();
		subjects = new HashMap<Event, Protagonist>();
		objects = new HashMap<Event, Protagonist>();
	}
	
	public EventChain getChain(Protagonist p) {
		return null; // want to extract the proper chain for the given protagonist
	}
	
	private void addEvent(Event e) {
		events.add(e);
		
		// You have to find out the best one or make a new one, I guess
		// also assign subjects.put(e, subject)
		// also assign objects.put(e, object)
	}
	public void addEventWithProtagonists(Event e, Protagonist s, Protagonist o) {
		addEvent(e);
		subjects.put(e, s);
		objects.put(e, o);
	}
	
	public double getLikelihood() {
		return 0.0; // compute likelihood in some fashion
	}
	public double getLikelihoodOfAdding(Event e) {
		return 0.0; // compute likelihood of adding on this bonus event
		// may also wish to return who the likely protagonists would be of that event
	}
}
