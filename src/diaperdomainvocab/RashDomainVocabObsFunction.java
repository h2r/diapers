package diaperdomainvocab;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import burlap.debugtools.RandomFactory;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.pomdp.ObservationFunction;
import burlap.oomdp.singleagent.pomdp.PODomain;
import burlap.oomdp.singleagent.pomdp.ObservationFunction.ObservationProbability;



public class RashDomainVocabObsFunction extends ObservationFunction{
	
//	public RashDomainVocabObsFunction(PODomain domain) {
//		super(domain);
//		// TODO Auto-generated constructor stub
//	}

	Map<String, List<State>> obsMap = new HashMap<String, List<State>>();
	Map<String, Map<String,Integer>> wordCountMap = new HashMap<String, Map<String,Integer>>();
	Map<String, Integer> totalWordCountMap = new HashMap<String, Integer>();
	List<State> allObs = new ArrayList<State>();
	Random randomNumber = RandomFactory.getMapped(0);
	
	private Map<String, String> stateToObservationName = new HashMap<String,String>();
	private Map<String, String> observationNameToMentalStates = new HashMap<String,String>();


	
	public RashDomainVocabObsFunction(PODomain d){
		super(d);
		String fromFile = "diapers/data/dialogues.json";
		
		
		// creating a forward key of state to observations
					this.stateToObservationName.put(Names.MS_TYPE_START, Names.OBS_START);
					this.stateToObservationName.put(Names.MS_TYPE_RASH, Names.OBS_RASH);
					this.stateToObservationName.put(Names.MS_TYPE_NO_RASH, Names.OBS_NO_RASH);
					this.stateToObservationName.put(Names.MS_TYPE_GOAL, Names.OBS_GOAL);

					//creating a reverse key of observations to keys
					this.observationNameToMentalStates.put(Names.OBS_START,Names.MS_TYPE_START);
					this.observationNameToMentalStates.put(Names.OBS_RASH, Names.MS_TYPE_RASH);
					this.observationNameToMentalStates.put(Names.OBS_NO_RASH, Names.MS_TYPE_NO_RASH);
					this.observationNameToMentalStates.put(Names.OBS_GOAL, Names.MS_TYPE_GOAL);
			
		
		
		obsMap.put(Names.MS_TYPE_START, new ArrayList<State>());
		obsMap.put(Names.MS_TYPE_NO_RASH, new ArrayList<State>());
		obsMap.put(Names.MS_TYPE_RASH, new ArrayList<State>());
		obsMap.put(Names.MS_TYPE_GOAL, new ArrayList<State>());
		
		wordCountMap.put(Names.MS_TYPE_START, new HashMap<String,Integer>());
		wordCountMap.put(Names.MS_TYPE_NO_RASH, new HashMap<String,Integer>());
		wordCountMap.put(Names.MS_TYPE_RASH, new HashMap<String,Integer>());
		wordCountMap.put(Names.MS_TYPE_GOAL, new HashMap<String,Integer>());
		
		totalWordCountMap.put(Names.MS_TYPE_START, 0);
		totalWordCountMap.put(Names.MS_TYPE_NO_RASH, 0);
		totalWordCountMap.put(Names.MS_TYPE_RASH, 0);
		totalWordCountMap.put(Names.MS_TYPE_GOAL, 0);
		
		try{
		 BufferedReader reader = new BufferedReader(new FileReader(fromFile));
		 String line = null;
	        while ((line=reader.readLine()) != null) {
	        	JsonFactory jf = new JsonFactory();
	        	JsonParser parser = jf.createJsonParser(line);
	            parser.nextToken(); //shift past the START_OBJECT that begins the JSON
	            while (parser.nextToken() != JsonToken.END_OBJECT) {
//	            	parser.nextToken();
//	            	parser.nextToken();
//	            	String fieldname = parser.getCurrentName();
//	                parser.nextToken(); // move to value, or START_OBJECT/START_ARRAY
	                parser.nextToken();
	                final String mentalState = parser.getText();
//	                parsed.put(fieldname, value);
	                parser.nextToken();
	                parser.nextToken();
	                final String stateSentence = parser.getText();
//	                State tempObs = new State();
	                //TODO: fill up the state
//	                State tempObs = new State(this.domain,mentalState+"_"+obsMap.get(mentalState).size(),stateSentence){
//	                	public double getProbability(State s, GroundedAction ga){
//	                		final Observation tempNewObs = new Observation(this.domain,mentalState+"_"+obsMap.get(mentalState).size(),stateSentence);
//	                		return RashDomainVocabObsModel.this.omega(tempNewObs, (POMDPState)s, ga);
//	                	}
//	                };
	                
//	                System.out.println("MS: " + mentalState);
//	                System.out.println("Sentence: " + stateSentence);
	                
	                String obsName = this.stateToObservationName.get(mentalState);
	                
	                State tempObs = this.setObs(obsName, stateSentence);
	                
	                
	                
	                obsMap.get(mentalState).add(tempObs);
	                allObs.add(tempObs);
	                for(String word : stateSentence.split(" ")){
	                	Integer count = totalWordCountMap.get(mentalState);
	                	if(count!=null){
	                		totalWordCountMap.put(mentalState, count+1);
	                	}
	                	else{
	                		System.out.println("RashDomainObsModel weird misssing state: "+ mentalState);
	                		totalWordCountMap.put(mentalState, 1);
	                	}
	                	Map<String,Integer> stateWordCountMap = wordCountMap.get(mentalState);
	                	if(!stateWordCountMap.equals(null)){
	                		if(stateWordCountMap.containsKey(word)){
	                			int wordCountState = stateWordCountMap.get(word);
	                			wordCountMap.get(mentalState).put(word, wordCountState+1);
	                		}
	                		else{
	                			wordCountMap.get(mentalState).put(word, 1);
	                		}
	                	}
	                	else{
	                		System.out.println("RashDomainObsModel weird misssing state: "+ mentalState);
	                		wordCountMap.put(mentalState, new HashMap<String, Integer>());
	                		wordCountMap.get(mentalState).put(word, 1);
	                	}
	                }
//	                System.out.println("state: " + value1 +" sentence: "+ value2);
	                
	            }
	        }
//	        for(Map<String,Integer> map : wordCountMap.values()){
//	        	System.out.println("Size of map: "+map.size());
//	        	System.out.println("Words: ");
//	        	for(String wordsPresent : map.keySet()){
//	        		System.out.print(wordsPresent+", ");
//	        	}
//	        	System.out.println("");
//	        }
//	        for(String str : wordCountMap.keySet()){
//	        	System.out.println("State: "+ str);
//	        }
//	        for(Observation oTemp : allObs){
//	        	System.out.println("name of obs: "+oTemp.getName() +" observation sentence: "+oTemp.getStentence());
//	        	System.out.println("name of obs: "+oTemp.getName().replaceAll("_(.*)$", ""));
//	        }
		} catch (JsonParseException e) {
	        // JSON could not be parsed
	        e.printStackTrace();
	    }  catch (IOException e){
			System.err.println(e);
            System.exit(1);
		}
		 
		
	}
	
	
	protected State setObs(String obsName, String obsSentence){
		State obsStart = new State();
		ObjectInstance obL = new ObjectInstance(this.domain.getObjectClass(Names.CLASS_OBSERVATION), Names.OBSERVATION_OBJ);
		obL.setValue(Names.ATTR_OBS, obsName);
		obL.setValue(Names.ATTR_OBS_SENTENCE, obsSentence);
		obsStart.addObject(obL);
		return obsStart;
	}

//	@Override
//	public Set<State> getAllObservations() {
//		return new HashSet<Observation>(allObs);//allObs;
//	}

//	@Override
//	public double omega(Observation o, POMDPState s, GroundedAction a) {
//		String sentence = o.getStentence();
//		String mentalState = s.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
//		int totalWordCountInState = this.totalWordCountMap.get(mentalState);
//		Map<String,Integer> wordCounts = this.wordCountMap.get(mentalState);
//		double prob = 1.0;
//		double alpha = .00000001;
//		int wordsInState = wordCounts.size();
//		for(String word : sentence.split(" ")){
//			if(wordCounts.containsKey(word)){
//				prob = prob * ((double)wordCounts.get(word)+alpha)/(totalWordCountInState + wordsInState*alpha);
//			}
//			else{
//				prob = prob * (alpha)/(totalWordCountInState + wordsInState*alpha);
//			}
//		}
////		System.out.println("RashDomainVocabObs: "+ prob);
//		return prob;
//	}

//	@Override
//	public Set<Observation> getPossibleObservationsFor(POMDPState s,
//			GroundedAction a) {
//		String mentalState  = s.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
//		return new HashSet<Observation>(this.obsMap.get(mentalState));
//	}

//	@Override
//	public Observation makeObservationFor(POMDPState s, GroundedAction a) {
//		String mentalState  = s.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
//		List<Observation> obsList = this.obsMap.get(mentalState);
//		return obsList.get(this.randomNumber.nextInt(obsList.size()));
//	}

//	@Override
//	public boolean isSuccess(Observation o) {
//			if(o==null){return false;}
//			return o.getName().replaceAll("_(.*)$", "").equals(Names.MS_TYPE_GOAL);
//	}

