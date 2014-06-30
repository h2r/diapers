package edu.brown.h2r.diapers.testing;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.core.State;

public class Environment {
	private POMDPDomain domain;
	private POMDPState currentState;
	private RewardFunction reward;
	private double totalReward;
	
	public Environment(POMDPDomain d, RewardFunction r) {
		domain = d;
		reward = r;
		reset();
	}

	public Observation observe(GroundedAction a) {
		return domain.makeObservationFor(a, currentState);
	}

	public void perform(GroundedAction a) {
		this.perform(a.action, a.params);
	}
	
	public void perform(Action a, String[] params) {
		System.out.print("The agent chose to perform action " + a.getName() + " with parameters [");
		for(String param : params) {
			System.out.print(param + ",");
		}
		System.out.println("]");
		if(a.applicableInState(currentState, params)) {
			POMDPState sPrime = (POMDPState) a.performAction(currentState, params);
			totalReward += reward.reward(currentState, new GroundedAction(a, params), sPrime);
			currentState = sPrime;
		}
	}

	public POMDPState getCurrentState() {
		return currentState;
	}

	public void reset() {
		currentState = domain.sampleInitialState();
	}

	public double getTotalReward() {
		return totalReward;
	}
}
