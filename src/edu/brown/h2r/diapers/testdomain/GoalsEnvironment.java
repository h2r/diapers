package edu.brown.h2r.diapers.testdomain;

import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.athena.Environment;
import edu.brown.h2r.diapers.athena.Agent;
import edu.brown.h2r.diapers.util.ANSIColor;

import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.Action;

import java.util.List;
import java.util.ArrayList;

public class GoalsEnvironment implements Environment {
	private State currentState;
	private POMDPDomain domain = (POMDPDomain) new GoalsDomain().generateDomain();
	private Agent agent;

	public GoalsEnvironment(State initialState) {
		currentState = initialState;
	}

	public void addAgent(Agent a) {
		agent = a;
	}

	public Observation observe() {
		return ((POMDPState)currentState).getObservation();
	}

	public void perform(Action a, String[] params) {
		if(a.applicableInState(currentState, params)) {
			ANSIColor.purple("[GoalsEnvironment.perform()] ");
			System.out.print("The agent chose to perform action ");
			ANSIColor.purple(a.getName());
			System.out.print(" with parameters [");
			for(String param : params) {
				System.out.print(param + ",");
			}
			System.out.print("].");
			System.out.println();
			currentState = a.performAction(currentState, params);
			agent.giveReward(((POMDPState)currentState).getReward());
		} else {
			ANSIColor.purple("[GoalsEnvironment.perform()] Agent requested to perform an action impossible in the current state");
		}
	}

	public State getCurrentState() {
		return currentState;
	}
}
