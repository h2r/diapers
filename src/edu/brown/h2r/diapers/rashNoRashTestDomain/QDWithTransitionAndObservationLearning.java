package edu.brown.h2r.diapers.rashNoRashTestDomain;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

//import edu.brown.h2r.diapers.athena.POMDPDiaperDomain;
//import edu.brown.h2r.diapers.athena.POMDPDiaperDomain.AdvanceAction;
//import edu.brown.h2r.diapers.athena.POMDPDiaperDomain.SpeakAction;
import edu.brown.h2r.diapers.rashNoRashTestDomain.P;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.util.*;
import java.util.Random;

import burlap.oomdp.auxiliary.DomainGenerator;

import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TransitionProbability;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;

public class QDWithTransitionAndObservationLearning implements DomainGenerator {
	
	int numberOfRepeatedObservations=1;
	double obsNoise = 0.10;
	private String filePath = "src\\edu\\brown\\h2r\\diapers\\data\\dialogues.txt";
	private Map<String, String> stateKey = new HashMap<String,String>();
	
	private Map<String, Integer> count = new HashMap<String,Integer>();
	
	private Map<Triple<String,String,String>,Integer> transitionCount = new HashMap<Triple<String,String,String>,Integer>();
	private Map<Tuple<String,String>,Integer> transitionSum = new HashMap<Tuple<String,String>,Integer>();
	private Map<Triple<String,String,String>,Double> transitionProbTriple = new HashMap<Triple<String,String,String>,Double>();
	
	double[][] startBelief = null;

	private Map<String,String> nameMap = new HashMap<String,String>() {{
		put("state S",P.OBJ_STATE_START);
		put("state R",P.OBJ_STATE_RASH);
		put("state NR",P.OBJ_STATE_NO_RASH);
		put("state G",P.OBJ_STATE_GOAL);
		put("action advanceStartState",P.ACTION_ADVANCE_START_STATE);
		put("action advanceRashState",P.ACTION_ADVANCE_RASH);
		put("action advanceNoRashState",P.ACTION_ADVANCE_NO_RASH);
//		put("state G",P.AC);
	}};
	
	
	
	
	public QDWithTransitionAndObservationLearning() {}
	
	public void setDialogueFilePath(String path){
		this.filePath=path;
	}

