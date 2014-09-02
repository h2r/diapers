package edu.brown.h2r.diapers.domain.easydiapervocab;

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
import burlap.oomdp.singleagent.GroundedAction;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.ObservationModel;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
//import edu.brown.h2r.diapers.pomdp.POMDPState;

public class RashDomainVocabObsModel extends ObservationModel{
	
	Map<String, List<Observation>> obsMap = new HashMap<String, List<Observation>>();
	Map<String, Map<String,Integer>> wordCountMap = new HashMap<String, Map<String,Integer>>();
	Map<String, Integer> totalWordCountMap = new HashMap<String, Integer>();
	List<Observation> allObs = new ArrayList<Observation>();
	Random randomNumber = RandomFactory.getMapped(0);

	
	public RashDomainVocabObsModel(POMDPDomain d){
		super(d);
		String fromFile = "data/dialogues.json";
		
		
		
		obsMap.put(Names.MS_TYPE_START, new ArrayList<Observation>());
		obsMap.put(Names.MS_TYPE_NO_RASH, new ArrayList<Observation>());
		obsMap.put(Names.MS_TYPE_RASH, new ArrayList<Observation>());
		obsMap.put(Names.MS_TYPE_GOAL, new ArrayList<Observation>());
		
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
	                String mentalState = parser.getText();
//	                parsed.put(fieldname, value);
	                parser.nextToken();
	                parser.nextToken();
	                String stateSentence = parser.getText();
	                obsMap.get(mentalState).add(new Observation(this.domain,mentalState+"_"+obsMap.get(mentalState).size(),stateSentence));
	                allObs.add(new Observation(this.domain,mentalState+"_"+obsMap.get(mentalState).size(),stateSentence));
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

	@Override
	public Set<Observation> getAllObservations() {
		return new HashSet<Observation>(allObs);//allObs;
	}

	@Override
	public double omega(Observation o, POMDPState s, GroundedAction a) {
		String sentence = o.getStentence();
		String mentalState = s.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
		int totalWordCountInState = this.totalWordCountMap.get(mentalState);
		Map<String,Integer> wordCounts = this.wordCountMap.get(mentalState);
		double prob = 1.0;
		double alpha = 1;
		int wordsInState = wordCounts.size();
		for(String word : sentence.split(" ")){
			if(wordCounts.containsKey(word)){
				prob = prob * ((double)wordCounts.get(word)+alpha)/(totalWordCountInState + wordsInState*alpha);
			}
			else{
				prob = prob * (alpha)/(totalWordCountInState + wordsInState*alpha);
			}
		}
		return prob;
	}

	@Override
	public Set<Observation> getPossibleObservationsFor(POMDPState s,
			GroundedAction a) {
		String mentalState  = s.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
		return new HashSet<Observation>(this.obsMap.get(mentalState));
	}

	@Override
	public Observation makeObservationFor(POMDPState s, GroundedAction a) {
		String mentalState  = s.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
		List<Observation> obsList = this.obsMap.get(mentalState);
		return obsList.get(this.randomNumber.nextInt(obsList.size()));
	}

	@Override
	public boolean isSuccess(Observation o) {
			if(o==null){return false;}
			return o.getName().replaceAll("_(.*)$", "").equals(Names.MS_TYPE_GOAL);
	}
	
//	public static void main(String[] args){
////		POMDPDomain d = new POMDPDomain();
//		ObservationModel test = new RashDomainObsModel(new POMDPDomain());
//	}

}
