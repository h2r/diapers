package edu.brown.h2r.diapers.pomdp;

import burlap.oomdp.singleagent.GroundedAction;

public class HistoryElement {
	private Observation observation = null;
	private GroundedAction action = null;
	
	public HistoryElement(Observation o) {
		observation = o;
	}

	public HistoryElement(GroundedAction a) {
		action = a;
	}

	public Observation getObservation() {
		return observation;
	}

	public GroundedAction getAction() {
		return action;
	}

	@Override 
	public int hashCode() {
		if(action != null) {
			return action.action.getName().hashCode();
		} 

		if(observation != null) {
			return observation.getName().hashCode();
		}

		return 0;
	}
}