package edu.brown.h2r.diapers.athena;

/**
 * Agent is an abstract class which can be run on an environment
 */
public abstract class Agent {
	protected Environment environment;
	protected double totalReward;

	public Agent(Environment e) {
		environment = e;
	}

	public void giveReward(double reward) {
		totalReward += reward;
	}

	public double getReward() {
		return totalReward;
	}

	public void clearReward() {
		totalReward = 0;
	}

	public abstract void run();


}
