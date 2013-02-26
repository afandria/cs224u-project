
public class CountEvents {

	// Issue; we probably can't store everything.
	// And this way may just take the firstN, rather than the bestN
	public static final int MAX_VERBS = 20;
	public static final int MAX_EVENTS = MAX_VERBS * 2;
	public static final int MAX_PROTAGONISTS = 30;
	public static final int DEFAULT_VERB = 0;
	public static final int DEFAULT_PRO = 0;
	public static final int NONE_PRO = 1;
	
	public int curVerbs = 0;
	public int curPros = 0;
	
	public String[] verbs = new String[MAX_VERBS];
	public String[] pros = new String[MAX_PROTAGONISTS];
	
	public int[][] eventPairCounts = new int[MAX_EVENTS][MAX_EVENTS];
	public int[][] eventProCounts = new int[MAX_EVENTS][MAX_PROTAGONISTS];
	public int[] eventCounts = new int[MAX_EVENTS]; // redundant, but useful
	
	public CountEvents() {
		initialize();
	}
	
	public CountEvents(String filename) {
		// I would load the data in here directly instead
	}
	
	private void initialize() {
		curVerbs = 1;
		curPros = 2;
		verbs[0] = "<VERB>";
		pros[0] = "<PRO>";
		pros[1] = "<NONE>";
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Let's ensure that we have default events and default protagonists, in case we overflow
		
		CountEvents countEvents = new CountEvents();
		Event.countEvents = countEvents;
		EventChain.countEvents = countEvents;
		NarrativeSchema.countEvents = countEvents;
		Protagonist.countEvents = countEvents;
		
		// for (each document)
		// for (each sentence that we see)
		// take the subject verb object
		// It's possible that there isn't a subject/object, so use NONE
		// Anyway, if there's still space, add on to the current list of verbs and pros
		// And add on to the counts properly
		
		// Can either save the counts to a file or something...
	}

}
