
public class Event {
	public static CountEvents countEvents = null;
	
	// Maybe this should be verb, argument-type (subject/object)

	public int verb;
	public boolean argType;
	public int argTokId;
	public int sentId;
	public Event(int v, boolean a) {
		verb = v;
		argType = a;
	}
	
	// public boolean isComplement(Event other)
	
		
	public boolean equals(Object e){
		if (!(e instanceof Event))
			  return false;
		Event o = (Event)e;
		if(o.verb==this.verb && o.argType == this.argType) return true;
		return false;
		
	}
	 
	public int hashCode() {
		return  argType?verb+1:-(verb+1);
	}
	
	public Event getComplementEvent() {
		return new Event(verb, !argType);
	}
	
	public int getLargeIndex() {
		return 2 * verb + (argType ? 0 : 1);
	}
	
	public double getPMI(Event e) {
		Integer cooccur = countEvents.eventPairCounts.get(new Pair<Event, Event>(this, e));
		double cooccurP = (cooccur + 0.0) / countEvents.eventPairOverallCount;
		
		int thisCount = countEvents.eventsCountMap.get(this);
		int eCount = countEvents.eventsCountMap.get(e);
		
		double thisP = (thisCount + 0.0) / countEvents.eventOverallCount;
		double eP = (eCount + 0.0) / countEvents.eventOverallCount;
		
		return Math.log(cooccurP / (thisP * eP));
	}
	
	public double getSimilarity(Event e, Protagonist p) {
		double freq = countEvents.eventPairProCounts.get(
				new Triple<Event, Event, Protagonist>(this, e, p));
		if (freq == 0) {
			return 0; // that's the minimum you'd get anyway
		}
		freq = Math.log(freq);
		freq *= CountEvents.LAMBDA;
		return getPMI(e) + freq;
	}
	
	public String toString() {
		if (argType)
			return "" + verb + " " + argType + ":" + countEvents.eventToVerbMap.get(this) + "S";
		else
			return "" + verb + " " + argType + ":" + countEvents.eventToVerbMap.get(this) + "O";
	}
}
