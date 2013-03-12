import java.util.*;


public class NarrativeSchema {
	public static CountEvents countEvents = null;

	// again a list of events, but this time ensuring that we have double events (with matching verb and filled subject/object)
	
	private List<Event> eventsAll; // Length is 2n, for n events, with complementary events in i and i + 1
	private List<EventChain> chains; // each chain is for 1 protagonist
	
	public NarrativeSchema() {
		eventsAll = new LinkedList<Event>();
		chains = new LinkedList<EventChain>();
	}
	
	public Protagonist getLikelyProtagonist(EventChain c) {
		return null;
	}
	
	public EventChain getLikelyChain(Protagonist p) {
		return null; // want to extract the proper chain for the given protagonist
	}
	
	// public List<EventChain> getAllChains()
	// public List<EventChain> getChainsOfLength(int minimum) 
	
	public double getEventScore(String v) {
		int verbS = countEvents.verbArgTypeMap.get(new Pair<String, String>(v, "nsubj"));
		int verbO = countEvents.verbArgTypeMap.get(new Pair<String, String>(v, "dobj"));
		Event e = new Event(verbS, true);
		Event e2 = new Event(verbO, false);
		
		// You have to find out the best one or make a new one, I guess
		int bestI1 = -1;
		double bestScore1 = CountEvents.BETA;
		int bestI2 = -1;
		double bestScore2 = CountEvents.BETA;
		for (int i = 0; i < chains.size(); i++) {
			EventChain chain = chains.get(i);
			double score1 = chain.getChainSimilarity(e);
			double score2 = chain.getChainSimilarity(e2);
			
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
		
		// return the score of this addition!
		return bestScore1 + bestScore2;
	}
	
	public double addEvent(String v) {
		int verbS = countEvents.verbArgTypeMap.get(new Pair<String, String>(v, "nsubj"));
		int verbO = countEvents.verbArgTypeMap.get(new Pair<String, String>(v, "dobj"));
		Event e = new Event(verbS, true);
		Event e2 = new Event(verbO, false);
		
		// You have to find out the best one or make a new one, I guess
		int bestI1 = -1;
		double bestScore1 = CountEvents.BETA;
		int bestI2 = -1;
		double bestScore2 = CountEvents.BETA;
		for (int i = 0; i < chains.size(); i++) {
			EventChain chain = chains.get(i);
			double score1 = chain.getChainSimilarity(e);
			double score2 = chain.getChainSimilarity(e2);
			
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
		
		// Add the event
		eventsAll.add(e);
		eventsAll.add(e2);
		
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
	public double getScore() {
		double s = 0.0;
		for (int i = 0; i < chains.size(); i++) {
			Pair<Double, Protagonist> pair = chains.get(i).getScore();
			s += pair.x;
			
			// do something with the best protagonist if you want...
		}
		return s;
	}
	
	// a method for FORCE adding events, like when building document event chains
	public void addEventWithProtagonists(int verb, Protagonist s, Protagonist o) {
		Event e = new Event(verb, true);
		Event e2 = e.getComplementEvent();
		
		// Add to our global events for the schema
		eventsAll.add(e);
		eventsAll.add(e2);
		
		// Now add to chains, if possible. Otherwise, add to a new chain.
		// These chains must mark that their protags are known.
		boolean sAdded = false;
		boolean oAdded = false;
		
		for (int i = 0; i < chains.size(); i++) {
			EventChain ec = chains.get(i);
			if (!sAdded && ec.getProtagonist().equals(s)) {
				ec.addEvent(e);
				sAdded = true;
			}
			if (!oAdded && ec.getProtagonist().equals(o)) {
				ec.addEvent(e);
				oAdded = true;
			}
		}
		
		// new chain needed if !sAdded or !oAdded
		if (!sAdded) {
			EventChain ec = new EventChain();
			ec.setProtagonist(s);
			ec.addEvent(e);
			
			chains.add(ec);
		}
		if (!oAdded) {
			EventChain ec = new EventChain();
			ec.setProtagonist(o);
			ec.addEvent(e);
			
			chains.add(ec);
		}
	}
	
	/*public double getLikelihood() {
		return 0.0; // compute likelihood in some fashion
	}*/
	/*public double getLikelihoodOfAdding(Event e) {
		return 0.0; // compute likelihood of adding on this bonus event
		// may also wish to return who the likely protagonists would be of that event
	}*/
}