	@Override
	public Domain generateDomain() {
		Domain domain = new POMDPDomain() {
			@Override
			public POMDPState getExampleState() {
				return QDWithTransitionAndObservationLearning.getNewState(this);
			}

			@Override
			public List<State> getAllStates() {
				return QDWithTransitionAndObservationLearning.getAllStates(this);
			}};
			
			Attribute attrMentalState = new Attribute(domain, P.ATTR_MENTAL_STATE, Attribute.AttributeType.RELATIONAL);

			ObjectClass stateHolderClass = new ObjectClass(domain, P.CLASS_STATE_HOLDER);
			stateHolderClass.addAttribute(attrMentalState);

			ObjectClass mentalStateClass = new ObjectClass(domain, P.CLASS_MENTAL_STATE);

			Action startAdvanceAction = new AdvanceAction(domain, P.ACTION_ADVANCE_START_STATE, P.OBJ_STATE_START);
			//System.out.println("POMDPDiaperDomain: before action A");
			Action rashAdvanceAction = new AdvanceAction(domain, P.ACTION_ADVANCE_RASH, P.OBJ_STATE_RASH);
			//System.out.println("POMDPDiaperDomain: after action A");
			Action noRashAdvanceAction = new AdvanceAction(domain, P.ACTION_ADVANCE_NO_RASH,P.OBJ_STATE_NO_RASH);
			
			Action speakAction = new SpeakAction(domain, P.ACTION_SPEAK);
			
			setObservations();
			
			for(int count = 0; count< numberOfRepeatedObservations; count++){
//				String start ="stateStartObservation"+"-"+count; 
//			Observation  stateStartObservation = new SimpleStateObservation(domain, P.OBS_STATE_START+"-"+count, P.OBJ_STATE_START);
//			Observation stateRashObservation = new SimpleStateObservation(domain, P.OBS_STATE_RASH+"-"+count, P.OBJ_STATE_RASH);
//			Observation stateNoRashObservation = new SimpleStateObservation(domain, P.OBS_STATE_NO_RASH+"-"+count, P.OBJ_STATE_NO_RASH);
//			Observation stateGoalObservation = new SimpleStateObservation(domain, P.OBS_STATE_GOAL+"-"+count, P.OBJ_STATE_GOAL);
//			Observation nullObservation = new SimpleStateObservation(domain, P.OBS_NULL+"-"+count, P.OBJ_STATE_GOAL);
			
			}
			return domain;
	}

	
	private void setObservations(){
		File inputTraining = new File(filePath);
		BufferedReader read = null;
		try {
			read = new BufferedReader(new FileReader(inputTraining));
		} catch(FileNotFoundException e){
			System.err.println("File not found " + filePath); 
			System.exit(1);
		}
		
		try {
			String line = "";
			String oldState=null;
			String actions=null;
			String newState=null;
			boolean flagStart = false;
			while((line = read.readLine()) != null) {
				if (line.startsWith("scene")){
					flagStart = true;
				}
				else if(line.startsWith("state")){
					if(flagStart)
					{
						oldState=nameMap.get(line.split(":")[0]);
						flagStart = false;
					}
					else{
						newState=nameMap.get(line.split(":")[0]);
						Triple<String,String,String> temp1 = new Triple<String,String,String>(oldState,actions,newState);
						Tuple<String,String> temp2 = new Tuple<String,String>(oldState,actions);
						if(transitionCount.containsKey(temp1)){
							transitionCount.put(temp1,transitionCount.get(temp1)+1);
						}
						else{
							transitionCount.put(temp1,1);
						}
						if(transitionSum.containsKey(temp2)){
						transitionSum.put(temp2, transitionSum.get(temp2)+1);
						}
						else{
							transitionSum.put(temp2, 1);
						}
							
						
					}
				}
				else if(line.startsWith("action")){
					actions=nameMap.get(line);
				}
			}
		}catch(IOException e){
			System.err.println("Can't read line");
			System.exit(1);
		}
	
	
	for(Triple<String,String,String> keyTriple: transitionCount.keySet()){
		// fill up probabilities
		Tuple<String,String> temp = new Tuple<String,String>(keyTriple.getX(),keyTriple.getY()); 
		transitionProbTriple.put(keyTriple, (transitionCount.get(keyTriple).doubleValue())/transitionSum.get(temp));
	}
	
	}
	
	
	/* ============================================================================
	 * State access methods
	 * ========================================================================= */

		public static POMDPState getNewState(Domain d) {
			POMDPState s = new POMDPState();

			ObjectClass stateHolderClass = d.getObjectClass(P.CLASS_STATE_HOLDER);
			ObjectClass mentalStateClass = d.getObjectClass(P.CLASS_MENTAL_STATE);

			ObjectInstance holder = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);

			ObjectInstance stateStart = new ObjectInstance(mentalStateClass, P.OBJ_STATE_START);
			ObjectInstance stateRash = new ObjectInstance(mentalStateClass, P.OBJ_STATE_RASH);
			ObjectInstance stateNoRash = new ObjectInstance(mentalStateClass, P.OBJ_STATE_NO_RASH);
			ObjectInstance stateGoal = new ObjectInstance(mentalStateClass, P.OBJ_STATE_GOAL);

