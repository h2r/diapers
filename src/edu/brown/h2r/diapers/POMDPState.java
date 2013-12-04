package edu.brown.h2r.diapers;

import burlap.oomdp.core.State;

public class POMDPState extends State {
	
	private Observation current_obs;

	public POMDPState() {
		super();
	}

	public POMDPState(State s) {
		super(s);
	}

	public void setObservation(Observation o) {
		this.current_obs = o;
	}

	public Observation getObservation() {
		return this.current_obs;
	}
}