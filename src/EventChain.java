import java.util.*;


public class EventChain {
	public static CountEvents countEvents = null;
	
	private Protagonist pro = null; // while a protag may be 'known', it doesn't affect score computation
	private List<Event> events; // not a set
	
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
	public List<Protagonist> findLikelyProtagonists(double threshold) {
		return null; // TODO
	}
	public List<Protagonist> findTopKProtagonists(int K) {
		return null; // TODO
	}
	
	public boolean hasEvent(Event e) {
		for (Event e2 : events)
			if (e.equals(e2))
				return true;
		return false;
	}
	
	public void addEvent(Event e) {
		if (!hasEvent(e))
			events.add(e);
	}
	// for the cloze test
	public Pair<Integer, Event> removeRandomEvent() {
		int index = (int)(events.size() * Math.random());
		Event e = events.get(index);
		events.remove(index);
		return new Pair<Integer, Event>(index, e);
	}
	
	/*public double getLikelihood() {
		return 0.0; // compute likelihood in some fashion
	}
	public double getLikelihoodOfAdding(Event e) {
		return 0.0; // compute likelihood of adding on this bonus event
		// may also wish to return who the likely protagonists would be of that event
		
		// May wish to return 0 if this event is already inside (or has PMI 0 to any event inside)
	}*/
	
	public Pair<Double, Protagonist> getNormalizedScore() {
		Pair<Double, Protagonist> p = getScore();
		return new Pair<Double, Protagonist>(
				new Double(p.x * 2 / (events.size() * (events.size() - 1))),
				p.y);
	}
	
	// find highest score, regardless of protagonist, by finding the best protagonist
	// May wish to return a pair instead
	public Pair<Double, Protagonist> getScore() {
		if (pro != null) {
			return new Pair<Double, Protagonist>(getScore(pro), pro);
		}
		
		double bestS = -1;
		Protagonist bestP = null;
		
		for (int i = 0; i < countEvents.protList.size(); i++) {
			Protagonist p = countEvents.protList.get(i);//new Protagonist(i);
			double s = getScore(p);
			
			if (bestP == null || s > bestS) {
				bestS = s;
				bestP = p;
			}
		}
		
		return new Pair<Double, Protagonist>(bestS, bestP);
	}
	
	
	public double getScore(Protagonist p) {
		double s = 0;
		for (int i = 0; i < events.size() - 1; i++) {
			Event e = events.get(i);
			for (int j = i + 1; j < events.size(); j++) {
				s += e.getSimilarity(events.get(j), p);
			}
		}
		return s;
	}
	
	public double getChainSimilarity(Event e, Protagonist p, Integer removedIndex) {
		// Let's find who's the best, and the resulting score
		double s = getScore(p);
		for (int j = 0; j < events.size(); j++) {
			if (removedIndex < j)
				s += events.get(j).getSimilarity(e, p);
			else // e would have occurred after event j
				s += e.getSimilarity(events.get(j), p);
		}
		return s;
	}
	// a little less work now that we know the best protagonist too
	public double getChainSimilarity(Event e, Protagonist p) {
		return getChainSimilarity(e, p, events.size());
	}
	public double getChainSimilarity(Event e) {
		// Let's find who's the best, and the resulting score
		double bestS = -1;
		Protagonist bestP = null;
		
		for (int i = 0; i < countEvents.protList.size(); i++) {
			Protagonist p = countEvents.protList.get(i);//new Protagonist(i);
			
			double s = getScore(p);
			for (int j = 0; j < events.size(); j++) {
				s += events.get(j).getSimilarity(e, p);
			}
			
			if (bestP == null || s > bestS) {
				bestS = s;
				bestP = p;
			}
		}
		return bestS;
	}

	public List<Event> getEvents() {
		return events;
	}
	
	public String toString() {
		String c = "";
		for (Event e : events) {
			c += e.toString() + "\n";
		}
		return c;
	}
}
