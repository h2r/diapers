package edu.brown.h2r.diapers.athena;

public class AgentTester {

	private Agent agent;
	private Environment environment;
	private int trials;
	private double avgReward;

	public AgentTester(Agent a, Environment e, int t) {
		agent = a;
		environment = e;
		trials = t;
	}

	public void runTests() {
		agent.clearReward();
		environment.reset();
		for(int i = 0; i < trials; ++i) {
			agent.run();
			avgReward += agent.getReward();
		}
		avgReward /= trials;
		System.out.println("[AgentTester.runTests()] After " + trials + " runs, the agent received average reward " + avgReward);
	}
}
