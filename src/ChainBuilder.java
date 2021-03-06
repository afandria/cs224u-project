import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import edu.jhu.agiga.AgigaCoref;
import edu.jhu.agiga.AgigaDocument;
import edu.jhu.agiga.AgigaMention;
import edu.jhu.agiga.AgigaPrefs;
import edu.jhu.agiga.AgigaSentence;
import edu.jhu.agiga.AgigaToken;
import edu.jhu.agiga.AgigaTypedDependency;
import edu.jhu.agiga.BasicAgigaSentence;
import edu.jhu.agiga.StanfordAgigaSentence;
import edu.jhu.agiga.StreamingDocumentReader;
import edu.jhu.agiga.StreamingSentenceReader;
import edu.jhu.agiga.AgigaConstants.DependencyForm;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TypedDependency;


public class ChainBuilder {

	
	public static final int MAX_CHAINS_TO_BUILD = 2800;
	public static final int MAX_CHAINS_PER_FILE = 400;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// We should already have counts...
		
		// If you choose to give arguments, make sure you give these 5.
		// 0: protagonistNamesFileOut
		// 1: eventToVerbsFileOut
		// 2: eventCountsFileOut
		// 3: eventPairCountsFileOut
		// 4: eventPairProCountsFileOut
		long startTime = System.currentTimeMillis();
		String[] countFiles = new String[5];
		if (args.length > 5) {
			for (int i = 0; i < 5; i++)
				countFiles[i] = args[i];
			
		} else {
			countFiles[0] = CountEvents.protagonistNamesFileOut;
			countFiles[1] = CountEvents.eventToVerbsFileOut;
			countFiles[2] = CountEvents.eventCountsFileOut;
			countFiles[3] = CountEvents.eventPairCountsFileOut;
			countFiles[4] = CountEvents.eventPairProCountsFileOut;
		}
		
		CountEvents countEvents = new CountEvents(
				countFiles[0],
				countFiles[1],
				countFiles[2],
				countFiles[3],
				countFiles[4]);
		Event.countEvents = countEvents;
		EventChain.countEvents = countEvents;
		NarrativeSchema.countEvents = countEvents;
		Protagonist.countEvents = countEvents;
		
		//countEvents.printCounts();
		long curTime = System.currentTimeMillis();
		System.out.println("Loading Data Time Elapsed: " + (startTime - curTime));
		System.out.println("# Popular Events: " + countEvents.popularEventsList.size());
		//getCountsByDeps(countEvents);
		List<EventChain> ecs = buildDocumentChains(countEvents);
		curTime = System.currentTimeMillis();
		System.out.println("Chain Building Time Elapsed: " + (startTime - curTime));
		
		
		List<Pair<Double, Pair<Protagonist, EventChain>>> pairs = new ArrayList<Pair<Double, Pair<Protagonist, EventChain>>>();
		for (int i = 0; i < ecs.size(); i++) {
			System.out.println("Chain " + i);
			System.out.println("Score " + ecs.get(i).getScore());
			Pair<Double, Protagonist> pair = ecs.get(i).getNormalizedScore();
			System.out.println("NScore " + pair.x);
			System.out.println(ecs.get(i));
			for (Event e: ecs.get(i).getEvents()) {
				System.out.println(e.getSentenceString());
			}
			
			pairs.add(new Pair<Double, Pair<Protagonist, EventChain>>(
					pair.x,
					new Pair<Protagonist, EventChain>(pair.y, ecs.get(i))));
		}
		
		curTime = System.currentTimeMillis();
		System.out.println("Building Chains Time Elapsed: " + (startTime - curTime));
		System.out.println("Sorting");
		Collections.sort(pairs, new PairComparator(false));
		for (int i = 0; i < pairs.size(); i++) {
			System.out.println("SortedChain " + i);
			System.out.println("NScore " + pairs.get(i).x);
			System.out.println("Protagonist " + pairs.get(i).y.x);
			System.out.println(pairs.get(i).y.y);
		}
		
		curTime = System.currentTimeMillis();
		System.out.println("Sorting Chains Time Elapsed: " + (startTime - curTime));
		
		int clozeSum = 0;
		int clozeTop1PercentCount = 0;
		int clozeTop10PercentCount = 0;
		int unranked = countEvents.popularEventsList.size(); // if unranked
		
