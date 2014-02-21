package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.util.Tuple;
import edu.brown.h2r.diapers.athena.namespace.P;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.State;
import burlap.behavior.statehashing.NameDependentStateHashFactory;
import burlap.behavior.statehashing.StateHashTuple;

import java.util.List;
import java.util.ArrayList;

public class RandomAgent extends Agent {
	
	private LLPlanner llplanner;
	private POMDPDomain domain;
	private String[] possibleActions = 
		new String[]{
			P.ACTION_SPEAK,
			P.ACTION_SX_ADVANCE,
			P.ACTION_SA_ADVANCE,
			P.ACTION_SB_ADVANCE,
			P.ACTION_SC_ADVANCE,
			P.ACTION_SD_ADVANCE,
			P.ACTION_SE_ADVANCE
		};

	public RandomAgent(Environment e) {
		super(e);
		init();
	}

	public void init() {
		llplanner = new LLPlanner(environment);
		domain = (POMDPDomain)(new POMDPDiaperDomain().generateDomain());
	}

	public void run() {
		while(true) {
			String bestPOMDPAction = chooseActionAtRandom();
			List<Tuple<Action, String[]>> bestOOMDPActions = llplanner.convert(bestPOMDPAction);

			for(Tuple<Action, String[]> tup : bestOOMDPActions) {
				environment.perform(tup.getX(), tup.getY());
			}

			Observation o = environment.observe();
			if(inGoalState(o)) break;
		}
	}

	public boolean inGoalState(Observation o) {
		List<State> allStates = domain.getAllStates();
		return o.getProbability(allStates.get(allStates.size() - 1), null) == 1;
	}

	private String chooseActionAtRandom() {
		int randIndex = (int) (new java.util.Random().nextDouble() * possibleActions.length);
		return possibleActions[randIndex];
	}
}