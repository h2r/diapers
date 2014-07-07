package edu.brown.h2r.diapers.domain.easy_diaper;

import java.util.ArrayList;
import java.util.List;

import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.solver.pbvi.PointBasedValueIteration;
import edu.brown.h2r.diapers.util.Tuple;

import burlap.behavior.singleagent.planning.StateConditionTest;
import burlap.behavior.statehashing.NameDependentStateHashFactory;
import burlap.oomdp.auxiliary.StateParser;
import burlap.oomdp.auxiliary.common.UniversalStateParser;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;

public class RashBehaviour {
	private RashDomain rashDomain;
//	private List<State> stateList;
	private POMDPDomain domain;
	
	public static PointBasedValueIteration pbvi;
	private List<Tuple<GroundedAction, double[]>> result;

	private StateParser stateParser;// where is this getting used??
	private RewardFunction rewardFunction;
	private StateConditionTest goalCondition;
	private POMDPState initialState;
	private NameDependentStateHashFactory hashFactory;
	
	
	
	public RashBehaviour(){
		rashDomain = new RashDomain();
		domain = (POMDPDomain)rashDomain.generateDomain();
//		stateList = pbvi.getStateList();
		initialState = domain.sampleInitialState();
		
		hashFactory = new NameDependentStateHashFactory();
		rewardFunction = new RashRewardFunction();
		stateParser = new UniversalStateParser(domain);
		
		System.out.println("observation size "+ domain.getObservations().size());
//		for(count = 0;count<domain.getObservations().size();count++){System.out.println(domain.)}
		int granularity=2;
		boolean doCompleteVI=true;
		
		this.pbvi = makePBVIInstance(granularity);
		this.pbvi.setDataPath("src\\edu\\brown\\h2r\\diapers\\data\\"); 
		long init = System.nanoTime();
		this.pbvi.doValueIteration(doCompleteVI);

		System.out.println("[RashBehaviour()] Running PBVI...");
		//long init = System.nanoTime();
		//pbvi.doValueIteration(false);
		result = this.pbvi.getAplhaVectors();
		long duration = System.nanoTime() - init;
		System.out.println("[RashBehaviour()] PBVI complete in " + duration + " nano seconds!");
	
	}

	
	
	private PointBasedValueIteration makePBVIInstance(int granularity) {
		//final List<List<Double>> bp = belief_points;
		final int myGranularity=granularity;
		return new PointBasedValueIteration() {{
			setHashFactory(hashFactory);
			setDomain(domain);
			setGranularity(myGranularity);
			setRewardFunction(rewardFunction);
			setGamma(0.95);
			setMaxDelta(0.001);
			setMaxIterations(30);
		}};
	}
	
	
	
