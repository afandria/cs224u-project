import java.util.*;


public class NarrativeSchema {
	public static CountEvents countEvents = null;

	// again a list of events, but this time ensuring that we have double events (with matching verb and filled subject/object)
	
	public static final double NARRATIVE_SCHEMA_THRESHOLD = .1;
	
	private List<Event> eventsAll; // Length is 2n, for n events, with complementary events in i and i + 1
	private List<EventChain> chains; // each chain is for 1 protagonist
	private Map<Event, EventChain> chainAssignment; // Try to ensure each chain is only assigned to 1 end of complement events at most
	
	public NarrativeSchema() {
		eventsAll = new LinkedList<Event>();
		chains = new LinkedList<EventChain>();
		chainAssignment = new HashMap<Event, EventChain>();
	}
	
	public Protagonist getLikelyProtagonist(EventChain c) {
		return null;
	}
	
	public EventChain getLikelyChain(Protagonist p) {
		return null; // want to extract the proper chain for the given protagonist
	}
	
	// public List<EventChain> getAllChains()
	// public List<EventChain> getChainsOfLength(int minimum) 
	
	private double addEvent(Event e) {
		Event e2 = e.getComplementEvent();
		
		if (e2.argType) { // then swap, so we add subject-events first
			Event eTemp = e2;
			e2 = e;
			e = eTemp;
		}
		eventsAll.add(e);
		eventsAll.add(e2);
		
		// You have to find out the best one or make a new one, I guess
		int bestI1 = -1;
		double bestScore1 = NARRATIVE_SCHEMA_THRESHOLD;
		int bestI2 = -1;
		double bestScore2 = NARRATIVE_SCHEMA_THRESHOLD;
		for (int i = 0; i < chains.size(); i++) {
			EventChain chain = chains.get(i);
			double score1 = chain.getLikelihoodOfAdding(e);
			double score2 = chain.getLikelihoodOfAdding(e2);
			
			// Careful, we don't want to add to have this chain on both sides of the event...
			// So make 1 of the scores 0, if this happens.
			if (score1 > bestScore1 && score2 > bestScore2) {
				if (score2 > score1)
					score1 = 0;
				else
					score2 = 0;
			}
			
			// Update if you can
			if (score1 > bestScore1) {
				bestI1 = i;
				bestScore1 = score1;
			}
			if (score2 > bestScore2) {
				bestI2 = i;
				bestScore2 = score2;
			}
		}
		
		// Now assign the chains properly, possibly making a new one if you have to
		if (bestI1 == -1) {
			EventChain newChain = new EventChain();
			newChain.addEvent(e);
			chains.add(newChain);
		} else {
			chains.get(bestI1).addEvent(e);
		}
		if (bestI2 == -1) {
			EventChain newChain = new EventChain();
			newChain.addEvent(e2);
			chains.add(newChain);
		} else {
			chains.get(bestI2).addEvent(e2);
		}
		
		// return the score of this addition!
		return bestScore1 + bestScore2;
	}
	/*public void addEventWithProtagonists(Event e, Protagonist s, Protagonist o) {
		addEvent(e);
		subjects.put(e, s);
		objects.put(e, o);
	}*/
	
	public double getLikelihood() {
		return 0.0; // compute likelihood in some fashion
	}
	/*public double getLikelihoodOfAdding(Event e) {
		return 0.0; // compute likelihood of adding on this bonus event
		// may also wish to return who the likely protagonists would be of that event
	}*/
}
