package edu.brown.h2r.diapers.pomdp;

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
}