			holder.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_START);	

			addObjects(s, holder, stateStart, stateRash, stateNoRash,stateGoal);

			return s;
		}

		private static void addObjects(State s, ObjectInstance... objs) {
			for(ObjectInstance object : objs) {
				s.addObject(object);
			}
		}

		public static List<State> getAllStates(Domain d) {
			List<State> ret_val = new ArrayList<State>();

			State s1 = new State();
			State s2 = new State();
			State s3 = new State();
			State s4 = new State();

			ret_val.add(s1);
			ret_val.add(s2);
			ret_val.add(s3);
			ret_val.add(s4);

			ObjectClass stateHolderClass = d.getObjectClass(P.CLASS_STATE_HOLDER);
			ObjectClass mentalStateClass = d.getObjectClass(P.CLASS_MENTAL_STATE);

			ObjectInstance stateStart = new ObjectInstance(mentalStateClass, P.OBJ_STATE_START);
			ObjectInstance stateRash = new ObjectInstance(mentalStateClass, P.OBJ_STATE_RASH);
			ObjectInstance stateNoRash = new ObjectInstance(mentalStateClass, P.OBJ_STATE_NO_RASH);
			ObjectInstance stateGoal = new ObjectInstance(mentalStateClass, P.OBJ_STATE_GOAL);

			for(int i = 0; i < ret_val.size(); ++i) {
				State temp = ret_val.get(i);
				addObjects(temp, stateStart, stateRash, stateNoRash, stateGoal);
			}

			ObjectInstance sh1 = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);
			sh1.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_START);
			s1.addObject(sh1);

			ObjectInstance sh2 = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);
			sh2.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_RASH);
			s2.addObject(sh2);

			ObjectInstance sh3 = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);
			sh3.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_NO_RASH);
			s3.addObject(sh3);

			ObjectInstance sh4 = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);
			sh4.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_GOAL);
			s4.addObject(sh4);

			return ret_val;
		}