		for (int i = 0; i < pairs.size(); i++) {
			Pair<Event, List<Pair<Double, Event>>> cloze = clozeTest(countEvents, pairs.get(i).y.y, pairs.get(i).y.x);
			
			int ranking = unranked;
			for (int rank = 0; rank < cloze.y.size(); rank++) {
				Pair<Double, Event> simEvent = cloze.y.get(rank);
				if (cloze.x.equals(simEvent.y)) {
					ranking = rank;
					break;
				}
			}
			for (int rank = 0; rank < cloze.y.size(); rank++) {
				if (rank == 0 || rank == cloze.y.size() / 100 || 
						rank == cloze.y.size() / 10 || 
						rank == ranking ||
						rank == cloze.y.size() / 2) {
				    Pair<Double, Event> simEvent = cloze.y.get(rank);
				    System.out.println(rank + " <Score " + simEvent.x + ", " + simEvent.y);
				}
			}
			clozeSum += ranking;
			if (ranking < unranked / 100)
				clozeTop1PercentCount++;
			if (ranking < unranked / 10)
				clozeTop10PercentCount++;
			System.out.println(i + ": got rank " + ranking + " " + cloze.x);
		}
		System.out.println("Cloze Sum: " + clozeSum);
		System.out.println("Cloze Top 1% Count: " + clozeTop1PercentCount);
		System.out.println("Cloze Top 10% Count: " + clozeTop10PercentCount);
		System.out.println("Cloze Results: " + (clozeSum + 0.0) / pairs.size());
		
