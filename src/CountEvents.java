import java.util.*;

public class CountEvents {
	public static final double BETA = 0.1;
	public static final double LAMBDA = 0.1;
	
	
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
	
	public String[] verbs = new String[MAX_VERBS]; // printable verb, CountEvents.verbs[Event.verb]
	public String[] pros = new String[MAX_PROTAGONISTS];
	
	// MOSTLY FOR JUST HELPING US SET MAX_VERBS AND MAX_PROTAGONISTS, UNLIMITED!
	public Set<String> verbsList = new HashSet<String>();
	public Set<String> prosList = new HashSet<String>();
	
	// go from verb/protagonist to int. IS LIMITED BY MAX_VERBS AND MAX_PROTAGONISTS
	public Map<String, Integer> verbsMap = new HashMap<String, Integer>();
	public Map<String, Integer> prosMap = new HashMap<String, Integer>();
	
	public Map<Pair<Event, Event>, Integer> eventPairCounts = new HashMap<Pair<Event, Event>, Integer>();
	public int eventPairOverallCount = 0;
	
	public Map<Triple<Event, Event, Protagonist>, Integer> eventPairProCounts =
		new HashMap<Triple<Event, Event, Protagonist>, Integer>();
	
	// mapping from Event to its index in the count array below
	public Map<Event, Integer> eventsMap = new HashMap<Event, Integer>();
	public int[] eventCounts = new int[MAX_EVENTS]; // redundant, but useful
	public int eventOverallCount = 0;
	
	public CountEvents() {
		initialize();
	}
	
	public CountEvents(String filename) {
		// I would load the data in here directly instead
	}
	
	private void initialize() {
		curVerbs = 1;
		curPros = 2;
		verbs[DEFAULT_VERB] = "<VERB>";
		pros[DEFAULT_PRO] = "<PRO>";
		pros[NONE_PRO] = "<NONE>";
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
		  // keep track of subjects and objects with coreference ids so that we can check for matches
		  // we're also just saying the first one is the actual name of the protagonist
		// for (each sentence/clause/subject-event-object that we see)
		// take the subject verb object
		// It's possible that there isn't a subject/object, so use NONE
		// Anyway, if there's still space, add on to the current list of verbs and pros
		// And add on to the counts properly
		
		// Can either save the counts to a file or something...
	}

}