//		public List<Action> getAllActions(POMDPDomain dom){
//			List actionList = new ArrayList<Action>();
//			List<GroundedAction> actionListWithoutRepeats = new ArrayList<GroundedAction>();
//			for (State stateEg : dom.getAllStates()){
//				for (Action exampleAction :stateEg.getA
////						getAllGroundedActionsFor(dom.getActions())){
//					actionList.add(exampleAction);
//					//System.out.println("PointBasedValueIteration: " + exampleAction.toString());
//				}
//				
//			}
//			return actionList;
//		}
		
		
		public POMDPState getReturnObservation(POMDPState st, GroundedAction a, POMDPDomain dom){
			//POMDPDomain dom = (POMDPDomain) this.domain;
		
			if(a.toString().equals(P.ACTION_SPEAK)){
				ObjectInstance holder = st.getObject(P.OBJ_HOLDER);
				String state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];
//				String tempState = holder.getStringValForAttribute(P.ATTR_MENTAL_STATE); 
				String temp = "-"+ (int)Math.floor(numberOfRepeatedObservations*Math.random());
				// 50% regular state obs 50% other state obs
				
				double obsStateDecide = Math.random();
				int obsPickOtherState = (int)Math.floor(3*Math.random()); 
//				double obsNoise = 0.5;

			switch(state) {
				case P.OBJ_STATE_START:
					if(obsStateDecide <= 1.0-obsNoise){
					st.setObservation(dom.getObservation(P.OBS_STATE_START+temp));
					}
					else{
						if(obsPickOtherState==0){
							st.setObservation(dom.getObservation(P.OBS_STATE_RASH+temp));
						}
						else if(obsPickOtherState==1){
							st.setObservation(dom.getObservation(P.OBS_STATE_NO_RASH+temp));
						}
						else {
							st.setObservation(dom.getObservation(P.OBS_STATE_GOAL+temp));	
						}
					}
					break;
				case P.OBJ_STATE_RASH:
					if(obsStateDecide <= 1-obsNoise){
						st.setObservation(dom.getObservation(P.OBS_STATE_RASH+temp));
						}
						else{
							if(obsPickOtherState==0){
								st.setObservation(dom.getObservation(P.OBS_STATE_START+temp));
							}
							else if(obsPickOtherState==1){
								st.setObservation(dom.getObservation(P.OBS_STATE_NO_RASH+temp));
							}
							else {
								st.setObservation(dom.getObservation(P.OBS_STATE_GOAL+temp));	
							}
						}
//					st.setObservation(dom.getObservation(P.OBS_STATE_RASH+temp));
					break;
				case P.OBJ_STATE_NO_RASH:
					if(obsStateDecide <= 1-obsNoise){
						st.setObservation(dom.getObservation(P.OBS_STATE_NO_RASH+temp));
						}
						else{
							if(obsPickOtherState==0){
								st.setObservation(dom.getObservation(P.OBS_STATE_START+temp));
							}
							else if(obsPickOtherState==1){
								st.setObservation(dom.getObservation(P.OBS_STATE_RASH+temp));
							}
							else {
								st.setObservation(dom.getObservation(P.OBS_STATE_GOAL+temp));	
							}
						}
//					st.setObservation(dom.getObservation(P.OBS_STATE_NO_RASH+temp));
					break;
				case P.OBJ_STATE_GOAL:
					if(obsStateDecide <= 1-obsNoise){
						st.setObservation(dom.getObservation(P.OBS_STATE_GOAL+temp));
						}
						else{
							if(obsPickOtherState==0){
								st.setObservation(dom.getObservation(P.OBS_STATE_START+temp));
							}
							else if(obsPickOtherState==1){
								st.setObservation(dom.getObservation(P.OBS_STATE_RASH+temp));
							}
							else {
								st.setObservation(dom.getObservation(P.OBS_STATE_NO_RASH+temp));	
							}
						}
//					st.setObservation(dom.getObservation(P.OBS_STATE_GOAL+temp));
					break;
			}

			return st;
//			st.setObservation()}
			}
			else{
				String temp = "-"+ (int)Math.floor(numberOfRepeatedObservations*Math.random());
				System.out.println(P.OBS_NULL+temp);
				st.setObservation(dom.getObservation(P.OBS_NULL+temp));
				return st;
			}
			 
		}
	
	/* ============================================================================
	 * Speak action definition
	 * ========================================================================= */
		
		

		public class SpeakAction extends Action {
			public SpeakAction(Domain domain, String name) {
				super(name, domain, new String[]{});
			}

			@Override
			public State performActionHelper(State st, String[] params) {
				POMDPState ps = new POMDPState(st);
				POMDPDomain dom = (POMDPDomain) domain;
				String temp = "-"+ (int)Math.floor(numberOfRepeatedObservations*Math.random());
				System.out.println(temp);

				ObjectInstance holder = ps.getObject(P.OBJ_HOLDER);
				String state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];
				
				double obsStateDecide = Math.random();
				int obsPickOtherState = (int)Math.floor(3*Math.random()); 
//				double obsNoise = 0.5;
				
				switch(state) {
					case P.OBJ_STATE_START:
						if(obsStateDecide <= 1.0-obsNoise){
							ps.setObservation(dom.getObservation(P.OBS_STATE_START+temp));
							}
							else{
								if(obsPickOtherState==0){
									ps.setObservation(dom.getObservation(P.OBS_STATE_RASH+temp));
								}
								else if(obsPickOtherState==1){
									ps.setObservation(dom.getObservation(P.OBS_STATE_NO_RASH+temp));
								}
								else {
									ps.setObservation(dom.getObservation(P.OBS_STATE_GOAL+temp));	
								}
							}
//						ps.setObservation(dom.getObservation(P.OBS_STATE_START+temp));
						break;
					case P.OBJ_STATE_RASH:
						if(obsStateDecide <= 1-obsNoise){
							ps.setObservation(dom.getObservation(P.OBS_STATE_RASH+temp));
							}
							else{
								if(obsPickOtherState==0){
									ps.setObservation(dom.getObservation(P.OBS_STATE_START+temp));
								}
								else if(obsPickOtherState==1){
									ps.setObservation(dom.getObservation(P.OBS_STATE_NO_RASH+temp));
								}
								else {
									ps.setObservation(dom.getObservation(P.OBS_STATE_GOAL+temp));	
								}
							}
//						ps.setObservation(dom.getObservation(P.OBS_STATE_RASH+temp));
						break;
					case P.OBJ_STATE_NO_RASH:
						if(obsStateDecide <= 1-obsNoise){
							ps.setObservation(dom.getObservation(P.OBS_STATE_NO_RASH+temp));
							}
							else{
								if(obsPickOtherState==0){
									ps.setObservation(dom.getObservation(P.OBS_STATE_START+temp));
								}
								else if(obsPickOtherState==1){
									ps.setObservation(dom.getObservation(P.OBS_STATE_RASH+temp));
								}
								else {
									ps.setObservation(dom.getObservation(P.OBS_STATE_GOAL+temp));	
								}
							}
//						ps.setObservation(dom.getObservation(P.OBS_STATE_NO_RASH+temp));
						break;
					case P.OBJ_STATE_GOAL:
						if(obsStateDecide <= 1-obsNoise){
							ps.setObservation(dom.getObservation(P.OBS_STATE_GOAL+temp));
							}
							else{
								if(obsPickOtherState==0){
									ps.setObservation(dom.getObservation(P.OBS_STATE_START+temp));
								}
								else if(obsPickOtherState==1){
									ps.setObservation(dom.getObservation(P.OBS_STATE_RASH+temp));
								}
								else {
									ps.setObservation(dom.getObservation(P.OBS_STATE_NO_RASH+temp));	
								}
							}
//						ps.setObservation(dom.getObservation(P.OBS_STATE_GOAL+temp));
						break;
				}

				return ps;
			}

			@Override
			public List<TransitionProbability> getTransitions(State s, String[] params) {
				List<TransitionProbability> trans = new ArrayList<TransitionProbability>();

				List<State> states = QDWithTransitionAndObservationLearning.getAllStates(domain);

				ObjectInstance holder = s.getObject(P.OBJ_HOLDER);
				String holder_state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

				for(State test_state : states) {
					
					ObjectInstance test_holder = test_state.getObject(P.OBJ_HOLDER);
					String test_mental_state = (String) test_holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

					if(holder_state.equals(test_mental_state)) {
						trans.add(new TransitionProbability(test_state, 1));
					} else {
						trans.add(new TransitionProbability(test_state, 0));
					}
				}

				return trans;
			}
		}

	/* ============================================================================
	 * Advance action definition
	 * ========================================================================= */

		public class AdvanceAction extends Action {

			private String fromState;
			protected Random rand;
			private String toState;

			public AdvanceAction(Domain domain, String name, String from_state) {
				super(name, domain, new String[]{});
				fromState = from_state;
			}

			@Override 
			public boolean applicableInState(State st, String[] params) {
				//System.out.println("[POMDPDiaperDomain Test:] applicable in state "+st.toString()+ " Action: "+this.name);
				 ObjectInstance holder = st.getObject(P.OBJ_HOLDER);
				 String mental_state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];
				 //System.out.println("[POMDPDiaperDomain Test:] applicable in state: holder "+ mental_state+ " action from:"+ fromState+ " Boolean output " + mental_state.equals(fromState)); 
				 return mental_state.equals(fromState);
				//return true;
			}

			@Override
			public State performActionHelper(State st, String[] params) {
				POMDPState ps = new POMDPState(st);
				POMDPDomain dom = (POMDPDomain) domain;

				ObjectInstance holder = st.getObject(P.OBJ_HOLDER);
//				ObjectInstance holder = ps.getObject(P.OBJ_HOLDER);
				String state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];
