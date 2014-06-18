package edu.brown.h2r.diapers.rashNoRashTestDomain;


//import edu.brown.h2r.diapers.athena.PBVIBehavior;
//import edu.brown.h2r.diapers.athena.PBVIBehavior.AthenaRewardFunction;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.pomdp.PointBasedValueIteration;
import edu.brown.h2r.diapers.util.Tuple;
import edu.brown.h2r.diapers.rashNoRashTestDomain.P;
import edu.brown.h2r.diapers.rashNoRashTestDomain.QuestionDomainMoreObservations;

import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.State;
//import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.auxiliary.StateParser;
import burlap.oomdp.auxiliary.common.UniversalStateParser;
import burlap.behavior.singleagent.planning.StateConditionTest;
import burlap.behavior.statehashing.NameDependentStateHashFactory;
import burlap.behavior.statehashing.StateHashTuple;
import burlap.oomdp.singleagent.RewardFunction;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class QuestionBehaviour {
	public static PointBasedValueIteration pbvi;
	private List<Tuple<GroundedAction, double[]>> result;

	private QuestionDomainMoreObservations questionDomain;
	private POMDPDomain domain;
	private StateParser stateParser;
	private RewardFunction rewardFunction;
	private StateConditionTest goalCondition;
	private State initialState;
	private NameDependentStateHashFactory hashFactory;
	
	public QuestionBehaviour() {
		questionDomain = new QuestionDomainMoreObservations();
		domain = (POMDPDomain) questionDomain.generateDomain();
		stateParser = new UniversalStateParser(domain);
		initialState = questionDomain.getNewState(domain);
		hashFactory = new NameDependentStateHashFactory();
		rewardFunction = new QuestionRewardFunction();
		System.out.println("observation size "+domain.getObservations().size());
//		for(count = 0;count<domain.getObservations().size();count++){System.out.println(domain.)}
		int granularity=8;
		boolean doCompleteVI=false;
		
		this.pbvi = makePBVIInstance(granularity);
		this.pbvi.setDataPath("src\\edu\\brown\\h2r\\diapers\\data\\"); 
		
		this.pbvi.doValueIteration(doCompleteVI);

		System.out.println("[QuestionBehaviour()] Running PBVI...");
		long init = System.nanoTime();
		//pbvi.doValueIteration(false);
		result = this.pbvi.getAplhaVectors();
		long duration = System.nanoTime() - init;
		System.out.println("[QuestionBehaviour()] PBVI complete in " + duration + " nano seconds!");
	}

	private PointBasedValueIteration makePBVIInstance(int granularity) {
		//final List<List<Double>> bp = belief_points;
		final int myGranularity=granularity;
		return new PointBasedValueIteration() {{
			setDomain(domain);
			setStates(domain.getAllStates());
			setGranularity(myGranularity);
			setHashFactory(hashFactory);
			setRewardFunction(rewardFunction);
			setGamma(0.95);
			setMaxDelta(0.001);
			setMaxIterations(30);
		}};
	}

	
	public class QuestionRewardFunction implements RewardFunction {
		public double reward(State s, GroundedAction a, State sprime) {
			
			ObjectInstance holder = s.getObject(P.OBJ_HOLDER);
			String holder_state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];
			//System.out.println("HLPPLanner: action "+a.toString()+" state: "+s.toString() + " holder state " + holder_state);
			if(holder_state.equals(P.OBJ_STATE_GOAL)){
				//if(a.toString().equals(P.ACTION_SA_ADVANCE)){System.out.println("HLPPLanner: advance A 1");}
				return 0.0;}
			else if(a.toString().equals(P.ACTION_SPEAK)) {
//				System.out.println("QuestionBehaviour.QuestionRewardfunction: speak action " + a.action.getName() + " state " + s.toString());
				return -0.5;
			}
			else if(!a.action.applicableInState(s, new String[]{""})) {
				//if(a.toString().equals(P.ACTION_SA_ADVANCE)){System.out.println("HLPPLanner: advance A 2");}
//				System.out.println("QuestionBehaviour.QuestionRewardfunction: wrong action " + a.action.getName() + " state " + s.toString());
				return -1.0;//reward for wrong action!
			}
			else{
				//System.out.println("HLPPLanner: right action");
//				System.out.println("QuestionBehaviour.QuestionRewardfunction: right action "+a.toString()+" state: "+s.toString());
				return -1.0;
			}
		}
	}
	public String getBestAction(List<Double> beliefState) {
		//System.out.println(beliefState);
		//System.out.println(this.pbvi.getAplhaVectors().size());
		//System.out.println(beliefState.size());
		return this.pbvi.findClosestBeliefPointIndex(beliefState); 
	}
	
	public static void main(String[] args) throws IOException {
		double testReward=0.0;
		int totalCount=1;
		for(int mainCount=0;mainCount<totalCount;mainCount++){
			
		QuestionBehaviour test = new QuestionBehaviour();
		POMDPState startState=test.domain.getExampleState();
//		test.domain.
		List<Double> input_bp = new ArrayList<Double>();
		List<Double> output_bp = new ArrayList<Double>();
		input_bp.add(0.250);
		input_bp.add(0.250);
		input_bp.add(0.250);
		input_bp.add(0.250);
		while(startState.getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0]!=P.OBJ_STATE_GOAL){
		
		GroundedAction tempAction =pbvi.getBestAction(input_bp); 
		//System.out.println("Best POMDP Action: " + namesArray[j]);
		System.out.println("Best POMDP Action: " + tempAction.toString());
		State oldState = startState;
		startState = new POMDPState(tempAction.executeIn(startState));
		System.out.println(startState.toString());
		startState=test.questionDomain.getReturnObservation(startState, tempAction, test.domain);
		testReward+=test.rewardFunction.reward(oldState,tempAction,startState);
		System.out.println(startState.getObservation().getName());
		System.out.println(startState.getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0]);
		output_bp= test.forward(input_bp, startState.getObservation(), tempAction, new String[]{""});
		input_bp=output_bp;
		
	}
		
		}
		double averageReward=testReward/totalCount;
		System.out.println("Question Behaviour: Average reward with POMDP based actions = " + averageReward);
	}
	
	private List<Double> forward(List<Double> prevBeliefState, Observation observation, GroundedAction action, String[] params) {
		List<State> allStates = domain.getAllStates();

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
			if(statesAreEqual(tp.s, s2)) {
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

	private boolean statesAreEqual(State s1, State s2) {
		Object st1 =s1.getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];
		Object st2 =s2.getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];
		return st1.equals(st2);
	}
}
