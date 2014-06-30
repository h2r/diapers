package edu.brown.h2r.diapers.tiger;

import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.tiger.namespace.P;
import edu.brown.h2r.diapers.athena.Environment;
import edu.brown.h2r.diapers.athena.Agent;
import edu.brown.h2r.diapers.util.ANSIColor;

import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.Action;

import java.util.List;
import java.util.ArrayList;

public class TigerEnvironment implements Environment {
	private State currentState;
	private POMDPDomain domain = (POMDPDomain) new TigerDomain().generateDomain();
	private Agent agent;

	public TigerEnvironment(State initialState) {
		currentState = initialState;
	}

	public void addAgent(Agent a) {
		agent = a;
	}

	public void reset() {

			return;
	}

	public Observation observe() {
		String tigerRealState = (String) currentState.getObject(P.OBJ_TIGER).getStringValForAttribute(P.ATTR_TIGER_LOCATION);

		if(tigerRealState.equals(P.DOOR_LEFT)) {
			if(new java.util.Random().nextDouble() < 0.3) {
				return domain.getObservation(P.RIGHT_DOOR_OBSERVATION);
			} else {
				return domain.getObservation(P.LEFT_DOOR_OBSERVATION);
			}
		} else if(tigerRealState.equals(P.DOOR_RIGHT)) {
			if(new java.util.Random().nextDouble() < 0.3) {
				return domain.getObservation(P.LEFT_DOOR_OBSERVATION);
			} else {
				return domain.getObservation(P.RIGHT_DOOR_OBSERVATION);
			}
		}

		return null;
	}

	public void perform(Action a, String[] params) {
		if(a.applicableInState(currentState, params)) {
			ANSIColor.purple("[TigerEnvironment.perform()] ");
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
			System.err.println("Agent requested to perfom an action impossible in the current state");
		}
	}

	public State getCurrentState() {
		return currentState;
	}
}
