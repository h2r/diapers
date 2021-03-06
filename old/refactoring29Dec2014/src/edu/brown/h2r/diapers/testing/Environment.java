package edu.brown.h2r.diapers.testing;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.core.State;

public class Environment {
	protected POMDPDomain domain;
	protected POMDPState currentState;
	protected RewardFunction reward;
	protected double totalReward;
	protected double discountedReward;
	protected double discount = 0.95;
	protected int step = 0;
	
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
			double r = reward.reward(currentState, new GroundedAction(a, params), sPrime);
			totalReward += r;
			discountedReward += r*Math.pow(discount, step);
			System.out.println("The agent received reward " + r);
			currentState = sPrime;
			domain.visualizeState(currentState);
			step++;
		}
	}

	public POMDPState getCurrentState() {
		return currentState;
	}

	public void reset() {
		currentState = domain.sampleInitialState();
		domain.visualizeState(currentState);
	}

	public double getTotalReward() {
		return totalReward;
	}
	
	public double getDiscountedReward() {
		return discountedReward;
	}
	
	public int getSteps(){
		return step;
	}
}
