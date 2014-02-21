package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.util.Tuple;
import edu.brown.h2r.diapers.athena.namespace.P;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.State;
import burlap.oomdp.core.Domain;
import burlap.behavior.statehashing.NameDependentStateHashFactory;
import burlap.behavior.statehashing.StateHashTuple;
import burlap.oomdp.singleagent.GroundedAction;

import java.util.List;
import java.util.ArrayList;

public class RandomAgent extends Agent {
	
	private LLPlanner llplanner;
	private Domain domain;
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
	private List<Action> llactions;

	public RandomAgent(Environment e) {
		super(e);
		init();
	}

	public void init() {
		llplanner = new LLPlanner(environment);
		domain = (new DiaperDomain().generateDomain());
		llactions = domain.getActions();
	}

	public void run() {
		while(true) {
			Tuple<Action, String[]> actiontup = chooseActionAtRandom();
			System.out.println(actiontup.getX() + ", " + actiontup.getY());
			environment.perform(actiontup.getX(), actiontup.getY());

			Observation o = environment.observe();
			if(inGoalState(o)) break;
		}
	}

	public boolean inGoalState(Observation o) {
		return false;
	}

	private Tuple<Action, String[]> chooseActionAtRandom() {
		List<GroundedAction> allPossible = new ArrayList<GroundedAction>();
		while(true) {
			int randIndex = (int) (new java.util.Random().nextDouble() * llactions.size());
			Action chosen = llactions.get(randIndex);
			allPossible = environment.getCurrentState().getAllGroundedActionsFor(chosen);
			if(allPossible.size() > 0) break;
		}
		int randIndex = (int) (new java.util.Random().nextDouble() * allPossible.size());
		GroundedAction gaChosen = allPossible.get(randIndex);
		return new Tuple<Action, String[]>(gaChosen.action, gaChosen.params);
	}
}