//				if (this.applicableInState(ps, new String[]{""}))
				List<TransitionProbability> tempList = this.getTransitions(st, new String[]{""});
				//for(TransitionProbability numberIn : tempList){
				//	System.out.println("QuestionDomain: State "+numberIn.s+" probability " + numberIn.p);
				//}
				System.out.println();
				//double rollADie =rand.nextDouble();
				double rollADie = Math.random();
				double sum=0.0;
				State transitionState=new State();
				for (TransitionProbability p : tempList){
					sum+=p.p;
					if (sum>=rollADie){
//						System.out.println("QuestionDomain: State "+p.s+" probability " + p.p);
						transitionState=p.s;
						break;
					}
				}
				//System.out.println("QuestionDomain: transition state "+transitionState);
				toState = (String) (transitionState.getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0]);
				
				
				holder.addRelationalTarget(P.ATTR_MENTAL_STATE, toState);
				POMDPState transitionStatePOMDP = new POMDPState(transitionState);
				
				String temp = "-"+ (int)Math.floor(numberOfRepeatedObservations*Math.random());
				System.out.println(P.OBS_NULL+temp);

				transitionStatePOMDP.setObservation(dom.getObservation(P.OBS_NULL+temp));
				return transitionStatePOMDP;
			}

			@Override
			public List<TransitionProbability> getTransitions(State s, String[] params) {
				List<TransitionProbability> trans = new ArrayList<TransitionProbability>();
				List<State> states = QDWithTransitionAndObservationLearning.getAllStates(domain);
				if(this.applicableInState(s,new String[]{""} )){
				switch(this.name){
				case P.ACTION_ADVANCE_NO_RASH:
					for (State tempState :states){
						String tempToState = (String) (tempState.getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0]);
						if (tempToState.equals(P.OBJ_STATE_GOAL)){
							trans.add(new TransitionProbability(tempState, 1.0));
						}
						else{
							trans.add(new TransitionProbability(tempState, 0.0));
						}
						
					}
					break;
				case P.ACTION_ADVANCE_RASH:
					for (State tempState :states){
						String tempToState = (String) (tempState.getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0]);
						if (tempToState.equals(P.OBJ_STATE_NO_RASH)){
							trans.add(new TransitionProbability(tempState, 1.0));
						}
						else{
							trans.add(new TransitionProbability(tempState, 0.0));
						}
						
					}
					
					break;
				case P.ACTION_ADVANCE_START_STATE:
					for (State tempState :states){
						String tempToState = (String) (tempState.getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0]);
						if (tempToState.equals(P.OBJ_STATE_NO_RASH)){
							trans.add(new TransitionProbability(tempState, 0.5));
						}
						else if(tempToState.equals(P.OBJ_STATE_RASH)){
							trans.add(new TransitionProbability(tempState, 0.5));
						}
						else {
							trans.add(new TransitionProbability(tempState, 0.0));
						}	
					}
					break;
							
				}}
				else{
					for (State tempState :states){
					if (tempState.equals(s)){trans.add(new TransitionProbability(tempState,1.0));}
					else{trans.add(new TransitionProbability(tempState,0.0));}
					}
				}
				
				
