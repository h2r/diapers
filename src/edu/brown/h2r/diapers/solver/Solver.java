package edu.brown.h2r.diapers.solver;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.testing.Environment;

import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.core.TerminalFunction;

public abstract class Solver {
	protected Environment environment;
	protected RewardFunction rewardFunction;
	protected POMDPDomain domain;

	public void init(POMDPDomain d, RewardFunction r) {
		environment = new Environment(d, r);
		rewardFunction = r;
		domain = d;
	}

	protected boolean isSuccess(Observation o) {
		return domain.isSuccess(o);
	}

	protected boolean isTerminal(POMDPState s) {
		return domain.isTerminal(s);
	}

	public abstract void run();
}