		curTime = System.currentTimeMillis();
		System.out.println("Cloze Test Time Elapsed: " + (startTime - curTime));
	}
	public static Pair<Event, List<Pair<Double, Event>>> clozeTest(CountEvents countEvents, EventChain ec, Protagonist pro) {
		// the return value is the ranking of the removed event
		// The list is the ordered event ranking for the missing event in the event chain

		List<Pair<Double, Event>> scoreEventList = new ArrayList<Pair<Double, Event>>();
		
		Pair<Integer, Event> indexEvent = ec.removeRandomEvent();
		Event removedEvent = indexEvent.y;
		int removedIndex = indexEvent.x;
		for (Event e : countEvents.popularEventsList) {
			double sim = ec.getChainSimilarity(e, pro, removedIndex);
			scoreEventList.add(new Pair<Double, Event>(sim, e));
		}
		Collections.sort(scoreEventList, new PairComparator(false));
		
		return new Pair<Event, List<Pair<Double, Event>>>(
				removedEvent,
				scoreEventList
				);
	}
	
	
	public static List<EventChain> buildDocumentChains(CountEvents countEvents){
		List<EventChain> ecs = new ArrayList<EventChain>();
		// for (each document)
		// for (each sentence that we see)
		// take the subject verb object
		// It's possible that there isn't a subject/object, so use NONE
		// Anyway, if there's still space, add on to the current list of verbs and pros
		// And add on to the counts properly
		
		
		ArrayList<String> files = new ArrayList<String>();
		/*files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\Set1\\afp_eng_199405.xml.gz");
		files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\Set1\\afp_eng_199406.xml.gz");
		files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\Set1\\afp_eng_199407.xml.gz");
		files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\Set1\\afp_eng_199408.xml.gz");
		files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\Set1\\afp_eng_199409.xml.gz");
		files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\Set1\\afp_eng_199410.xml.gz");			//"C:\\Users\\aman313\\Documents\\Winter-2013\\cs224u\\agiga_1.0\\Data\\Set1\\afp_eng_199405.xml.gz";
	*/
		//files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\Set2\\afp_eng_200901.xml.gz");
		//files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\Set2\\nyt_eng_200901.xml.gz");
		
		files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\set4\\test\\afp_eng_199606.xml.gz");
		files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\set4\\test\\apw_eng_200007.xml.gz");
		files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\set4\\test\\cna_eng_200602.xml.gz");
		files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\set4\\test\\ltw_eng_200704.xml.gz");
		files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\set4\\test\\nyt_eng_200106.xml.gz");
		files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\set4\\test\\wpb_eng_201010.xml.gz");
		files.add("C:\\Users\\AlexFandrianto\\Desktop\\Articles\\Stanford\\CS224U\\Smaller\\set4\\test\\xin_eng_200410.xml.gz");

		int fileIndex = 0;
		
		for(String file:files){
	        AgigaPrefs prefs = new AgigaPrefs();
	        prefs.setAll(false);
	        prefs.setWord(true);
	        prefs.setCoref(true);
	        prefs.setLemma(true);
	        prefs.setPos(true);
	        prefs.setDeps(DependencyForm.BASIC_DEPS);

	        StreamingDocumentReader reader = new StreamingDocumentReader(file, prefs);
	        int idx=0;
	        
	        fileIndex++;
	        
	        for(AgigaDocument doc:reader){
	        	if(ecs.size() == MAX_CHAINS_TO_BUILD)
	        		return ecs;
	        	if (ecs.size() == MAX_CHAINS_PER_FILE * fileIndex) // do 500 per file
	        		break;
	        	//if(Math.random() < .3) // skip randomly
	        	//	continue;
	        	if (idx % 100 == 0)
	        		System.out.println(idx);
	        	// get all mentions for the document
	        	doc.assignMucStyleIdsAndRefsToMentions(); // is this needed ??
	        	List<AgigaCoref> corefs = doc.getCorefs();
	        	List<AgigaSentence> sents = doc.getSents();
	        
	        	Map<String,AgigaTypedDependency> depsMap = new HashMap<String, AgigaTypedDependency>();
	        	for(AgigaSentence sent :sents){
	        		List<AgigaToken> tokens = sent.getTokens();
	        		List<AgigaTypedDependency> deps = sent.getAgigaDeps(DependencyForm.BASIC_DEPS);
	        		for(AgigaTypedDependency dep :deps){
	        			if(dep.getType().equals("nsubj") || dep.getType().equals("dobj")){
	        				//AgigaToken tokGov = tokens.get(dep.getGovIdx());//Verb token
	        				AgigaToken tokDep = tokens.get(dep.getDepIdx());
	        				depsMap.put(sent.getSentIdx()+ " " +tokDep.getTokIdx() , dep);
	        			}
	        		} 
	        	
	        	}// dependency map created
	        	//System.out.println("Created Dependency Map");
	        	
	        	int maxCorefSize = 0;
	        	AgigaCoref longestCoref = null;
	        	for(AgigaCoref coref:corefs){
	        		int size = coref.getMentions().size();
	        		if (size > maxCorefSize) {
	        			maxCorefSize = size;
	        			longestCoref = coref;
	        		}
	        	}
	        	if (longestCoref == null)
	        		continue;
	        	
	        	// Use longest coreferent chain as the protagonist.
	        	// This will build a document event chain.
	        	EventChain chain = new EventChain();
	        	
	        	Protagonist p = null;
	        	for (AgigaMention mention : longestCoref.getMentions()) {
	        		if (chain.getEvents().size() == 8) // we're done if we hit 8
	        			break; 
	        		
	        		AgigaSentence sent = sents.get(mention.getSentenceIdx());// Get the sentence
        			List<AgigaToken> tokens = sent.getTokens();
	        		
	        		AgigaTypedDependency depend = depsMap.get(mention.getSentenceIdx()+ " "+mention.getHeadTokenIdx());
	    			if(depend!=null){
	    				// skip if the wrong type
        				if (!depend.getType().equals("nsubj") && !depend.getType().equals("dobj"))
        					continue;
        				
        				AgigaToken tokGov = tokens.get(depend.getGovIdx());//Verb token
        				//System.out.println(tokGov.getPosTag());
        				if (tokGov.getPosTag().indexOf("VB") != 0) // isn't a verb...
        					continue;
        					
        				AgigaToken tokDep = tokens.get(depend.getDepIdx());
        				String type = depend.getType();
        				Boolean typ = type.equals("nsubj")?true:false;
        				Integer temp = countEvents.verbArgTypeMap.get(new Pair<String,String>(tokGov.getLemma(),type));	  
        				
        				int eventIndex = -1;
        				if (temp != null)
        					eventIndex = temp.intValue();
        				
        				if (eventIndex < 0)
        					continue;
        				
        				// DOING IT
        				// Set the protagonist because we found an event
            			if (p == null) {
                			AgigaToken token = tokens.get(mention.getHeadTokenIdx());
                			Integer pid = countEvents.prosMap.get(token.getLemma());
                			if (pid != null) {
                			  p = new Protagonist(pid);
            	        	  chain.setProtagonist(p);        	  
                			}
            			}
        				
            			// Add the event, as well as the sentence it came from
        				Event e = new Event(eventIndex, typ);
        				AgigaSentence sentence = doc.getSents().get(mention.getSentenceIdx());
        				e.sentence = sentence;
        				chain.addEvent(e);
	    			}
	        	}
    			if (chain.getEvents().size() >= 5 && chain.getEvents().size() <= 8)
    				ecs.add(chain);
    			
	        	// built 1 chain for 1 document
	        	idx++;	
	        }//document dealt with
	        			
		} // all files read
		return ecs;
	}
	
}