//				int indexState = states.indexOf(s);
//
//				ObjectInstance holder = s.getObject(P.OBJ_HOLDER);
//				String holder_state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];
//
//				for(State test_state : states) {
//					int testIndex=states.indexOf(test_state);
//					//if (globalFlag==0 && Math.abs(testIndex)==1){
//						
//						//{System.out.println("POMDPDiaperDomain: test index = " + testIndex + " state index= " + indexState);}
//						
//				//	}
//					
//					
//					ObjectInstance test_holder = test_state.getObject(P.OBJ_HOLDER);
//					String test_mental_state = (String) test_holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];
//
//					if (test_mental_state.equals(toState) && indexState==5 && holder_state.equals(fromState)){
//						trans.add(new TransitionProbability(test_state, 1 - TransitionNoise/2));
//					}
//					else if(test_mental_state.equals(toState) && holder_state.equals(fromState) && !holder_state.equals(P.OBJ_STATE_Y)) {
//						trans.add(new TransitionProbability(test_state, 1 - TransitionNoise));
//					//}else if(test_mental_state.equals(fromState) && holder_state.equals(fromState) && !holder_state.equals(P.OBJ_STATE_Y)) {
//					//	trans.add(new TransitionProbability(test_state, TransitionNoise));
//					}
//					else if(!holder_state.equals(fromState) && test_mental_state.equals(holder_state) && !holder_state.equals(P.OBJ_STATE_Y)) {
//						trans.add(new TransitionProbability(test_state, 1));
//					} else if (((indexState - testIndex) == 0 || (-indexState + testIndex) == 2) && holder_state.equals(fromState)){
//						trans.add(new TransitionProbability(test_state, TransitionNoise/2));
//					//	System.out.println("POMDPDiaperDomain testIndex check " +  TransitionNoise/2);
//					}
//					else {
//						trans.add(new TransitionProbability(test_state, 0.0));
//					}
//				}
//				//if (globalFlag<=20){
//					//for(TransitionProbability t:trans)
//					//{System.out.println("POMDPDiaperDomain: Trans state = " + t.s.toString()+" probability = " + t.p);}
//					//globalFlag+=1;
//				//}

				return trans;
			}
		}
	
		
		/* ============================================================================
		 * Observation definition change probability here for changing the learner's behaviour
		 * ========================================================================= */

			public class DialogueObservation extends Observation {
				
				private String my_state;
				private String obs_name;
				private String obsSample;

				public DialogueObservation(Domain d, String s, String state, String exampleObs) {
					super((POMDPDomain)d, s);
					this.my_state = state;
					this.obs_name=s;
					this.obsSample=exampleObs;
				}

				@Override
				public double getProbability(State s, GroundedAction a) {
					//System.out.println("QuestionDomain: action " + a.toString()); 
						//	System.out.println(" logic " + a.toString().equals(P.ACTION_SPEAK));
					
//					System.out.println("QuestionDomain: action " + a.toString() + " logic " + a.toString().equals(P.ACTION_SPEAK));

					ObjectInstance holder = s.getObject(P.OBJ_HOLDER);
					String state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];
//					System.out.println("QuestionDomain: input state " + state + " obs state "+this.my_state + " logic " + this.my_state.equals(state));
					
					if(state.equals(this.my_state) && a.toString().equals(P.ACTION_SPEAK) && !this.obs_name.split("-")[0].equals(P.OBS_NULL)) {//this.my_state
//						System.out.println("QuestionDomain: action " + a.toString() + " state " + state +" obs " + this.name + " probability " + 1.0);
//						if(Integer.parseInt(this.obs_name.split("-")[1]) % 2 == 0){
						if(state.equals(P.OBJ_STATE_START) && this.obs_name.split("-")[0].equals(P.OBS_STATE_START)){
							return (1-obsNoise)*1.0/(numberOfRepeatedObservations);
						}
						else if(state.equals(P.OBJ_STATE_RASH) && this.obs_name.split("-")[0].equals(P.OBS_STATE_RASH)){
							return (1-obsNoise)*1.0/(numberOfRepeatedObservations);
						}
						else if(state.equals(P.OBJ_STATE_NO_RASH) && this.obs_name.split("-")[0].equals(P.OBS_STATE_NO_RASH)){
							return (1-obsNoise)*1.0/(numberOfRepeatedObservations);
						}
						else if(state.equals(P.OBJ_STATE_GOAL) && this.obs_name.split("-")[0].equals(P.OBS_STATE_GOAL)){
							return (1-obsNoise)*1.0/(numberOfRepeatedObservations);
						}
						else{
							return obsNoise*1.0/(3*numberOfRepeatedObservations);
						}
						
							
//							}
//							else{return 1.0/numberOfRepeatedObservations+0.1/numberOfRepeatedObservations;}
							
						
					}
					else if(a.toString().equals(P.ACTION_SPEAK)){
//						System.out.println("QuestionDomain: action " + a.toString() + " state " + state +" obs " + this.name + " probability " + 0.0);
						return 0.0;
						}
					else if(!this.obs_name.split("-")[0].equals(P.OBS_NULL)){
//						System.out.println("QuestionDomain: action " + a.toString() + " state " + state +" obs " + this.name + " probability " + (1.0)/12);
						return (0.0);
					}
					else{
//						if(Integer.parseInt(this.obs_name.split("-")[1]) % 2 == 0){
						return 1.0/numberOfRepeatedObservations;
//						}
//						else{return 1.0/numberOfRepeatedObservations+0.1/numberOfRepeatedObservations;}
						}
				}
			}
	
	
}
