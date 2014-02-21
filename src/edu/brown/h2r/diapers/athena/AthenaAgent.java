package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.util.Tuple;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.State;

import java.util.List;
import java.util.ArrayList;


public class AthenaAgent extends Agent {
	private POMDPDomain domain;
	private List<Double> currentBeliefState = new ArrayList<Double>();
	private HLPlanner hlplanner;
	private LLPlanner llplanner;

	public AthenaAgent(Environment e) {
		super(e);
		init();
	}

	public void init() {
		System.out.println("[AthenaAgent.init()] Creating planners");

		hlplanner = new HLPlanner();
		llplanner = new LLPlanner(environment);

		System.out.println("[AthenaAgent.init()] Building domain");
		domain = (POMDPDomain)(new POMDPDiaperDomain().generateDomain());

		System.out.println("[AthenaAgent.init()] Initializing high-level planner");
		hlplanner.init();

		System.out.println("[AthenaAgent.init()] Building initial (uniform) belief state");
		for(int i = 0; i < domain.getAllStates().size(); ++i) {
			currentBeliefState.add(new Double(1/(double)domain.getAllStates().size()));
		}
	}

	public void run() {

		System.out.println("[AthenaAgent.run()] Agent running!");

		while(true) {

			System.out.println("[AthenaAgent.run()] current belief state: " + currentBeliefState);

			String bestPOMDPAction = hlplanner.getBestAction(currentBeliefState);

			System.out.println("[AthenaAgent.run()] High-level planner returned " + bestPOMDPAction);
			System.out.println("[AthenaAgent.run()] Converting to low-level actions...");

			List<Tuple<Action, String[]>> bestOOMDPActions = llplanner.convert(bestPOMDPAction);
			
			System.out.print("[AthenaAgent.run()] Low-level planner returned ");

			for(Tuple<Action, String[]> tup : bestOOMDPActions) {
				System.out.print(tup.getX().getName() + " with params " + tup.getY() + ", ");
				environment.perform(tup.getX(), tup.getY());
			}
			System.out.println();

			Observation o = environment.observe();
			currentBeliefState = forward(currentBeliefState, o, domain.getAction(bestPOMDPAction), new String[]{});
			if(inGoalState(o)) break;

			System.out.println("[AthenaAgent.run()] Not in goal state yet, continuing...");
			System.out.println("-----");
		}
		System.out.println("[AthenaAgent.run()] Done!");
	}

	public boolean inGoalState(Observation o) {
		List<State> allStates = domain.getAllStates();
		return o.getProbability(allStates.get(allStates.size() - 1), null) == 1;
	}

	private List<Double> forward(List<Double> prevBeliefState, Observation observation, Action action, String[] params) {
		List<State> allStates = domain.getAllStates();

		List<Double> newBeliefState = new ArrayList<Double>();

		for(int sPrimeIndex = 0; sPrimeIndex < allStates.size(); sPrimeIndex++) {
			double sum = 0;
			double obsProb = observation.getProbability(allStates.get(sPrimeIndex), null);

			for(int currStateIndex = 0; currStateIndex < allStates.size(); currStateIndex++) {
				double tprob = getTransitionProbability(allStates.get(currStateIndex), allStates.get(sPrimeIndex), action, params);
				tprob *= currentBeliefState.get(currStateIndex);
				sum += tprob;
			}

			sum *= obsProb;
			newBeliefState.add(sum);
		}

		System.out.println("belief state prior to normalization " + newBeliefState);
		normalizeList(newBeliefState);
		System.out.println("belief state after normalization " + newBeliefState);
		return newBeliefState;
	}

	private double getTransitionProbability(State s1, State s2, Action a, String[] params) {
		List<TransitionProbability> tplist = a.getTransitions(s1, params);
		for(TransitionProbability tp : tplist) {
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
		for(int i = 0; i < in.size(); ++i) {
			in.set(i, in.get(i)/sum);
		}
	}
}