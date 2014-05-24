package edu.brown.h2r.diapers.pomdp;

import burlap.oomdp.core.State;

public class POMDPState extends State {
	
	private Observation current_obs;
	private double current_reward;

	public POMDPState() {
		super();
	}

	public POMDPState(State s) {
		super(s);
	}

	public void setObservation(Observation o) {
		this.current_obs = o;
	}

	public void setReward(double r) {
		current_reward = r;
	}

	public Observation getObservation() {
		return this.current_obs;
	}

	public double getReward() {
		return this.current_reward;
	}
}
