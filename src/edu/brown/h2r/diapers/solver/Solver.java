package edu.brown.h2r.diapers.solver;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.testing.Environment;
import edu.brown.h2r.diapers.testing.UserEnvironment;

import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.auxiliary.StateParser;

import java.util.Map;

public abstract class Solver {
	protected Environment environment;
	protected RewardFunction rewardFunction;
	protected POMDPDomain domain;
	protected StateParser sparse;
	protected boolean userMode = false;

	public void init(POMDPDomain d, RewardFunction r) {
		environment = new Environment(d, r);
		rewardFunction = r;
		domain = d;
	}

	public void init(POMDPDomain d, RewardFunction r, Map<String, Double> p) {
		init(d, r);
		setParams(p);
	}

	public void printEnvironmentType() {
		if(environment instanceof UserEnvironment) {
			System.out.println("USER");
		} else {
			System.out.println("NORMAL");
		}
	}

	protected boolean isSuccess(Observation o) {
		return domain.isSuccess(o);
	}

	protected boolean isTerminal(POMDPState s) {
		return domain.isTerminal(s);
	}

	public void userMode(StateParser s) {
		environment = new UserEnvironment(domain, rewardFunction, s);
		sparse = s;
		userMode = true;
	}

	public void setParams(Map<String, Double> params) { 
		return;
	}
	
	public double getReward(){
		return environment.getTotalReward();
	}
	
	public double getDiscountedReward(){
		return environment.getDiscountedReward();
	}

	public abstract void run();
}
