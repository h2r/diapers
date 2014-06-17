package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.athena.namespace.S;
import edu.brown.h2r.diapers.athena.namespace.P;

import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.Action;

import java.util.List;
import java.util.ArrayList;

public class DiaperEnvironment implements Environment {
	private State currentState;
	private ObservationStyle obsStyle;

	public enum ObservationStyle {
		DETERMINISTIC, BLURRY
	}

	public DiaperEnvironment(State initialState) {
		currentState = initialState;
		obsStyle = ObservationStyle.DETERMINISTIC;
	}

	public void addAgent(Agent a) {
		return;
	}

	public void reset() {
		return;
	}

	public DiaperEnvironment(State initialState, ObservationStyle os) {
		currentState = initialState;
		obsStyle = os;
	}

	public void setObservationStyle(ObservationStyle os) {
		obsStyle = os;
	}

	public Observation observe() {

		String caregiverRealState = (String) currentState.getObject(S.OBJ_CAREGIVER).getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];

		switch(obsStyle) {
			case BLURRY:
				return getBlurryObsrevation(caregiverRealState);
			case DETERMINISTIC:
			default:
				return getDeterministicObservation(caregiverRealState);
		}
	}

	private Observation getDeterministicObservation(String caregiverState) {
		final String cs = caregiverState;

		return new Observation(new POMDPDomain(), "") {
			@Override public double getProbability(State s, GroundedAction a) {
				String state = (String) s.getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

				if(state.equals(cs) || state.substring(4).equals(cs)) {
					return 1;
				}
				return 0;
			}
		};
	}

	private Observation getBlurryObsrevation(String caregiverState) {	
		final String cs = caregiverState;
		List<String> selected = pickNStates(3);
		final String os1 = selected.get(0);
		final String os2 = selected.get(1);
		final String os3 = selected.get(2);

		return new Observation(new POMDPDomain(), "") {
			@Override public double getProbability(State s, GroundedAction a) {
				String state = (String) s.getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

				if(state.equals(cs) || state.substring(4).equals(cs)
					|| state.equals(os1) || state.substring(4).equals(os1)
					|| state.equals(os2) || state.substring(4).equals(os2) 
					|| state.equals(os3) || state.substring(4).equals(os3)) {
					return 0.25;
				}
				return 0;
			}
		};
	}

	private List<String> pickNStates(int n) {
		@SuppressWarnings("serial")
		List<String> states = new ArrayList<String>() {{
			add(S.OBJ_STATE_X); add(S.OBJ_STATE_A); add(S.OBJ_STATE_B);
			add(S.OBJ_STATE_C); add(S.OBJ_STATE_D); add(S.OBJ_STATE_E); add(S.OBJ_STATE_Y);
		}};
		List<String> returnValue = new ArrayList<String>();

		for(int i = 0; i < n; ++i) {
			int index = (int) (new java.util.Random().nextDouble() * states.size());
			returnValue.add(states.remove(index));
		}

		return returnValue;
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