	@Override
	public List<State> getAllPossibleObservations() {
		return allObs;
	}

	@Override
	public double getObservationProbability(State observation, State s,
			GroundedAction action) {
		// TODO Auto-generated method stub

		
		String oName = observation.getFirstObjectOfClass(Names.CLASS_OBSERVATION).getStringValForAttribute(Names.ATTR_OBS);
		String oSentence =  observation.getFirstObjectOfClass(Names.CLASS_OBSERVATION).getStringValForAttribute(Names.ATTR_OBS_SENTENCE);
//		String sentence = o.getStentence();
		String mentalState = s.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
		int totalWordCountInState = this.totalWordCountMap.get(mentalState);
		Map<String,Integer> wordCounts = this.wordCountMap.get(mentalState);
		double prob = 1.0;
		double alpha = .00000001;
		int wordsInState = wordCounts.size();
		for(String word : oSentence.split(" ")){
			if(wordCounts.containsKey(word)){
				prob = prob * ((double)wordCounts.get(word)+alpha)/(totalWordCountInState + wordsInState*alpha);
			}
			else{
				prob = prob * (alpha)/(totalWordCountInState + wordsInState*alpha);
			}
		}
//		System.out.println("RashDomainVocabObs: "+ prob);
		return prob;
	}

	@Override
	public boolean isTerminalObservation(State observation) {
		String oVal = observation.getFirstObjectOfClass(Names.CLASS_OBSERVATION).getStringValForAttribute(Names.ATTR_OBS);
		String obsMentalState = this.observationNameToMentalStates.get(oVal);
		return obsMentalState.equals(Names.MS_TYPE_GOAL);
	}
	
	@Override
	public State sampleObservation(State state, GroundedAction action){
		
		String mentalState  = state.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
		List<State> obsList = this.obsMap.get(mentalState);
		return obsList.get(this.randomNumber.nextInt(obsList.size()));
//		List<ObservationProbability> obProbs = this.getObservationProbabilities(state, action);
//		Random rand = RandomFactory.getMapped(0);
//		double r = rand.nextDouble();
//		double sumProb = 0.;
//		for(ObservationProbability op : obProbs){
//			sumProb += op.p;
//			if(r < sumProb){
//				return op.observation;
//			}
//		}
//		
//		throw new RuntimeException("Could not sample observaiton because observation probabilities did not sum to 1; they summed to " + sumProb);
	}
	
//	public static void main(String[] args){
////		POMDPDomain d = new POMDPDomain();
//		ObservationModel test = new RashDomainObsModel(new POMDPDomain());
//	}

}