	public String getBestAction(List<Double> beliefState) {
		//System.out.println(beliefState);
		//System.out.println(this.pbvi.getAplhaVectors().size());
		//System.out.println(beliefState.size());
		return this.pbvi.findClosestBeliefPointIndex(beliefState); 
	}
	
	

	
	public class RashRewardFunction implements RewardFunction {
		public double reward(State s, GroundedAction a, State sprime) {
			if(s.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE)!=Names.MS_TYPE_GOAL)
				return -1.0;
			else
				return 0.0;
			}
		
	}
	
	private List<Double> forward(List<Double> prevBeliefState, Observation observation, GroundedAction action, String[] params) {
		List<State> allStates = pbvi.getStateList();

		List<Double> newBeliefState = new ArrayList<Double>();

		for(int sPrimeIndex = 0; sPrimeIndex < allStates.size(); sPrimeIndex++) {
			double sum = 0;
			double obsProb = observation.getProbability(allStates.get(sPrimeIndex), action);

			for(int currStateIndex = 0; currStateIndex < allStates.size(); currStateIndex++) {
				double tprob = getTransitionProbability(allStates.get(currStateIndex), allStates.get(sPrimeIndex), action, params);
				tprob *= prevBeliefState.get(currStateIndex);
				sum += tprob;
			}

			sum *= obsProb;
			newBeliefState.add(sum);
		}

		//System.out.println("belief state prior to normalization " + newBeliefState);
		normalizeList(newBeliefState);
		//System.out.println("belief state after normalization " + newBeliefState);
		return newBeliefState;
	}

	private double getTransitionProbability(State s1, State s2, GroundedAction a, String[] params) {
		//System.out.println(a.getName());
//		Action aNew = 
		List<TransitionProbability> tplist = a.action.getTransitions(s1, new String[]{""});
		for(TransitionProbability tp : tplist) {
			//System.out.println(tp.p);
			//System.out.println(tp.s);
			if(tp.s.equals(s2)) {
				return tp.p;
			}
		}
		return 0.0;
	}

	public void normalizeList(List<Double> in) {
		double sum = 0;
		for(int i = 0; i < in.size(); ++i) {
			sum += in.get(i);
		}
		if(sum == 0) return;
		for(int i = 0; i < in.size(); ++i) {
			in.set(i, in.get(i)/sum);
		}
	}

	
	
	

	public static void main(String[] args){
		
			
		double testReward=0.0;
		int totalCount=100;
		
		RashBehaviour rashBehaviour = new RashBehaviour();
		
		for(int i=0;i<totalCount;i++){
		POMDPState initialState = rashBehaviour.initialState;
		List<State> allStates = pbvi.getStateList();
		List<Double> inputBP = new ArrayList<Double>();
		List<Double> outputBP = new ArrayList<Double>();
		for(State s: allStates){
			if(s.equals(initialState)){
				inputBP.add(1.0);
			}
			else{
				inputBP.add(0.0);
			}
			
		}
		
		POMDPState startState = new POMDPState(initialState.copy());
		while(startState.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE)!=Names.MS_TYPE_GOAL){
			
			GroundedAction tempAction =pbvi.getBestAction(inputBP); 
			//System.out.println("Best POMDP Action: " + namesArray[j]);
			System.out.println("Best POMDP Action: " + tempAction.toString());
			State oldState = startState;
			startState = new POMDPState(tempAction.executeIn(startState));
			System.out.println(startState);
			startState=rashBehaviour.rashDomain.getObservation(startState, tempAction, rashBehaviour.domain);
			testReward+=rashBehaviour.rewardFunction.reward(oldState,tempAction,startState);
			System.out.println(startState.getObservation().getName());
			System.out.println(startState.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE));
			outputBP= rashBehaviour.forward(inputBP, startState.getObservation(), tempAction, new String[]{""});
			inputBP=outputBP;
			
		}
		}

		System.out.println("net reward: " +testReward/totalCount);
		
			
		// The next portion is just sanity test for the domain in terms of actions and states
		/*
		 		 
		 System.out.println(rashBehaviour.stateList.size());
		List<State> stateList = rashBehaviour.stateList;
		for(State s: stateList){
			System.out.println("state: "+ stateList.indexOf(s));
			System.out.println(s);
		} 
		if(false){
		List<String> actionList = new ArrayList<String>();
		State s = rashBehaviour.domain.getExampleState();
		System.out.println("example state: "+ s);
		State sTick = rashBehaviour.domain.getAction(Names.ACTION_NULL).performAction(s, new String[]{});
		actionList.add(Names.ACTION_NULL);
		System.out.println(sTick);
		
		if(false){
		System.out.println("bring diaper");
		
		State sTick1 = rashBehaviour.domain.getAction(Names.ACTION_BRING_DIAPER).performAction(sTick, new String[]{});
//		actionList.add(Names.ACTION_NULL);
		System.out.println(sTick1);
		
		System.out.println("bring ointment");
		sTick1 = rashBehaviour.domain.getAction(Names.ACTION_BRING_OINTMENT).performAction(sTick, new String[]{});
//		actionList.add(Names.ACTION_NULL);
		System.out.println(sTick1);
		}
		
		if(true){
		String mentalState1 = sTick.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
		if (mentalState1.equals(Names.MS_TYPE_RASH)){
			sTick = rashBehaviour.domain.getAction(Names.ACTION_BRING_OINTMENT).performAction(sTick, new String[]{});
			actionList.add(Names.ACTION_BRING_OINTMENT);
		}
		else if (mentalState1.equals(Names.MS_TYPE_NO_RASH)){
			sTick = rashBehaviour.domain.getAction(Names.ACTION_BRING_DIAPER).performAction(sTick, new String[]{});
			actionList.add(Names.ACTION_BRING_DIAPER);
		}
		System.out.println(sTick);
		mentalState1 = sTick.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
		if (mentalState1.equals(Names.MS_TYPE_NO_RASH)){
			sTick = rashBehaviour.domain.getAction(Names.ACTION_BRING_DIAPER).performAction(sTick, new String[]{});
			actionList.add(Names.ACTION_BRING_DIAPER);
			System.out.println(sTick);
		}
		}
		
		for(String tempAction : actionList){
			System.out.println(tempAction);
		}
	}
	*/
	}
}
