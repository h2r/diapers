package edu.brown.h2r.diapers.domain.easy_diaper;

import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;

public class RashDomainRewardFunction implements RewardFunction {
	public double reward(State s, GroundedAction a, State sprime) {

		return -1.0;

	}
}
