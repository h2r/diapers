package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.athena.namespace.S;
import edu.brown.h2r.diapers.athena.namespace.P;

import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.Action;

public class DiaperEnvironment implements Environment {
	private State currentState;

	public DiaperEnvironment(State initialState) {
		currentState = initialState;
	}

	public Observation observe() {

		final String caregiverRealState = (String) currentState.getObject(S.OBJ_CAREGIVER).getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];

		return new Observation(new POMDPDomain(), "") {
			@Override public double getProbability(State s, GroundedAction a) {
				String state = (String) s.getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

				if(state.equals(caregiverRealState)) {
					return 1;
				}
				return 0;
			}
		};
	}

	public void perform(Action a, String[] params) {
		if(a.applicableInState(currentState, params)) {
			currentState = a.performAction(currentState, params);
		} else {
			System.err.println("Agent requested to perform an action which is not possible in the current state.");
		}
	}

	public State getCurrentState() {
		return currentState;
	}
}